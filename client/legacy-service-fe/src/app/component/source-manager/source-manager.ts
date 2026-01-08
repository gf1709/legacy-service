import { Component, ViewChild, WritableSignal, signal } from '@angular/core';
import { MessageHelperService } from '../../services/message-helper.service';
import { BkService, SourceListResult, SourceManagerFilterParams, SourceResult } from '../../services/bk.service';
import { FormsModule, NgForm } from '@angular/forms';
import { tap } from 'rxjs';

@Component({
  selector: 'app-source-manager',
  imports: [FormsModule],
  templateUrl: './source-manager.html',
  styleUrl: './source-manager.css',
})
export class SourceManager {

  @ViewChild('sourceFilterForm') sourceFilterForm!: NgForm;

  constructor(private bkService: BkService, private message_service: MessageHelperService) {
  }

  get sourceFilterParams():SourceManagerFilterParams {
    return this.bkService.g_sourceManagerFilterParams;
  }
  set sourceFilterParams(value: SourceManagerFilterParams) {
    this.bkService.g_sourceManagerFilterParams = value;
  }

  m_source_list: WritableSignal<SourceListResult> = signal(new SourceListResult());
  m_source_result: WritableSignal<SourceResult> = signal(new SourceResult());

  getSourceList() {

    console.log('[0]-getSourceList');
    this.bkService.getSourceList(this.sourceFilterParams.library, this.sourceFilterParams.file, this.sourceFilterParams.member).subscribe(
      data => {
        console.log(data);
        this.m_source_list.set(data);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }

    );
  }
  getSourceListEmpty(): boolean {
    return this.m_source_list().sources?.values.length === 0;
  }

  getSourceContent(library: string, sourceFile: string, sourceMember: string, explodeCopy: boolean) {
    this.m_source_result.set(
      new SourceResult()
    )
    console.log('[0]-getSourceContent');
    return this.bkService.getSource(library, sourceFile, sourceMember, explodeCopy).pipe(
      tap(data => console.log('Source result is: ' + JSON.stringify(data))),
      tap(data => this.m_source_result.set(data))
    );
  }


  getSource(library: string, sourceFile: string, sourceMember: string, explodeCopy: boolean) {
    console.log('[1]-getSource');
    this.getSourceContent(library, sourceFile, sourceMember, explodeCopy).subscribe(
      data => {
        let binaryData: BlobPart[] = [];
        this.m_source_result().source.forEach(
          (ele) => {
            binaryData.push(ele + '\n');
          }
        );
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: 'application/octet-stream' }));
        downloadLink.setAttribute('download', this.m_source_result().library + '_' + this.m_source_result().sourceFile + '_' + this.m_source_result().sourceMember + '.' + this.m_source_result().sourceType);
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    )
  }

  showSource(library: string, sourceFile: string, sourceMember: string, explodeCopy: boolean) {
    console.log('[0]-showSource');
    this.getSourceContent(library, sourceFile, sourceMember, explodeCopy).subscribe(
      data => {
        console.log('data received-showSource-m_source_result set');
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    )
  }

  clean_filter() {
    console.log('clean_filter');
    this.sourceFilterParams.library = '';
    this.sourceFilterParams.file = '';
    this.sourceFilterParams.member = '';
  }
}
