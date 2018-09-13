import React from 'react';

import Icon from 'antd/lib/icon';
import 'antd/lib/icon/style/css';

import * as config from '../../config';
import { observable } from '../../providers/subject.provider';

import './loading.component.scss';


export default class LoadingComponent extends React.Component {

    state = { queues: [] }

    componentWillMount() {
        this.subscription = observable(this.props.observable).subscribe(status => {
            if (status.type == 'push') {
                this.state.queues.push(status);
            } else if (status.type == 'pop') {
                this.state.queues.pop();
            } else {
                throw 'undefined type: ' + status.type;
            }
            this.setState({ queues: this.state.queues });
        });
    }

    componentWillUnmount() {
        this.subscription && this.subscription.unsubscribe();
    }

    render() {
        let message = this.state.queues.length > 0 ? this.state.queues[this.state.queues.length - 1].message || config.components.loading.message : config.components.loading.message;
        return (
            <div className={'bc-loading' + (this.state.queues.length > 0 ? ' bc-block' : ' bc-hide')}>
                <div className="bc-icon">
                    <Icon type="loading" title={message} />
                    {message}
                </div>
            </div>
        )
    }
}