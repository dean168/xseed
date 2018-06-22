import './welcome.component.scss';

import React, { Component } from 'react';

import { Link } from 'react-router-dom';


export class Welcome extends Component {
    render() {
        return (
            <div className="container-fluid">
                <div className="xs-h3">
                    <Link to="/widget1">welcome</Link>
                </div>
            </div>
        )
    }
}