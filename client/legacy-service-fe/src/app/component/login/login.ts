import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { NgForm, FormsModule  } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { MessageHelperService } from '../../services/message-helper.service';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {

  @ViewChild('loginForm') loginForm!: NgForm;

  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';

  constructor(private authService: AuthenticationService, private message_service: MessageHelperService, private router: Router) { }

  ngOnInit(): void {
    if (this.authService.isUserLoggedIn()) {
      this.isLoggedIn = true;
    }
  }

  ngAfterViewInit() {
    console.log('loginForm property is set:', this.loginForm);
  }
  doLogin(data: any): void {
    this.authService.authenticate(data.username, data.password, data.session).subscribe(
      {
        next: data => {
          console.log('login ok');
          this.isLoginFailed = false;
          this.isLoggedIn = true;
          this.redirectToObjectManager();
          this.loginForm.reset();
        },
        error: err => {
          console.log('Error on login', err);
          this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di login:'+ err.error.text);
          this.errorMessage = err.error.message;
          this.isLoginFailed = true;
        }
      }
    )
  }

  redirectToObjectManager(): void {
    this.router.navigate(['/object-manager']);
  }
}
