import {Component} from '@angular/core';
import {NotificationsService} from './notifications.service';
import {UserService} from './user.service';
import {HttpService} from './http.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [NotificationsService, UserService, HttpService]
})
export class AppComponent {

  public menu = [
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


  constructor(public notificationsService: NotificationsService, public userService: UserService, private router: Router) {
  }

  public isVisible(item) {
    return this.userService.getUser();
  };

  public isActive(path) {
    return this.router.url.indexOf(path) === 0;
  };

}
