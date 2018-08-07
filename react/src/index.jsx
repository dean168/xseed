import 'babel-polyfill';
import './index.html';
import './styles.css';

import React, { Component } from 'react';
import { render } from 'react-dom';

import * as config from './config';

import Loading from './components/loading/loading.component';
import { routes } from './routes';


class App extends Component {

    constructor(props) {
        super(props);
        this.state = { prepared: true };
    }

    render() {
        return (
            this.state.prepared ? (
                <div>
                    <Loading subject={config.components.loading.subject}  />
                    {routes()}
                </div>
            ) : (
                    <div>
                        <Loading subject={config.components.loading.subject}  />
                    </div>
                )
        );
    }
}

render(<App />, document.getElementById('app'));
