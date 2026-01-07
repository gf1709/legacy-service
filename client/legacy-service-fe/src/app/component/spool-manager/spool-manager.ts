import { Component, signal, WritableSignal } from '@angular/core';
import { BkService, SpoolFileItem } from '../../services/bk.service';
import { LoaderService } from '../../services/loader.service';
import { tap } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { MessageHelperService } from '../../services/message-helper.service';

enum SortMode {
  Nome_ASC,
  Nome_DESC,
  Data_ASC,
  Data_DESC,
  JobName_ASC,
  JobName_DESC,
  undefined
}
@Component({
  selector: 'app-spool-manager',
  imports: [FormsModule],
  templateUrl: './spool-manager.html',
  styleUrl: './spool-manager.css',
})

export class SpoolManager  {

  m_sort_mode: SortMode = SortMode.undefined;
  m_spool_filter_username: string = '';
  protected readonly m_spool_file_list: WritableSignal<SpoolFileItem[]> = signal([]);
  protected readonly m_spool_file_list_all: WritableSignal<SpoolFileItem[]> = signal([]);
  protected readonly m_spool_file_item_selected_index: WritableSignal<number> = signal(-1);
  protected readonly m_spool_file_item_selected: WritableSignal<SpoolFileItem> = signal(new SpoolFileItem());
  protected readonly m_spool_file_item_content: WritableSignal<string[]> = signal([]);

  m_filter_nome: string = '';
  m_filter_data: string = '';
  m_filter_jobname: string = '';

  constructor(private bkService: BkService, private message_service: MessageHelperService, private loadingService: LoaderService) {
  }


