import React from 'react';
import notificationService from '../services/NotificationService';

class Notifications extends React.Component {

    componentDidMount() {
        notificationService.subscribe(() => this.forceUpdate());
    }

    render() {
        return (
            <div className="notification-wrapper">
                {notificationService.getNotifications().map((it) => this.notification(it))}
            </div>
        );
    }

    notification(it) {
        return (
            <div key={it.id} className="modal-content notification notification-animation">
                <i className="close fa fa-lg fa-times-circle" onClick={() => notificationService.removeNotification(it.id)}></i>
                <span><i className={"type-icon fa fa-lg " + it.type}></i>{it.message}</span>
            </div>
        );
    }

}

export default Notifications;
