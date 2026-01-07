import { Component, signal, WritableSignal } from '@angular/core';
import { BkService, LibraryListItem } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import {CommonModule} from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-library-list-manager',
  imports: [CommonModule, FormsModule],
  templateUrl: './library-list-manager.html',
  styleUrl: './library-list-manager.css',
})
export class LibraryListManager{

  m_library_to_add: WritableSignal<string> = signal('');
  m_library_list: WritableSignal<LibraryListItem[]> = signal([]);
  m_add_form: boolean = false;

  constructor(private bkService: BkService, private message_service: MessageHelperService) {
    this.getLibraryList();
  }

  getLibraryList() {
    this.bkService.getLibraryList().subscribe(
      data => {
        console.log(data);
        this.m_library_list.set(data);
      },
      error => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  canRemove(lli: LibraryListItem) {

    if (lli.type !== 'USER')
      return false;
    if (lli.library.substring(0, 1) === 'Q' || lli.library === 'S44B3824' || lli.library === 'QTEMP')
      return false;
    return true;
  }

  removeLibraryFromLibraryList(library: string) {
    {
      this.bkService.removeLibraryFromLibraryList(library).subscribe(
        data => {
          console.log(library + ' removed from libraryList');
          this.message_service.messageShow(this.message_service.msg_type.Success, library + ' removed from libraryList');
          this.getLibraryList();
        },
        err => {
          console.log(err);
          this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di rimozione della libreria dalla lista delle librerie');
        }
      )
    }
  }

  addLibraryFromLibraryList() {
    {
      this.bkService.addLibraryToLibraryList(this.m_library_to_add()).subscribe(
        data => {
          console.log(this.m_library_to_add() + ' added to libraryList');
          this.message_service.messageShow(this.message_service.msg_type.Success, this.m_library_to_add() + ' added to libraryList');
          this.getLibraryList();
        },
        err => {
          console.log(err);
          this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di aggiunta della libreria dalla lista delle librerie');
        }
      )
    }
  }

  toogleAddLibraryForm()
  {
    this.m_add_form = !this.m_add_form;
  }

}