  getSpoolFileList() {
    this.bkService.getSpoolFileList(this.m_spool_filter_username).subscribe(
      data => {
        console.log(data);
        this.m_spool_file_list.set(data);
        this.m_spool_file_list_all.set(data);
      },
      error => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.error('Errore in fase di esecuzione della richiesta');
      }
    );
  }

  getSpoolContent(spool: SpoolFileItem) {
    console.log('getSpoolContent', spool);
    let downLoadedFile: string = spool.jobName + "_" + spool.jobUser + "_" + spool.jobNumber + "_" + spool.spoolfileName + "_" + spool.spoolNumber + ".log";
    return this.bkService.getSpoolFileItem(spool.jobName, spool.jobUser, spool.jobNumber, spool.spoolfileName, spool.spoolNumber)
      .pipe(
        tap(data => console.log('Spool Content: ' + JSON.stringify(data))),
        tap(data => this.m_spool_file_item_selected.set(spool)),
        tap(data => this.m_spool_file_item_content.set(data))
      );
  }

  getSpool(spool: SpoolFileItem) {
    console.log('getSpool', spool);
    let downLoadedFile: string = spool.jobName + "_" + spool.jobUser + "_" + spool.jobNumber + "_" + spool.spoolfileName + "_" + spool.spoolNumber + ".log";
    this.getSpoolContent(spool).subscribe(
      data => {
        let binaryData: BlobPart[] = [];
        this.m_spool_file_item_content().forEach(
          (ele) => {
            binaryData.push(ele + '\n');
          }
        );
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: 'application/octet-stream' }));
        downloadLink.setAttribute('download', downLoadedFile);
        document.body.appendChild(downloadLink);
        downloadLink.setAttribute('target', '_blank');
        downloadLink.click();
        document.body.removeChild(downloadLink);
      },
      error => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.error('Errore in fase di esecuzione della richiesta');
      }
    );
  }
  showSpool(spool: SpoolFileItem, idx: number) {
    console.log('showSpool', spool);
    this.m_spool_file_item_selected_index.set(-1);
    this.getSpoolContent(spool).subscribe(
      data => {
        this.m_spool_file_item_selected_index.set(idx);
        console.log('data received-showSpool-m_spool_file_item_content set');
      }
    );
  }

  getSpoolListLen():number
  {
    return this.m_spool_file_list().length;
  }

  deleteSpool(spool: SpoolFileItem, idx: number) {
    console.log('deleteSpool', spool);
    this.bkService.deleteSpoolFileItem(spool.jobName, spool.jobUser, spool.jobNumber, spool.spoolfileName, spool.spoolNumber).subscribe(
      data => {
        this.m_spool_file_list.update((items) => items.filter((_, index) => index !== idx));
        this.message_service.info('Cancellazione terminata con successo');
      },
      error => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.error('Errore in fase di esecuzione della richiesta');
      }
    );
  }

  deleteAllSpool() {
    if (!confirm("Sicuro di voler cancellare tutti gli spool per l'utente " + this.m_spool_filter_username.toUpperCase()))
      return;
    this.bkService.deleteAllSpools(this.m_spool_filter_username).subscribe(
      data => {
        this.getSpoolFileList();
        this.message_service.info('Cancellazione degli spool terminata con successo');
      },
      error => {
        this.message_service.error('Errore in fase di esecuzione della richiesta');
      }
    );
  }

  isCurrentSortASC(): boolean {
    if (this.m_sort_mode === SortMode.Nome_ASC
      || this.m_sort_mode === SortMode.Data_ASC
      || this.m_sort_mode === SortMode.JobName_ASC
    )
      return true;
    else
      return false;
  }

  sort(sortCol: string) {
    console.log('sort', sortCol);
    this.loadingService.setLoading(true);
    if (sortCol === 'nome') {
      if (this.m_sort_mode === SortMode.Nome_ASC) {
        this.m_spool_file_list.set(this.m_spool_file_list().sort((a, b) => (a.spoolfileName > b.spoolfileName ? -1 : 1)));
        this.m_sort_mode = SortMode.Nome_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_spool_file_list.set(this.m_spool_file_list().sort((a, b) => (a.spoolfileName < b.spoolfileName ? -1 : 1)));
        this.m_sort_mode = SortMode.Nome_ASC; // l'ordinamento fatto e' crescente
      }
    }
    else if (sortCol === 'data') {
      if (this.m_sort_mode === SortMode.Data_ASC) {
        this.m_spool_file_list.set(this.m_spool_file_list().sort((a, b) => (a.creation_ts > b.creation_ts ? -1 : 1)));
        this.m_sort_mode = SortMode.Data_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_spool_file_list.set(this.m_spool_file_list().sort((a, b) => (a.creation_ts < b.creation_ts ? -1 : 1)));
        this.m_sort_mode = SortMode.Data_ASC; // l'ordinamento fatto e' crescente
      }
    }
    else if (sortCol === 'jobname') {
      if (this.m_sort_mode === SortMode.JobName_ASC) {
        this.m_spool_file_list.set(this.m_spool_file_list().sort((a, b) => (a.jobName + a.jobUser + a.jobNumber > b.jobName + b.jobUser + b.jobNumber ? -1 : 1)));
        this.m_sort_mode = SortMode.JobName_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_spool_file_list.set(this.m_spool_file_list().sort((a, b) => (a.jobName + a.jobUser + a.jobNumber < b.jobName + b.jobUser + b.jobNumber ? -1 : 1)));
        this.m_sort_mode = SortMode.JobName_ASC; // l'ordinamento fatto e' crescente
      }
    }

    this.loadingService.setLoading(false);
  }

  onKeydown(event: any) {
    console.log('onKeydown', event);
    if (event.code.indexOf('Enter') > -1) {
      console.log('onKeydown-filterSpool', event, 'm_filter_nome is: ', this.m_filter_nome);
      console.log('onKeydown-filterSpool', event, 'm_filter_data is: ', this.m_filter_data);
      console.log('onKeydown-filterSpool', event, 'm_filter_jobname is: ', this.m_filter_jobname);
      this.filterSpool();
    }
  }
  filterSpool() {
    this.m_spool_file_list.set([...this.m_spool_file_list_all()]);
    var i = this.m_spool_file_list.length;
    while (i--) {
      var element: SpoolFileItem = this.m_spool_file_list()[i];
      if (this.m_filter_nome.length > 0 && element.spoolfileName.indexOf(this.m_filter_nome.toUpperCase()) < 0) {
        this.m_spool_file_list().splice(i, 1);
        continue;
      }

      if (this.m_filter_data.length > 0 && element.creation_ts.indexOf(this.m_filter_data.toUpperCase()) < 0) {
        this.m_spool_file_list().splice(i, 1);
        continue;
      }

      if (this.m_filter_jobname.length > 0 && (element.jobName + element.jobUser + element.jobNumber).indexOf(this.m_filter_jobname.toUpperCase()) < 0) {
        this.m_spool_file_list().splice(i, 1);
        continue;
      }
    }
  }

  ciao()
  {
    console.log('toast closed....');
  }
}

