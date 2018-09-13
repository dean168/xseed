import React from 'react';

import { observable, next } from '../../providers/subject.provider';

import './mask.component.scss';


export default class MaskComponent extends React.Component {

    state = { visible: false }

    componentWillMount() {
        this.props.observable && (this.subscription = observable(this.props.observable).subscribe(status => this.setState({ visible: status.visible })));
    }

    componentWillUnmount() {
        this.subscription && this.subscription.unsubscribe();
    }

    render() {
        let visible = this.props.observable ? this.state.visible : this.props.visible;
        return (
            <div onClick={() => next(this.props.subject, { type: 'click' })} className={'bc-mask' + (visible ? ' bc-block' : ' bc-hide')}>
            </div>
        )
    }
}