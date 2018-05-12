import { PortalLayout } from './portal.layout';
import { Widget1Component } from './widget1/widget1.component';


export const routes = [{
    path: 'portal',
    component: PortalLayout,
    children: [
        { path: 'welcome', component: Widget1Component },
        { path: '**', redirectTo: 'welcome' }
    ]
}]