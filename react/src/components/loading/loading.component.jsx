import React, { Component } from 'react';

import Icon from 'antd/lib/icon';
import 'antd/lib/icon/style/css';

import * as config from '../../config';
import { observable } from '../../providers/subject.provider';

import './loading.component.scss';


export default class Loading extends Component {

    state = { queues: [] }

    componentWillMount() {
        this.subscription = observable(this.props.subject).subscribe(status => {
            status => {
                if (status.type == 'push') {
                    this.state.queues.push(status);
                } else if (status.type == 'pop') {
                    this.state.queues.pop();
                } else {
                    throw 'undefined type: ' + status.type;
                }
            }
        });
    }

    componentWillUnmount() {
        this.subscription && this.subscription.unsubscribe();
    }

    render() {
        let message = this.state.queues.length > 0 ? this.state.queues[this.state.queues.length - 1].message || config.components.loading.message : config.components.loading.message;
        return (
            <div className={'xs-loading' + (this.state.queues.length > 0 ? ' xs-block' : ' xs-hide')}>
                <div className="xs-icon">
                    <Icon type="loading" title={message} />
                    {message}
                </div>
            </div>
        )
    }
}