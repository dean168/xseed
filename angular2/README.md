# angular2-seed
## 学习资料
* https://www.angular.cn/docs

## nodejs 安装
```
curl -O https://nodejs.org/dist/v8.11.1/node-v8.11.1-win-x64.zip
export NODE_HOME
```

## 设置仓库和代理 (可直连外网的不需要设置)
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
ng new angular2-seed
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