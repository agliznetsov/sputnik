import {Component, OnInit} from '@angular/core';
import {UserService} from '../user.service';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SigninComponent implements OnInit {

  public errorMessage;
  public username;
  public password;

  constructor(private userService: UserService) {
  }

  ngOnInit() {
  }

  public clickOk = function (form) {
    let that = this;
    // this.userService.signin(this.username, this.password, this.error.bind(this));
    this.userService.signin(this.username, this.password, (e) => this.error(e));
  };

  private error(response) {
    let data = response.json();
    if (data && data.message) {
      this.errorMessage = data.message;
    } else {
      this.errorMessage = 'Request failed!';
    }
  }

}
