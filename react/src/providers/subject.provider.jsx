import { Subject, Observable } from 'rxjs';

let subjects = {}, observables = {};

export const subject = (name) => {
    if (typeof name == 'string') {
        return subjects[name] || (subjects[name] = new Subject());
    } else {
        throw 'subject name must not be null';
    }
}

export const observable = (name) => {
    if (typeof name == 'string') {
        return observables[name] || (observables[name] = subject(name).asObservable());
    } else {
        throw 'observable name must not be null';
    }
}

export const next = (name, options) => {
    if (name) {
        if (typeof name == 'string') {
            subject(name).next(options);
        } else if (typeof name == 'function') {
            name(options);
        } else {
            throw 'undefined on#' + name;
        }
    }
}