import * as _ from 'lodash';
import BaseService from './BaseService';

class NotificationsService extends BaseService{

    notifications = [];

    getNotifications() {
        return _.values(this.notifications);
    }

    error(error, timeout, defaultMessage) {
        let message;
        if (typeof error === 'string') {
            message = error;
        }
        else if (error.message) {
            message = error.message;
        } else {
            message = defaultMessage || 'Request failed!';
        }
        this.addNotification('error', message, timeout);
    }

    info(message, timeout) {
        this.addNotification('info', message, timeout);
    }

    addNotification(type, message, timeout) {
        let notification = {
            id: type + (new Date().getTime()) + Math.random(),
            type: type,
            message: message
        };
        this.notifications[notification.id] = notification;
        timeout = timeout || 5000;
        let that = this;
        setTimeout(function () {
            that.removeNotification(notification.id);
        }, timeout);
        this.notify();
        return notification.id;
    }

    changeNotification(id, type, message, timeout) {
        let notification = this.notifications[id];
        if (notification) {
            notification.type = type;
            notification.message = message;
            if (timeout > 0) {
                let that = this;
                setTimeout(function () {
                    that.removeNotification(notification.id);
                }, timeout);
            }
            this.notify();
        }
    }

    removeNotification(id) {
        console.log('remove', id);
        delete this.notifications[id];
        this.notify();
    }

}

let notificationsService = new NotificationsService();

export default notificationsService;