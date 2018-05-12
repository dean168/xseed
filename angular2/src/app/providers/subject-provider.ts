import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';


@Injectable()
export class SubjectProvider {

    private subjects;
    private observables;

    constructor() {
        !window['xseed'] && (window['xseed'] = {});
        !window['xseed']['subjects'] && (window['xseed']['subjects'] = {});
        this.subjects = window['xseed']['subjects'];
        !window['xseed']['observables'] && (window['xseed']['observables'] = {});
        this.observables = window['xseed']['observables'];
    }

    subject(name: string): Subject<any> {
        return this.subjects[name] || (this.subjects[name] = new Subject<any>());
    }

    observable(name: string): Observable<any> {
        return this.observables[name] || (this.observables[name] = this.subject(name).asObservable());
    }
}