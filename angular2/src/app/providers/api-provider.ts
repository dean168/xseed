import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';

import { SubjectProvider } from './subject-provider';

import { apiURL } from '../app.config';


@Injectable()
export class ApiProvider {

    private csrf = { name: '', token: '' };

    constructor(private httpclient: HttpClient, private translate: TranslateService, private subject: SubjectProvider) {
    }

    exchange(options) {
        return new Promise((resolve, reject) => {
            this.options(options).then(
                (status: any) => status.errcode ? resolve(status.data) : alert(status.message),
                error => reject(error)
            )
        });
    }

    options(options) {
        return new Promise((resolve, reject) => {
            this.translate.get('commons.loading.content').subscribe(message => {
                let animate = (options.animate == undefined || options.animate) ? this.subject.subject('app.components.loading.queues') : undefined;
                animate && animate.next({ type: 'push', content: message });
                options.url = apiURL(options.url);
                options.headers = new HttpHeaders();
                options.headers.append(this.csrf.name, this.csrf.token);
                this.httpclient.request(options.method, options.url, options).subscribe(
                    res => {
                        animate && animate.next({ type: 'pop' });
                        resolve(res);
                    },
                    error => {
                        animate && animate.next({ type: 'pop' });
                        options.errorText ? this.translate.get(options.errorText).subscribe(message => alert(message)) : reject(error);
                    }
                )
            })
        });
    }
}