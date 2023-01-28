import {Component, OnInit} from '@angular/core';
import {User} from "src/app/model/user";
import {UserService} from "src/app/service/user.service";
import {Role} from "../model/role";


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  clientRole: Role = new Role(1,"Client");
  adminRole: Role = new Role(1,"Admin");

  roles: Role[] = [];
  user: User = new User();

  constructor(
    private userService: UserService,
  ) {
  }

  ngOnInit(): void {
  }

  // defineClientRole() {
  //   this.userService.register(this.user).subscribe(data => {
  //     this.roles.push(this.clientRole);
  //     this.user.roles = this.roles;
  //   }, error =>
  //     alert("failed"))
  // }

  userRegister() {
    this.roles.push(this.clientRole);
    this.user.roles = this.roles;
    this.userService.register(this.user).subscribe(data => {
        this.user.username = "";
        this.user.password = "";
        this.user.email = "";
        this.user.cityAddress = "";
        this.user.phoneNumber = "";
        this.user.creditCardNumber = "";
      },
      error => {
        this.user.username = '';
        this.user.password = '';
        this.user.email = '';
        this.user.cityAddress = '';
        this.user.phoneNumber = '';
        this.user.creditCardNumber = '';
      })
  }
}
