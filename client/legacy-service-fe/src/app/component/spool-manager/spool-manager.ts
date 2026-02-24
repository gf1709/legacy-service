import { Component, signal, WritableSignal } from '@angular/core';
import { BkService, SpoolFileItem, SpoolManagerFilterParams } from '../../services/bk.service';
import { LoaderService } from '../../services/loader.service';
import { tap } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { MessageHelperService } from '../../services/message-helper.service';
import { AuthenticationService } from '../../services/authentication.service';
import { NgClass } from '@angular/common';

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
  imports: [FormsModule,NgClass],
  templateUrl: './spool-manager.html',
  styleUrl: './spool-manager.css',
})

export class SpoolManager {

  m_selectedRowIndex: number = -1;

  m_sort_mode: SortMode = SortMode.undefined;
  protected readonly m_spool_file_list: WritableSignal<SpoolFileItem[]> = signal([]);
  protected readonly m_spool_file_list_all: WritableSignal<SpoolFileItem[]> = signal([]);
  protected readonly m_spool_file_item_selected_index: WritableSignal<number> = signal(-1);
  protected readonly m_spool_file_item_selected: WritableSignal<SpoolFileItem> = signal(new SpoolFileItem());
  protected readonly m_spool_file_item_content: WritableSignal<string[]> = signal([]);

  get spoolManagerFilterParams(): SpoolManagerFilterParams {
    return this.bkService.g_spoolManagerFilterParams;
  }
  set spoolManagerFilterParams(value: SpoolManagerFilterParams) {
    this.bkService.g_spoolManagerFilterParams = value;
  }

  constructor(private bkService: BkService, private message_service: MessageHelperService,
    private loadingService: LoaderService, public authService: AuthenticationService) {
  }

  getSpoolFileList() {
    console.log('getSpoolFileList params are:', this.spoolManagerFilterParams);
    this.m_selectedRowIndex=-1;
    this.bkService.getSpoolFileList(this.spoolManagerFilterParams.userName).subscribe(
      data => {
        console.log(data);
        this.m_spool_file_list.set(data);
        this.m_spool_file_list_all.set(data);
        this.spoolManagerFilterParams.spoolDateFilter = '';
        this.spoolManagerFilterParams.spoolJobNameFilter = '';
        this.spoolManagerFilterParams.spoolNameFilter = '';
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

  getSpoolListLen(): number {
    return this.m_spool_file_list().length;
  }

  deleteSpool(spool: SpoolFileItem, idx: number) {
    console.log('deleteSpool', spool);
    this.bkService.deleteSpoolFileItem(spool.jobName, spool.jobUser, spool.jobNumber, spool.spoolfileName, spool.spoolNumber).subscribe(
      data => {
        this.m_spool_file_list.update((items) => items.filter((_, index) => index !== idx));
        this.message_service.info('Cancellazione terminata con successo');
        this.m_selectedRowIndex = -1;
      },
      error => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.error('Errore in fase di esecuzione della richiesta');
      }
    );
  }

  deleteAllSpool() {
    if (!confirm("Sicuro di voler cancellare tutti gli spool per l'utente " + this.spoolManagerFilterParams.userName.toUpperCase()))
      return;
    this.bkService.deleteAllSpools(this.spoolManagerFilterParams.userName).subscribe(
      data => {
        this.getSpoolFileList();
        this.message_service.info('Cancellazione degli spool terminata con successo');
        this.m_selectedRowIndex = -1;
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
    this.m_selectedRowIndex = -1;
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


  filterSpool() {
    console.log('filterSpool - spoolNameFilter is: ', this.spoolManagerFilterParams.spoolNameFilter);
    console.log('filterSpool - spoolDateFilter is: ', this.spoolManagerFilterParams.spoolDateFilter);
    console.log('filterSpool - spoolJobNameFilter is: ', this.spoolManagerFilterParams.spoolJobNameFilter);
    this.m_selectedRowIndex = -1;
    this.m_spool_file_list.set([...this.m_spool_file_list_all()]);
    var i = this.m_spool_file_list().length;
    while (i--) {
      var element: SpoolFileItem = this.m_spool_file_list()[i];
      if (this.spoolManagerFilterParams.spoolNameFilter.length > 0 && element.spoolfileName.indexOf(this.spoolManagerFilterParams.spoolNameFilter.toUpperCase()) < 0) {
        this.m_spool_file_list().splice(i, 1);
        continue;
      }

      if (this.spoolManagerFilterParams.spoolDateFilter.length > 0 && element.creation_ts.indexOf(this.spoolManagerFilterParams.spoolDateFilter.toUpperCase()) < 0) {
        this.m_spool_file_list().splice(i, 1);
        continue;
      }

      if (this.spoolManagerFilterParams.spoolJobNameFilter.length > 0 && (element.jobName + element.jobUser + element.jobNumber).indexOf(this.spoolManagerFilterParams.spoolJobNameFilter.toUpperCase()) < 0) {
        this.m_spool_file_list().splice(i, 1);
        continue;
      }
    }
  }
  clearSpoolNameFilter() {
    this.spoolManagerFilterParams.spoolNameFilter = '';
    this.filterSpool();
  }
  clearSpoolDateFilter() {
    this.spoolManagerFilterParams.spoolDateFilter = '';
    this.filterSpool();
  }
  clearSpoolJobNameFilter() {
    this.spoolManagerFilterParams.spoolJobNameFilter = '';
    this.filterSpool();
  }


  onChangeSpoolNameFilter(value: string) {
    console.log('onChangeSpoolNameFilter', value);
    this.spoolManagerFilterParams.spoolNameFilter = value;
    this.filterSpool();
  }
  onChangeSpoolJobNameFilter(value: string) {
    console.log('onChangeSpoolJobNameFilter', value);
    this.spoolManagerFilterParams.spoolJobNameFilter = value;
    this.filterSpool();

  }
  onChangeSpoolDateFilter(value: string) {
    console.log('onChangeSpoolDateFilter', value);
    this.spoolManagerFilterParams.spoolDateFilter = value;
    this.filterSpool();

  }
  selectRow(i: number) {
    this.m_selectedRowIndex = i;
  }
}

