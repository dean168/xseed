import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

import * as $ from 'jquery';


@Injectable()
export class ParamsProvider {

    private params;

    constructor() {
        !window['xseed'] && (window['xseed'] = {});
        !window['xseed']['params'] && (window['xseed']['params'] = {});
        this.params = window['xseed']['params'];
    }

    put(key, value) {
        typeof key == 'string' ? (this.params[key] = value) : $.extend(this.params, key);
        return this;
    }

    get(key) {
        return this.params[key];
    }

    take(key) {
        let value = this.get(key);
        this.remove(key);
        return value;
    }

    remove(key) {
        delete this.params[key];
        return this;
    }
}