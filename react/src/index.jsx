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
            <div>
                { this.state.prepared ? routes() : undefined}
                <Loading observable={config.components.loading.observable} />
            </div>
        );
    }
}

render(<App />, document.getElementById('app'));
