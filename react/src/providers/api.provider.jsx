import $ from 'jquery';

import message from 'antd/lib/message';
import 'antd/lib/message/style/css';

import * as config from '../config';
import { subject } from './subject.provider';


export const exchange = (options) => {
    return new Promise((resolve, reject) => {
        fetch(options).then(
            status => status.code ? resolve(status.data) : message.error(status.message),
            error => (options.errorText && message.error(options.errorText)) || reject(error)
        );
    });
}

export const fetch = (options) => {
    let animate = _animate(options.animate);
    animate = animate ? subject(animate.observable) : undefined;
    animate && animate.next({ type: 'push', content: animate.message });
    options = _options(options);
    return new Promise((resolve, reject) => {
        $.ajax(options).done(status => (animate && animate.next({ type: 'pop' })) || resolve(status)).fail(error => (animate && animate.next({ type: 'pop' })) || reject(error));
    });
}

export const _animate = (animate) => {
    if (animate == undefined || (typeof animate == 'boolean' && animate)) {
        animate = { observable: config.components.loading.observable, message: config.components.loading.message };
    } else if (typeof animate == 'string') {
        animate = { observable: animate, message: config.components.loading.message };
    }
    animate && !animate.observable && (animate.observable = config.components.loading.observable);
    animate && !animate.message && (animate.message = config.components.loading.message);
    return animate;
}

export const _options = (options) => {
    options.url = config.apiURL(options.url);
    if (!options.contentType) {
        options.contentType = 'application/json;charset=UTF-8';
        options.data && (options.data = JSON.stringify(options.data));
    }
    !options.timeout && (options.timeout = config.ajax.timeout);
    !options.dataType && (options.dataType = 'json');
    return options;
}