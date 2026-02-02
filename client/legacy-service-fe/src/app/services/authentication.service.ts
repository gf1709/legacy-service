import { Injectable, WritableSignal, inject, signal } from '@angular/core';
import { map } from "rxjs/operators";
import { environment } from '../environment';
import { BkService } from './bk.service';
import { HttpClient } from '@angular/common/http';
import {jwtDecode, JwtPayload} from 'jwt-decode';

export class User {
  constructor(public status: string) { }
}
interface JwtPayloadWithRoles extends JwtPayload {
  roles?: string[];
}
@Injectable({
  providedIn: "root"
})
export class AuthenticationService {

  isUserLoggedIn: WritableSignal<boolean> = signal(false);

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
          sessionStorage.setItem("roles", userData.roles);
          this.isUserLoggedIn.set(true);

          this.bkservice.g_spoolManagerFilterParams.userName = username.toUpperCase();
          this.bkservice.g_jobManagerFilterParams.jobUser = username.toUpperCase();
          this.bkservice.g_jobManagerFilterParams.jobName = session.toUpperCase();

          return userData;
        })
      );
  }

  getRoles() {
    let token = sessionStorage.getItem("token");
    if (!token) {
      return [];
    }
    const decodedJwt = jwtDecode<JwtPayloadWithRoles>(token.split(' ')[1]);
    console.log('decoded jwt :', decodedJwt);
    let rolesArray: string[] = decodedJwt.roles ? decodedJwt.roles : [];
    return rolesArray;
  }

  logOut() {
    return this.httpClient.get(environment.apiUrl + "/logout")
      .pipe(
        map(userData => {
          sessionStorage.removeItem("username");
          sessionStorage.removeItem("session");
          sessionStorage.removeItem("token");
          this.isUserLoggedIn.set(false);
          return userData;
        })
      );
  }

  canBeVisibile(route: string): boolean {
    if (this.isUserLoggedIn()) {
      if (route === 'object-manager'
        || route === 'source-manager'
        || route === 'spool-manager'
        || route === 'job-manager'
        || route === 'library-list'
        || route === 'zztrut-manager'
        || route === 'session-manager'
        || route === 'netstat-job'
        || route === 'servizi-sibank'
        || route === 'ifs-manager'
      ) {
        console.log('route can be visibile', route)
        return true;
      }
      if (route === 'cdc-table'
        || route === 'sql-script'
        || route === 'dsplog-manager'
        || route === 'utilities'
      )
        if (this.getRoles()?.indexOf('admin')) {
          console.log('route can be visibile', route)
          return true;
        }
    }
    return false;
  }

  public isUserAdmin(): boolean {
    let roles = this.getRoles();
    if (roles && roles.indexOf('admin') >= 0) {
      return true;
    } else {
      return false;
    }
  }

  public getLoggedInUsername(): string | null {
    let username = sessionStorage.getItem("username");
    return username;
  }
}
