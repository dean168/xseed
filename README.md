# 子模块

## portal
```
演示模块，与前台页面对接，提供REST服务
```

# 规则和约束
## src/main/java/org/learning/*/service：接口包
## src/main/java/org/learning/*/service/support：接口的实现
## src/main/java/org/learning/*/controller：前端入口，spring mvc REST 风格
## 配置文件加载(放在这个目录下就会自动被加载了)
```
1. spring service: classpath*:/META-INF/*/service/*.service.xml
2. spring servlet: classpath*:/META-INF/*/servlet/*.servlet.xml
3. hibernate: classpath*:/META-INF/*/hibernate/*.hbm.xml
```
## spring bean全部用注解: @RestController, @Service

## dao操作使用全局的 IHibernateOperations
```
@Autowired
@Qualifier(IHibernateOperations.SERVICE_ID)
private IHibernateOperations hibernateOperations;
```
## 有事物的在service层，无事物的可以直接调用 IHibernateOperations 接口在 Controller 查

## REST 规则
```
1、使用标准 spring mvc REST，网上一搜就很多了
2、请求总入口: /api/*, 配在 src/main/webapp/WEB-INF/web.xml
3、按照四种：GET, POST, PUT, DELETE
```
# 准备工作
```
1. 代码和运行是jdk1.8，所以没有的就要去下载了
2. 下载gradle http://gradle.org/gradle-download/
3. 配置环境变量，类似 mvn 的一样
```
# 工程导入 eclipse
```
1. cd portal
2. gradle eclipse
3. 执行到这里就已经生成了 .project .classpath .settings，在eclipse导入即可
```
# 工程导入 idea
```
在 idea 中 import... 弹出框中选中 build.gradle 就可以了
```
# 本地开发

## 开发步骤
```
1. 在eclipse、idea、vim 里面修改文件
2. cd portal
3. gradle jettyRun
4. 浏览器 -> http://127.0.0.1:8080/
5. 回到步骤1
```
## 调试
```
1. 这么运行: gradle jettyRunDebug
2. 在 eclipse、idea 里面远程调试就可以了，默认端口：8000
3. 修改java文件：不修改方法名称不需要重启
4. 修改静态文件：实时生效
```

# angular2
## 参考 angular2/README.md

# react
## 参考 react/README.md

# vue
## 参考 vue/README.md