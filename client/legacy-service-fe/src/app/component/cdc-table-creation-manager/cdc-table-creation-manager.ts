import { Component, signal, WritableSignal } from '@angular/core';
import { BkService } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import {CommonModule} from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
@Component({
  selector: 'app-cdc-table-creation-manager',
  imports: [FormsModule, CommonModule],
  templateUrl: './cdc-table-creation-manager.html',
  styleUrl: './cdc-table-creation-manager.css',
})
export class CdcTableCreationManager {


  m_library: string = '';
  m_file: string = '';
  m_ddl_script_lines: WritableSignal<string[]> = signal([]);
  constructor(private bkService: BkService, private message_service: MessageHelperService) {
  }

  hasLines(): boolean {
    return this.m_ddl_script_lines().length > 0;
  }
  createScript() {
    console.log("createScript...", this.m_library, this.m_file);
    this.m_ddl_script_lines.set([]);
    this.bkService.createCDCTableDDL(this.m_library, this.m_file).subscribe(
      data => {
        this.m_ddl_script_lines.set(data);
        console.log("createScript script is ", this.m_ddl_script_lines());
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  copyToClipboard() {
    if (this.m_ddl_script_lines().length < 1)
      return;
    let text: string = '';
    this.m_ddl_script_lines().forEach(element => {
      text += element + '\n';
    });
    if (!text) {
      console.log('Nothing to copy');
      return;
    }
    // Try modern API first
    if (navigator.clipboard && window.isSecureContext) {
      try {
        navigator.clipboard.writeText(text).then(
          (value) => {
            console.log('copy done');
          }
        );
      } catch (err) {
        console.warn('Modern clipboard API failed, trying fallback:', err);
      }
    }
  }

}
