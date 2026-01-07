import { Component, signal, WritableSignal } from '@angular/core';
import { BkService } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { CommonModule } from '@angular/common';

export class Session {
  user: string = '';
  terminal: string = '';
  targaCassa: string = '';
  ambiente: string = '';
  terminaleApplicativo: string = '';
  libreriaTemporanea: string = '';
  sib_Directory: string = '';
  descrizione_breve_CR: string = '';
  cartellaAmbienteDati: string = '';
  descrizione_2_CR: string = '';
  cab: string = '';
  libreria4: string = '';
  libreria2: string = '';
  libreria3: string = '';
  libreriaRete: string = '';
  documentale_SIB2000: string = '';
  documentale_InfoBanking: string = '';
  libreriaCestino: string = '';
  cartellaFileTransfer: string = '';
  libreriaDatiBanca: string = '';
  ambienteDatiStorici: string = '';
  server_SID2000: string = '';
  abi_cin: string = '';
  ambienteDati: string = '';
  descrizione_1_CR: string = '';
  descrizioneFilialeBreve: string = '';
  abi: string = '';
  cab_senza_cin: string = '';
  libreria1: string = '';
  libreriaProcedure: string = '';
  descrizioneFiliale: string = '';
  codiceFiliale: string = '';
};



@Component({
  selector: 'app-session-manager',
  imports: [],
  templateUrl: './session-manager.html',
  styleUrl: './session-manager.css',
})
export class SessionManager {

  constructor(private bkService: BkService, private message_service: MessageHelperService) {
    this.getSession();
  }

  session_values: WritableSignal<Session> = signal(new Session());

  getSession() {
    this.bkService.getSession().subscribe(
      data => {
        this.session_values.set(<Session>data);
        console.log('ok. session data are', this.session_values);
      },
      error => {
        console.log('errore in fase di esecuzione della richiesta session');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta session');
      }
    );
  }

}
