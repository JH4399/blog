# 用户注册

接口：`POST /api/user/register`，请求体为 JSON。

```json
{
  "username": "alice",
  "password": "Password123!",
  "nickname": "小爱",
  "email": "alice@example.com"
}
```

用户名必填，最多 50 个字符，不能包含空白。密码必填，8 至 72 个字符，
UTF-8 编码最多 72 字节。昵称、邮箱、头像 `avatar`、简介 `bio` 可选。

成功返回 HTTP 201 和 `ApiResponse<UsersVO>`，用户数据位于 `data` 中，含数据库生成的 ID，不含密码。
参数错误返回 400，用户名重复或插入时违反唯一约束返回 409。
所有返回体统一为 `code`（与 HTTP 状态一致）、`message`（提示）、`data`（业务数据）。
业务错误的 `data` 为 null，字段校验错误的 `data` 为字段名到提示信息的映射。

```json
{"code": 409, "message": "用户名已存在", "data": null}
```

其他接口可使用 `ApiResponse.success(data)` 或 `ApiResponse.success("操作成功", data)` 返回 HTTP 200 结果，
使用 `ApiResponse.success()` 返回无数据的成功结果。创建资源时使用 `ApiResponse.created(data)`，
并在 Controller 设置 HTTP 201。业务失败建议抛出 `BusinessException`，由全局异常处理器设置 HTTP 状态和返回体。
`role`、`status`、`id` 等服务端字段不在注册 DTO 中，客户端提交不会改变账户权限。

链路：`UserController` → `UserService` / `UserServiceImpl` → `UserMapper`。
使用 MyBatis-Plus 的 `BaseMapper` 完成查询和插入，BCrypt 加密密码，事务保证注册原子性。
Mapper 按表结构截图显式映射 `"BLOG"."users"` 及小写列名。

数据库必须为 `users.username` 建立唯一约束或唯一索引，保证并发注册时用户名唯一。
如果尚未建立，可在确认不存在重复用户名后执行
`src/main/resources/db/users-username-unique.sql`；该脚本不会自动执行。

运行 `mvn test` 验证注册链路。测试使用独立 H2 内存库，不写入配置中的达梦数据库。
H2 测试覆盖 HTTP 参数校验、MyBatis-Plus SQL、生成主键、字段映射、密码加密、
越权字段过滤、重复用户名；单元测试覆盖预查询后出现唯一键冲突的分支。
