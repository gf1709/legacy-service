import { Injectable } from '@angular/core';
import {HTTP_INTERCEPTORS, HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';
import { LoaderService } from '../services/loader.service';
import { finalize } from 'rxjs/internal/operators/finalize';


@Injectable({
  providedIn: 'root',
})
export class BasicAuthHtppInterceptorService implements HttpInterceptor {

  private totalRequests = 0;
  constructor(private loadingService: LoaderService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {

    if (sessionStorage.getItem('username') && sessionStorage.getItem('token')) {
      console.log('BasicAuthHtppInterceptorService-Adding auth header to the request. request url: ', req.url);
      req = req.clone({
        setHeaders: {
          Authorization: `${sessionStorage.getItem('token')}`
        }
      })
      console.log('BasicAuthHtppInterceptorService-Added auth header to the request. request url: ', req.url);
    }
    else {
      console.log('BasicAuthHtppInterceptorService-No auth header added to the request. request url: ', req.url);
    }

    this.totalRequests++;
    this.loadingService.setLoading(true);

    return next.handle(req).pipe(
      finalize(() => {
        this.totalRequests--;
        if (this.totalRequests == 0) {
          this.loadingService.setLoading(false);
        }
      })
    );

  }
}
