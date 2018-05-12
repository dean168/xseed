import { Routes, RouterModule } from "@angular/router";
import { ModuleWithProviders } from "@angular/core";

import { routes as PortalRoutes } from './portal/portal.routing';

import { LoginComponent } from "./login/login.component";


export const routes: Routes = [
    ...PortalRoutes,
    { path: 'login', component: LoginComponent },
    { path: '**', redirectTo: 'portal' }
];


export const routing: ModuleWithProviders = RouterModule.forRoot(routes, { useHash: true });