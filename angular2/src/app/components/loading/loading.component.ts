import { Component } from '@angular/core';
import { Subscription } from 'rxjs';

import { SubjectProvider } from '../../providers/subject.provider';


@Component({
    selector: 'loading-component',
    templateUrl: './loading.component.html',
    styleUrls: ['./loading.component.css']
})
export class LoadingComponent {

    state = { queues: [] }

    subscription: Subscription;

    constructor(private subject: SubjectProvider) {
    }

    ngOnInit() {
        this.subscription = this.subject.observable('app.components.loading.queues').subscribe(
            queue => {
                if (queue.type == 'push') {
                    this.state.queues.push(queue);
                } else if (queue.type == 'pop') {
                    this.state.queues.pop();
                } else {
                    throw 'undefined type: ' + queue.type;
                }
            }
        )
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}