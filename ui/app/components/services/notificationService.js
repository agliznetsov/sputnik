'use strict';
angular.module('sputnik').factory('notificationService', function ($rootScope, $timeout) {
    $rootScope.notifications = {};

    var api = {
        error: function (error, timeout, defaultMessage) {
            var message;
            if (typeof error === 'string') {
                message = error;
            }
            else if (error.data && error.data.message) {
                message = error.data.message;
            } else {
                message = defaultMessage || "Request failed!";
            }
            this.addNotification('error', message, timeout);
        },
        info: function (message, timeout) {
            api.addNotification('info', message, timeout);
        },
        addNotification: function (type, message, timeout) {
            var id = type + (new Date().getTime());
            var notification = {
                id: id,
                type: type,
                message: message
            };
            $rootScope.notifications[id] = notification;
            timeout = timeout || 5000;
            $timeout(function () {
                delete $rootScope.notifications[id];
            }, timeout);
            return id;
        },
        changeNotification: function (id, type, message, timeout) {
            var notification = $rootScope.notifications[id];
            if (notification) {
                notification.type = type;
                notification.message = message;
                if (timeout > 0) {
                    $timeout(function () {
                        delete $rootScope.notifications[id];
                    }, timeout);
                }
            }
        },
        removeNotification: function (id) {
            delete $rootScope.notifications[id];
        },
        getNotifications: function () {
            return $rootScope.notifications;
        }
    };

    return api;
});
