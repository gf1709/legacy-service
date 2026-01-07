import { AfterContentInit, AfterViewInit, Component, ElementRef, signal, ViewChild } from '@angular/core';
import { Router, RouterOutlet, RouterLink } from '@angular/router';
import { AuthenticationService } from './services/authentication.service';
import { BkService } from './services/bk.service';
import { Spinner } from './component/spinner/spinner';
import { MessageHelperService } from './services/message-helper.service';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { interval, takeWhile } from 'rxjs';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Spinner, CommonModule, FormsModule, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  @ViewChild('my_toast') my_toast!: ElementRef;

  protected readonly title = signal('Allitude. Legacy Service');
  private TOAST_TIMEOUT: number = 5000;
  private timeoutID: number = -1;

  constructor(private router: Router, private authService: AuthenticationService,
    private bkService: BkService, public message_service: MessageHelperService) {

    message_service.toasts$.subscribe(
      () => {
        console.log('message_service.toasts$ changed:', message_service.toasts());
        if (message_service.toasts().length > 0) {
          this.showToast();
        }
      }
    );
    console.log('timeoutID [1]:', this.timeoutID);
  }

  showToast() {
    this.my_toast.nativeElement.classList.add('show');  // visualizza il toast
    // imposto il timeout per rimuovere il toast dopo un certo periodo se non è già stato impostato
    console.log('timeoutID [2a] - clearing timeout:', this.timeoutID);
    clearTimeout(this.timeoutID);
    this.timeoutID = setTimeout(() => {
      this.deleteAllToastMessages()
    }, this.TOAST_TIMEOUT);
    console.log('timeoutID [2b]:', this.timeoutID);
  }

  deleteAllToastMessages() {
    console.log('removeToast [3]:', this.message_service.toasts());
    this.message_service.removeAll();
    this.my_toast.nativeElement.classList.remove('show');
    console.log('timeoutID [3a] - clearing timeout:', this.timeoutID);
    clearTimeout(this.timeoutID);
    this.timeoutID = -1;
    console.log('timeoutID [3b]:', this.timeoutID);
  }

  logout() {
    console.log("logging out");
    this.authService.logOut().subscribe(
      data => {
        console.log('logout done');
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta di logout', err);
      }
    );
    this.router.navigate(['/login']);
  }
  isUserLoggedIn() {
    return this.authService.isUserLoggedIn();
  }

  loginInfo() {
    let loginInfo = '';
    if (this.isUserLoggedIn())
      loginInfo = sessionStorage.getItem("username")?.toUpperCase() + ' - ' + sessionStorage.getItem("session")?.toUpperCase();
    return loginInfo;
  }

  update_abi_mapper() {
    console.log('update_abi_mapper...');
    this.bkService.updateLegacyTermialAbiMapper().subscribe(
      () => {
        console.log('update_abi_mapper done');
        alert('Aggiornamento effettuato correttamente')
      },
      (err) => {
        console.log('update_abi_mapper in error', err);
        alert("Errore in fase di esecuzione dell'aggiornamento richiesto");
      }
    );
  }

}
