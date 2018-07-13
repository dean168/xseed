import './welcome.component.scss';

import React, { Component } from 'react';

import { Link } from 'react-router-dom';

import { topping } from '../../components/topping/topping.component';
import Scrolling from '../../components/scrolling/scrolling.component';

@topping
export class Welcome extends Component {
    render() {
        return (
            <div className="container-fluid">
                <Scrolling>
                    <div className="xs-h3">
                        <Link to="/widget1">welcome</Link>
                    </div>
                </Scrolling>
            </div>
        )
    }
}