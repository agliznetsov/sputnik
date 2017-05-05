import {Injectable} from '@angular/core';
import * as _ from 'lodash';

@Injectable()
export class NotificationsService {

  notifications: {} = {};

  constructor() {
  }

  public getNotifications(): Array<Object> {
    return _.values(this.notifications);
  }

  public error(error, timeout?, defaultMessage?) {
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

  public info(message, timeout) {
    this.addNotification('info', message, timeout);
  }

  public addNotification(type, message, timeout) {
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
    return notification.id;
  }

  public changeNotification(id, type, message, timeout) {
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
    }
  };

  public removeNotification(id) {
    delete this.notifications[id];
  }

}
