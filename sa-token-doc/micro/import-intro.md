
# 微服务中使用Sa-Token 依赖引入说明 

--- 

虽然在 [开始] 章节已经说明了依赖引入规则，但是交流群里不少小伙伴提出bug解决到最后发现都是因为依赖引入错误导致的，此处再次重点强调一下：

> [!TIP| style:callout] 
> **在微服务架构中使用Sa-Token时，网关和内部服务要分开引入Sa-Token依赖（不要直接在顶级父pom中引入Sa-Token）**

总体来讲，我们需要关注的依赖就是两个：`sa-token-spring-boot-starter` 和 `sa-token-reactor-spring-boot-starter`：

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
注：如果你使用的是 `SpringBoot 3.x`，只需要将 `sa-token-spring-boot-starter` 修改为 `sa-token-spring-boot3-starter` 即可。
``` xml 
<!-- Sa-Token 权限认证，在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
注：如果你使用的是 `SpringBoot 3.x`，只需要将 `sa-token-spring-boot-starter` 修改为 `sa-token-spring-boot3-starter` 即可。
``` gradle
// Sa-Token 权限认证，在线文档：https://sa-token.cc
implementation 'cn.dev33:sa-token-spring-boot-starter:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Maven 方式 -------->
注：如果你使用的是 `SpringBoot 3.x`，只需要将 `sa-token-reactor-spring-boot-starter` 修改为 `sa-token-reactor-spring-boot3-starter` 即可。
``` xml 
<!-- Sa-Token 权限认证（Reactor响应式集成），在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-reactor-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
注：如果你使用的是 `SpringBoot 3.x`，只需要将 `sa-token-reactor-spring-boot-starter` 修改为 `sa-token-reactor-spring-boot3-starter` 即可。
``` gradle
// Sa-Token 权限认证（Reactor响应式集成），在线文档：https://sa-token.cc
implementation 'cn.dev33:sa-token-reactor-spring-boot-starter:${sa.top.version}'
```
<!---------------------------- tabs:end ------------------------------>


至于怎么分辨我们需要引入哪个呢？这个要看你使用的基础框架：

对于内部基础服务来讲，我们一般都是使用SpringBoot默认的web模块：SpringMVC，
因为这个SpringMVC是基于Servlet模型的，在这里我们需要引入的是`sa-token-spring-boot-starter`

对于网关服务，大体来讲分为两种：
- 一种是基于Servlet模型的，如：Zuul，我们需要引入的是：`sa-token-spring-boot-starter`，详细戳：[在SpringBoot环境集成](/start/example)；理论上`Zuul`并不支持`Spring Boot3`
- 一种是基于Reactor模型的，如：SpringCloud Gateway、ShenYu 等等，我们需要引入的是：`sa-token-reactor-spring-boot-starter`，**并且注册全局过滤器！**，详细戳：[在WebFlux环境集成](/start/webflux-example)

注：切不可直接在一个项目里同时引入这两个依赖，否则会造成项目无法启动

另外，我们需要引入Redis集成包，因为我们的网关和子服务主要通过Redis来同步数据 

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 整合 Redis （使用 jackson 序列化方式） -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-redis-jackson</artifactId>
	<version>${sa.top.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 整合 Redis （使用 jackson 序列化方式）
implementation 'cn.dev33:sa-token-redis-jackson:${sa.top.version}'
implementation 'org.apache.commons:commons-pool2'
```
<!---------------------------- tabs:end ------------------------------>

详细参考：[集成 Redis](/up/integ-redis)







