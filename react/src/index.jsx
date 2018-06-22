import 'babel-polyfill';
import './index.html';
import './styles.css';

import React, { Component } from 'react';
import ReactDOM, { render } from 'react-dom';

import { routes } from './routes';
import { Loading } from './components/loading/loading.component';

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
