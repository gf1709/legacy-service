import { Injectable, inject } from '@angular/core';
import { map } from "rxjs/operators";
import { environment } from '../environment';
import { BkService } from './bk.service';
import { HttpClient } from '@angular/common/http';

export class User {
  constructor(public status: string) { }
}

@Injectable({
  providedIn: "root"
})
export class AuthenticationService {
  httpClient: HttpClient = inject(HttpClient);
  constructor(private bkservice: BkService) { }

  // Provide username and password for authentication, and once authentication is successful, store JWT token in session
  authenticate(username: string, password: string, session: string) {
    console.log("logging to ", environment.apiUrl + "/authenticate");
    return this.httpClient
      .post<any>(environment.apiUrl + "/authenticate", { username, password, session })
      .pipe(
        map(userData => {
          sessionStorage.setItem("username", username);
          sessionStorage.setItem("session", session);
          let tokenStr = "Bearer " + userData.token;
          sessionStorage.setItem("token", tokenStr);
          return userData;
        })
      );
  }

  isUserLoggedIn() {
    let user = sessionStorage.getItem("username");
    return !(user === null);
  }

  logOut() {
    return this.httpClient.get(environment.apiUrl + "/logout")
      .pipe(
        map(userData => {
          sessionStorage.removeItem("username");
          sessionStorage.removeItem("session");
          sessionStorage.removeItem("token");
          return userData;
        })
      );
  }

}
