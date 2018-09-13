import React from 'react';
import ReactDOM from 'react-dom';

import $ from 'jquery';


export default class ScrollingComponent extends React.Component {

    static defaultProps = {
        component: 'div',
        transitionName: 'fadeIn',
        className: '',
        pollInterval: 0,
        threshold: 0,
        delayTimeMillis: 0
    }

    constructor(props) {
        super(props);
        this.state = { animated: false };
    }

    componentWillMount = () => (this.timer = window.setTimeout(() => !this.animating() && $(window).on('scroll.scrolling', this.scrolled), this.props.delayTimeMillis))

    scrolled = () => {
        this.timer && (window.clearTimeout(this.timer));
        this.timer = window.setTimeout(() => { (delete this.timer) && this.animating() }, this.props.pollInterval);
    }

    animating = () => {
        !this.sael && (this.sael = ReactDOM.findDOMNode(this));
        if (this.enteringViewport(this.sael)) {
            this.awillUnmount();
            this.setState({ animated: true });
            return true;
        } else {
            return false;
        }
    }

    enteringViewport = (element) => {
        let threshold = this.props.threshold;
        let offset = $(element).offset();
        let top = offset.top + $(element).scrollTop();
        let bottom = $(window).scrollTop() + $(window).height();
        return top < (bottom - threshold);
    }

    componentWillUnmount = () => {
        !this.state.animated && this.awillUnmount();
    }

    awillUnmount = () => {
        (this.timer && window.clearTimeout(this.timer)) || (delete this.timer) && (delete this.sael);
        $(window).off('scroll.scrolling');
    }

    render() {
        const className = 'animated' + (this.state.animated ? ' bc-opacity1 ' + this.props.transitionName : ' bc-opacity0') + (this.props.className ? ' ' + this.props.className : '');
        return React.createElement(this.props.component, { className: className }, this.props.children);
    }
}