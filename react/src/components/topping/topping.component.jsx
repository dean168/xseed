import React from 'react';

import $ from 'jquery';


export let topping = ComponsedComponent => class extends React.Component {

    componentDidMount = () => {
        $('body,html').scrollTop(0);
    }

    render() {
        return <ComponsedComponent {...this.props} {...this.state} />
    }
}