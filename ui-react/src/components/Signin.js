import React from 'react';
import userService from '../services/UserService';

class Signin extends React.Component {

    constructor() {
        super();
        this.state = {};
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.state[name] = value;
        this.forceUpdate();
    }

    render() {
        return (
            <form className="signin-form center" data-ng-controller="SigninController">
                <div className="form-group">
                    <input className="form-control" type="text" placeholder="Anonymous" name="username" value={this.state.username}
                           onChange={this.handleInputChange}/>
                </div>
                <div className="form-group">
                    <input className="form-control" type="password" placeholder="password" name="password" value={this.state.password}
                           onChange={this.handleInputChange} />
                </div>
                <div className="form-group">
                    <button className="btn btn-primary" onClick={(e) => this.clickOk(e)} disabled={!this.state.password}>
                        Sign in
                    </button>
                </div>
                <div className="label label-danger">{this.state.errorMessage}</div>
            </form>
        )
    };

    clickOk(e) {
        e.preventDefault();
        var that = this;
        userService.signin(this.state.username, this.state.password).then(
            response => {
            },
            error => {
                that.state.errorMessage = error.data.message;
                that.forceUpdate();
            });
    }

}

export default Signin;
