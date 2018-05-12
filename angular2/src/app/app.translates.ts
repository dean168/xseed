import { HttpClient } from "@angular/common/http";
import { TranslateHttpLoader } from "@ngx-translate/http-loader";
import { TranslateModule, TranslateLoader } from "@ngx-translate/core";


export function factory(httpclient: HttpClient) { return new TranslateHttpLoader(httpclient, './assets/i18n/', '.json') };

export const translates = TranslateModule.forRoot({
    loader: {
        provide: TranslateLoader,
        useFactory: factory,
        deps: [HttpClient]
    }
});