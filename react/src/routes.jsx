import React, { Component } from 'react';
import { HashRouter as Router, Switch, Route } from 'react-router-dom';

import { Welcome } from './portal/welcome/welcome.component';
import { Widget1 } from './portal/widget1/widget1.component';

export const routes = () => (
    <Router>
        <Switch>
            <Route path='/' component={Welcome} exact />
            <Route path='/widget1' component={Widget1} />
        </Switch>
    </Router>
)