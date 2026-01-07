import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGaurdService implements CanActivate {

  constructor(private router: Router, private authService: AuthenticationService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    console.log(route);
    if (this.authService.isUserLoggedIn()) {
      console.log('route ok allowed', route);
      return true;
    }

    this.router.navigate(['login']);
    {
      console.log('route not allowed', route);
      return false;
    }
  }

}
