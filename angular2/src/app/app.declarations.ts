import { LoadingComponent } from './components/loading/loading.component';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { LoginComponent } from './login/login.component';
import { PortalLayout } from './portal/portal.layout';
import { Widget1Component } from './portal/widget1/widget1.component';

export const declarations = [
    // 公共的组件
    LoadingComponent,
    // 公共的指令
    // 框架的组件
    AppComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    // 业务的组件
    PortalLayout,
    Widget1Component
];