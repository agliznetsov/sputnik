import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {Router} from '@angular/router';

@Injectable()
export class UserService {
  private user;

  constructor(private httpService: HttpService, private router: Router) {
  }

  public getUser() {
    return this.user;
  }

  public signout() {
    this.user = null;
    this.router.navigate(['/home']);
  }

  public signin(username, password, error) {
    username = username || 'Anonymous';
    return this.httpService.post('/signin', {username: username, password: password})
      .subscribe(
        response => {
          this.httpService.token = response.json().token;
          this.user = username;
          this.router.navigate(['/home']);
        },
        err => {
          error(err);
        }
      );
  }

}
