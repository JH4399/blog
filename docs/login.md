# 用户登录与 Redis 公共操作

## 配置

已引入 `spring-boot-starter-data-redis`，使用 Lettuce 和 `StringRedisTemplate`，由 Spring Boot 管理版本。
启动后端前请准备 Redis，默认连接 `localhost:6379`、数据库 0、无密码。
可通过环境变量覆盖，不需要 JWT 密钥：

| 环境变量 | 默认值 | 含义 |
| --- | --- | --- |
| REDIS_HOST | localhost | Redis 地址 |
| REDIS_PORT | 6379 | Redis 端口 |
| REDIS_USERNAME | 空 | Redis ACL 用户名，可选 |
| REDIS_PASSWORD | 空 | Redis 密码，可选 |
| REDIS_DATABASE | 0 | Redis 数据库编号 |
| LOGIN_TOKEN_TTL_SECONDS | 7200 | Token 固定有效期，单位秒，必须至少为 1 |

例如 PowerShell 中设置当前终端的环境变量后启动（实际地址及密码自行替换）：

```powershell
$env:REDIS_HOST = '127.0.0.1'
$env:REDIS_PORT = '6379'
$env:LOGIN_TOKEN_TTL_SECONDS = '7200'
mvn spring-boot:run
```

Redis 不可用时，登录、认证和注销均返回 HTTP 503，不会签发未存储的 Token，
也不会绕过认证或返回虚假的退出成功。

## 登录

`POST /api/user/login`，JSON 请求体：

```json
{"username":"alice","password":"Password123!"}
```

成功返回 HTTP 200（user 实际包含完整 UsersVO 字段，不含密码或密码摘要）：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "<43个字符的随机Token>",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "user": {"id": 1, "username": "alice", "role": 1, "status": 1}
  }
}
```

用户名不能为空、最多 50 字符且不含空白；密码不能为空，最多 72 字符、UTF-8 最多 72 字节。
密码原样验证，不会 trim。登录不对已有密码重新施加注册时的最小长度要求。
参数错误返回 400；用户名不存在、密码错误统一返回 401「用户名或密码错误」；
密码正确但账户被禁用返回 403「账号已被禁用」。

Token 使用 SecureRandom 生成 32 字节（256 bit），采用无填充 Base64URL 编码。
Redis 通过一次 SET NX + TTL 原子写入：

```text
Key:   login:token:<Token 原文的 SHA-256 十六进制摘要>
Value: 用户 ID 的字符串
TTL:   默认 7200 秒
```

Token 原文仅返回给客户端，不写入数据库或 Redis。读取登录态不会续期，过期需重新登录。
每次登录生成独立 Token，允许多个设备同时登录，不创建 HttpSession。
Redis 登录记录丢失会要求用户重新登录；禁用或删除账户后，下一次认证会拒绝访问并撤销该 Token。

## 使用登录态

前端可以按当前约定将 `accessToken` 保存到 LocalStorage，并通过请求头发送：

```http
Authorization: Bearer <accessToken>
```

Swagger／Knife4j 文档已声明 BearerAuth。点击授权按钮并填写 Token 原文，
即可调试当前用户和退出接口；不要重复填写 `Bearer ` 前缀。

`GET /api/user/me` 返回 `ApiResponse<UsersVO>`，每次认证都从数据库读取最新状态、角色和资料。
缺少、格式错误、不存在、已过期或已撤销的 Token 返回 401；禁用账户返回 403。
登录和受保护接口的响应带 `Cache-Control: no-store`。

新增需要登录的 `/api/**` Controller 方法或类时，标注 `@LoginRequired`。
`LoginInterceptor` 验证令牌，将用户资料放入本次请求属性中：

```java
@LoginRequired
@GetMapping("/example")
public ApiResponse<UsersVO> example(
        @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO currentUser) {
    return ApiResponse.success(currentUser);
}
```

默认只有 `me`、`logout` 标注了该注解，注册、登录、接口文档及未标注的接口公开访问。
此注解仅检查登录，管理员权限须另行验证，不能信任客户端提交的用户 ID 或角色。

## 退出登录

`POST /api/user/logout`，携带同一个 Authorization 请求头，不需要请求体。
删除 Redis 中当前 Token 的登录记录，返回：

```json
{"code":200,"message":"退出登录成功","data":null}
```

该 Token 随即无法发起新的认证请求，其他设备 Token 不受影响。
已经通过认证、正在执行的请求不会被中断。前端退出后同时清除本地 Token；
若令牌已经失效，接口返回 401，前端同样清理本地状态。

## 公共 Redis 封装

其他业务通过构造器注入 `RedisService`，不重复创建连接或操作模板。
统一以字符串序列化 key 和 value，不使用 Java 原生对象序列化；对象由调用方先显式转换为 JSON。

| 类别 | 方法 |
| --- | --- |
| 字符串 | set（含带 TTL 重载）、get、setIfAbsent（NX + TTL） |
| key | delete（单个／批量）、hasKey、expire、getExpire |
| 计数器 | increment（正数增加，负数减少） |
| Hash | hashPut、hashGet、hashEntries、hashDelete |
| List | listRightPush、listRange、listLeftPop |
| Set | setAdd、setMembers、setRemove |

`getExpire` 单位为秒，-2 表示 key 不存在，-1 表示没有过期时间。
TTL 必须为正且至少为 1 毫秒；带 TTL 的 set／setIfAbsent 原子写入。
普通 set 会覆盖旧 TTL；集合及新建计数器默认无 TTL，需要调用方显式设置。
已有计数器 increment 保留 TTL。Redis 操作异常直接传播，由业务层决定响应策略，
TokenService 将数据访问异常转换为统一的 503 业务响应。

## 验证

```powershell
mvn test
```

默认测试使用 H2，登录测试仅模拟 RedisService 边界，不访问业务数据库和真实 Redis。
覆盖注册→登录→查询当前用户→退出→拒绝旧 Token、错误密码、账户禁用／删除、
密码字节边界、最新角色、多设备登录、Redis 故障、Token 冲突重试和损坏的缓存值。
Redis 过期在默认 HTTP 测试中以 GET 返回空模拟。

准备好独立测试 Redis 后，可额外运行真实 Redis 验证，检查序列化、公共操作、实际 TTL 过期及撤销：

```powershell
mvn test '-Dtest=RedisIntegrationTests' '-Dblog.test.redis=true' '-Dblog.test.redis.host=127.0.0.1' '-Dblog.test.redis.port=6379'
```

可用 `-Dblog.test.redis.database=0` 指定数据库；ACL／密码沿用 REDIS_USERNAME／REDIS_PASSWORD 环境变量。
测试只清理自身创建的 key，不执行 FLUSHDB；未显式开启时跳过真实 Redis 测试。
