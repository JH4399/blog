-- 仅在 users.username 尚无唯一约束或唯一索引时执行。
-- 执行前需先处理已有的重复用户名；应用不会自动执行本文件。
-- 此索引用于保证多个注册请求并发时仍不会出现重复用户名。
CREATE UNIQUE INDEX "BLOG"."uk_users_username" ON "BLOG"."users" ("username");
