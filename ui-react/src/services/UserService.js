import httpService from '../services/HttpService';
import BaseService from './BaseService';
import notificationService from './NotificationService';

class UserService extends BaseService {

    user = null;
    token = null;

    getUser() {
        return this.user;
    }

    signout() {
        this.user = null;
        httpService.token = null;
        window.location.hash = '/home';
        this.notify();
    }

    signin(username, password) {
        let that = this;
        username = username || 'Anonymous';
        return httpService.post('/signin', {username: username, password: password})
            .then(
                response => {
                    httpService.token = response.data.token;
                    that.user = username;
                    window.location.hash = '/home';
                    notificationService.info("Welcome back, " + username + "!");
                    this.notify();
                }
            );
    }

}

let userService = new UserService();

export default userService;