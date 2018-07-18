import 'babel-polyfill';
import './index.html';
import './styles.css';

import React, { Component } from 'react';
import { render } from 'react-dom';

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
                    <Loading status={false} />
                    {routes()}
                </div>
            ) : (
                    <div>
                        <Loading status={true} />
                    </div>
                )
        );
    }
}

render(<App />, document.getElementById('app'));
