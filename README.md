# 子模块

## portal
```
门户模块，与前台页面对接，提供REST服务
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
## 学习资料
* https://www.angular.cn/docs

## nodejs 安装
```
wget https://nodejs.org/dist/v8.11.1/node-v8.11.1-win-x64.zip && export NODE_HOME
```

## 设置仓库和代理
```
npm config delete proxy && npm config delete http-proxy && npm config delete https-proxy && npm config delete strict-ssl && npm config delete registry
npm config set registry http://registry.npm.taobao.org
npm config set strict-ssl false
export HTTP_PROXY=""
export HTTPS_PROXY=""
export NO_PROXY=""
export NODE_TLS_REJECT_UNAUTHORIZED=0
```

## 初始化项目(不需要执行)
```
npm cache clean -f
ng new angular2
npm install --save @ngx-translate/core @ngx-translate/http-loader bootstrap jquery
```

## 初始化环境(只需要执行一次)
```
npm install -g @angular/cli
cd angular2 && npm install
```

## 启动
```
cd angular2 && npm start
http://127.0.0.1:4200
```

## 开发工具
```
建议使用 vscode
```

## 已引入
```
bootstrap
jquery
```

## 公共的 css 路径
* angular2/src/styles.css

## 图片存放路径
* angular2/src/assets/images

## 字体存放路径
* angular2/src/assets/fonts

## 公共组件存放路径
* angular2/src/app/components

## 公共的指令存放路径
* angular2/src/app/directives.ts

## 新建的组件需要在这里声明
* angular2/src/app/app.declarations.ts

## 新建的模块路由需要在这里声明
* angular2/src/app/app.routing.ts


# react
## 初始化项目(不需要执行)
```
npm install autoprefixer babel-cli babel-core babel-loader babel-plugin-rewire babel-plugin-transform-class-properties babel-plugin-transform-decorators-legacy babel-plugin-transform-runtime babel-polyfill babel-preset-env babel-preset-flow babel-preset-react babel-preset-stage-0 babel-runtime consolidate copy-webpack-plugin cross-env css-loader extract-text-webpack-plugin file-loader html-loader json-loader lodash node-sass postcss-loader react-hot-loader sass-loader style-loader template-html-loader webpack webpack-cli webpack-dev-server --save-dev
npm install antd bootstrap react react-dom react-router react-router-dom jquery --save
```