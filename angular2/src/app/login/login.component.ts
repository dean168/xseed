import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';


@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent {

    constructor(private router: Router) {
    }

    ngOnInit() {
    }

    login() {
        // TODO 在这里登录，这里直接就等于登录了
        this.router.navigate(['/portal']);
    }

    ngOnDestroy() {
    }
}