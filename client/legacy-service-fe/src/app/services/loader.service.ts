import { Injectable, signal, WritableSignal } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoaderService {

  private loading: WritableSignal<boolean> = signal(false);

  constructor() { }

  setLoading(loading: boolean) {
    this.loading.set(loading);
  }

  getLoading(): boolean {
    return this.loading();
  }

}
