import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';

import { LANGUAGES, LANGUAGE_DEFAULT, LANGUAGE_STORAGE_KEY } from './app.config';
import { ApiProvider } from './providers/api.provider';

import * as $ from 'jquery'


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private translate: TranslateService, private api: ApiProvider, private router: Router) {
    // translate
    this._translate();
    // session
    this._session();
  }

  _translate() {
    this.translate.addLangs(LANGUAGES);
    this.translate.setDefaultLang(LANGUAGE_DEFAULT);
    let language = localStorage.getItem(LANGUAGE_STORAGE_KEY);
    if (!language) {
      let userLang = this.translate.getBrowserLang(), index = $.inArray(userLang, LANGUAGES);
      language = index != -1 ? LANGUAGES[index] : LANGUAGE_DEFAULT;
    }
    this.translate.use(language);
    localStorage.setItem(LANGUAGE_STORAGE_KEY, language);
  }

  _session() {
    this.api.options({ method: 'get', url: 'account/session', errorText: 'commons.http.error.unknown' }).then(
      (status: any) => {
        if (status.errcode) {
          (!this.router.url.length || this.router.url == '/') && this.router.navigate(['/portal']);
        } else {
          this.router.navigate(['/login']);
        }
      }
    )
  }
}
