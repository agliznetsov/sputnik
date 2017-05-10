import React from 'react';
import '../node_modules/font-awesome/css/font-awesome.css';
import './styles/bootstrap-spacelab.min.css';
import './styles/main.css';
import './styles/notification.css';
import logo from './assets/sputnik-logo.png'

import notificationService from './services/NotificationService';
import userService from './services/UserService';
import Notifications from './components/Notifications';
import Signin from './components/Signin';
import DataSources from './components/DataSources';

import {HashRouter as Router, Route, Link, NavLink} from 'react-router-dom'

const Home = () => (
    <div className="app-body">
        <h1>Home</h1>
    </div>
);


class App extends React.Component {

    componentDidMount() {
        userService.subscribe(() => this.forceUpdate());
    }

    render() {
        return (
            <div>
                <Notifications/>
                {this.menu()}
            </div>
        );
    }

    signout(e) {
        e.preventDefault();
        userService.signout();
    }

    menu() {
        const mainMenu = [
            {
                name: 'Sources',
                url: '/sources',
                icon: 'fa-database'
            },
            {
                name: 'Profiles',
                url: '/profiles',
                icon: 'fa-cogs'
            }
        ];

        return (
            <Router>
                <div>
                    <nav className="navbar navbar-default navbar-fixed-top">
                        <div>
                            <div className="navbar-header">
                                <a className="navbar-brand" href="#"><img src={logo}/></a>
                            </div>
                            <div id="navbar" className="navbar-collapse collapse">
                                <ul className="nav navbar-nav navbar-right">
                                    {mainMenu.map((it) => this.menuItem(it))}
                                    {this.menuUser()}
                                </ul>
                            </div>
                        </div>
                    </nav>
                    <Route exact path="/" component={Home}/>
                    <Route path="/sources" component={DataSources}/>
                    <Route path="/signin" component={Signin}/>
                </div>
            </Router>
        )
    }

    menuUser() {
        return userService.getUser() ?  (
            <li className="btn-link">
                <a title="Sign out" onClick={(e) => this.signout(e)}>
                    <i className="fa fa-lg fa-power-off"></i>
                    <span className="li-text">{userService.getUser()}</span>
                </a>
            </li>
        ) : (
            <li>
                <Link to="/signin">Sign in</Link>
            </li>
        );
    }

    menuItem(it) {
        return (
            <li key={it.url}>
                <NavLink to={it.url} activeClassName="active">
                    <i className={`fa fa-lg ${it.icon}`}></i>
                    <span className="li-text">{it.name}</span>
                </NavLink>
            </li>
        )
    }

}

export default App;
