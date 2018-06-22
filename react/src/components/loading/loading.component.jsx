import './loading.component.scss';

import React, { Component } from 'react';

export class Loading extends Component {
    render() {
        return (
            <div className={'xs-loading' + (this.props.status ? 'xs-block' : 'xs-hide')}>
                <div className="xs-icon">
                    {/* <img src="images/loading.gif"></img> */}
                </div>
            </div>
        )
    }
}