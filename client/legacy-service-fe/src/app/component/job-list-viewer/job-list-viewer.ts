import { Component, computed, effect, input, InputSignal, output, signal, Signal, WritableSignal } from '@angular/core';
import { BkService, JobListItemExtended } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { DomSanitizer } from '@angular/platform-browser';
import { formatDate, NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { tap } from 'rxjs';

@Component({
  selector: 'app-job-list-viewer',
  imports: [FormsModule, NgClass],
  templateUrl: './job-list-viewer.html',
  styleUrl: './job-list-viewer.css',
})

export class JobListViewer {
  // valore passato come input dal componente padre
  public readonly joblist = input<JobListItemExtended[]>([]);
  // valore passato come input dal componente padre: è la funziona da lanciare per eseguire l'aggiornamento dell'elenco
  updateList = output<void>();
  public readonly m_is_netstat_list = input<boolean>(false);

  m_curr_JobLogContent: WritableSignal<string[]> = signal([]);

  m_curr_JobLogUser: string = '';
  m_curr_JobLogName: string = '';
  m_curr_JobLogNumber: string = '';


  m_filter_jobname: WritableSignal<string> = signal(''); // filtro sul nome del job. I filtri possono essere molti separatati da virgola
  m_filter_username: WritableSignal<string> = signal(''); // filtro sul nome utente. I filtri possono essere molti separatati da virgola
  m_filter_current_username: WritableSignal<string> = signal(''); // filtro sul nome utente. I filtri possono essere molti separatati da virgola
  m_filter_status: WritableSignal<string> = signal(''); // filtro sulla funzione. I filtri possono essere molti separatati da virgola

  // lista dei job filtrata
  public m_job_list: Signal<JobListItemExtended[]> = computed(() => {
    const allJobs = this.joblist().slice(); // copia dell'array originale
    const filter_jobname_values = this.m_filter_jobname().split(',');
    const filter_username_values = this.m_filter_username().split(',');
    const filter_current_username_values = this.m_filter_current_username().split(',');
    const filter_status_values = this.m_filter_status().split(',');

    var i = allJobs.length;

    while (i--) {
      var element: JobListItemExtended = allJobs[i];

      const jobName = element.job.name();
      if (!this.isValueContainedInFilters(jobName, filter_jobname_values)) {
        allJobs.splice(i, 1);
        continue;
      }

      const jobUser = element.job.user();
      if (!this.isValueContainedInFilters(jobUser, filter_username_values)) {
        allJobs.splice(i, 1);
        continue;
      }

      const currentUser = element.job.currentUser();
      const currentUserDescr = element.job.currentUserDescription();
      if (!this.isValueContainedInFilters(currentUser, filter_current_username_values)
        && !this.isValueContainedInFilters(currentUserDescr, filter_current_username_values)) {
        allJobs.splice(i, 1);
        continue;
      }

      const jobStatus = element.job.status();
      if (!this.isValueContainedInFilters(jobStatus, filter_status_values)) {
        allJobs.splice(i, 1);
        continue;
      }
    }
    return allJobs;
  });

  constructor(private bkService: BkService, private message_service: MessageHelperService, private sanitizer: DomSanitizer) {
  }

  clearJobNameFilter() {
    this.m_filter_jobname.update(() => '');
  }
  clearUserNameFilter() {
    this.m_filter_username.update(() => '');
  }
  clearCurrentUserNameFilter() {
    this.m_filter_current_username.update(() => '');
  }
  clearStatusFilter() {
    this.m_filter_status.update(() => '');
  }
  isBankLibrary(lib: string): boolean {
    if (lib.endsWith('UDT') || lib.endsWith('UST') || lib.endsWith('UTM') || lib.endsWith('UPC'))
      return true;
    return false;
  }
  isStarterProgram(pgm: string): boolean {
    if (pgm.startsWith('JOTCP') || pgm.endsWith('ZINAV') || pgm.endsWith('JOSERVICE'))
      return true;
    return false;
  }

  isValueContainedInFilters(val: string, filters: string[]): boolean {
    if (filters.length === 0 || (filters.length > 0 && filters[0].trim().length === 0))
      return true;
    let res = false;
    for (let flt_idx = 0; flt_idx < filters.length; flt_idx++) {
      let flt: string = filters[flt_idx].trim();
      if (flt.length > 0) {
        if (val.toUpperCase().indexOf(flt.toUpperCase()) >= 0) {
          {
            res = true;
            break;
          }
        }
      }
    }
    return res;
  }

  downloadAllJoblog() {
    let binaryData: BlobPart[] = [];
    let parms: { user: string, name: string, number: string }[] = [];
    this.bkService, this.joblist().forEach((value, index) => {
      parms.push({ user: value.job.user(), name: value.job.name(), number: value.job.number() })
    });
    this.getAllJobLogContent(parms).subscribe(
      (data) => {
        data.forEach(
          (ele) => {
            binaryData.push(ele + '\n');
          }
        );

        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: 'application/octet-stream' }));
        let dat = formatDate(new Date(), '_yyyy/MM/dd_hh_mm_ss', "en-US");
        let filename = 'allJobLogs' + dat + '.log';
        downloadLink.setAttribute('download', filename);
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
      }
    );
  }

  getAllJobLogContent(aJobs: { name: string, user: string, number: string }[]) {
    console.log('[1]-getAllJobLogContent', aJobs);
    this.m_curr_JobLogContent.set([]);
    return this.bkService.getJobLog(aJobs).pipe(
      tap(data => console.log('getAllJobLogContent content is [1]', JSON.stringify(data)))
    );
  }

  getJobLogContent(aJobUser: string, aJobName: string, aJobNumber: string) {
    console.log('[0]-getJobLogContent');
    this.m_curr_JobLogContent.set([]);
    let parms: { user: string, name: string, number: string }[] = [];
    parms.push({ user: aJobUser, name: aJobName, number: aJobNumber })
    return this.bkService.getJobLog(parms).pipe(
      tap(data => console.log('joblog content is [0]', JSON.stringify(data))),
      tap(data => {
        this.m_curr_JobLogName = aJobName;
        this.m_curr_JobLogUser = aJobUser;
        this.m_curr_JobLogNumber = aJobNumber;
        this.m_curr_JobLogContent.set(data);
      }
      )
    );
  }

  getJobLog(aJobUser: string, aJobName: string, aJobNumber: string) {
    console.log('[0]-getJobLog');
    this.getJobLogContent(aJobUser, aJobName, aJobNumber).subscribe(
      data => { console.log('joblog content is [2]', this.m_curr_JobLogContent) },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  getCurrentJobLogFile() {
    console.log('[0]-getCurrentJobLogFile');
    this.getJobLogFile(this.m_curr_JobLogUser, this.m_curr_JobLogName, this.m_curr_JobLogNumber);
  }

  getJobLogFile(aJobUser: string, aJobName: string, aJobNumber: string) {
    console.log('[0]-getJobLogFile');
    this.getJobLogContent(aJobUser, aJobName, aJobNumber).subscribe(
      data => {
        let binaryData: BlobPart[] = [];
        this.m_curr_JobLogContent().forEach(
          (ele) => {
            binaryData.push(ele + '\n');
          }
        );
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: 'application/octet-stream' }));
        downloadLink.setAttribute('download', this.m_curr_JobLogUser + '_' + this.m_curr_JobLogName + '_' + this.m_curr_JobLogNumber + '.log');
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }
  setJobLogVerbose(aJobUser: string, aJobName: string, aJobNumber: string) {
    console.log('[0]-setJobLogVerbose');
    this.bkService.setJoblogVerbose(aJobUser, aJobName, aJobNumber).subscribe(
      data => {
        console.log('setJobLogVerbose ok');
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }
  endJob(aJobUser: string, aJobName: string, aJobNumber: string) {
    if (!confirm('Sicuro di voler terminare il lavoro'))
      return;
    console.log('[0]-endJob');
    this.bkService.endJob(aJobUser, aJobName, aJobNumber).subscribe(
      data => {
        console.log(data);
        this.message_service.messageShow(this.message_service.msg_type.Success, 'Elaborazione completata con successo');
        this.updateList.emit(); // chiamo la funzione del componente padre per aggiornare la lista
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      })
  }

  loadDetailInfo(aListIem: JobListItemExtended) {
    // Se i dati del lavoro sono gia stati caricati non faccio nulla
    if (aListIem.jobDate().length > 0)
      return;
    console.log('[0]-loadDetailInfo', aListIem);
    this.bkService.getJobDetail(aListIem.job.user(), aListIem.job.name(), aListIem.job.number()).subscribe(
      data => {
        console.log('data is', data);
        let details: any = data;

        aListIem.jobDate.update(() => details['jobDate']);
        aListIem.cpuUsed.update(() => details['cpuUsed']);
        aListIem.tempStorageUsed.update(() => details['tempStorageUsed']);
        aListIem.subSystem.update(() => details['subSystem']);

        aListIem.statusExtended.update(() => details['statusExtended']);
        aListIem.jobSwitches.update(() => details['jobSwitches']);
        aListIem.loggingText.update(() => details['loggingText']);
        aListIem.loggingLevel.update(() => details['loggingLevel']);
        aListIem.loggingCLPrograms.update(() => details['loggingCLPrograms']);
        aListIem.job.function.update(() => details['function']);
        aListIem.libraryList.update(() => details['libraryList']);

        aListIem.openFiles.update(() => details['openFiles']);
        aListIem.callStack.update(() => details['callStack']);

        aListIem.userDescription.update(() => details['userDescription']);
        console.log('[1]-loadDetailInfo', aListIem);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  public toggleShowJobStatusInfo(aListIem: JobListItemExtended) {
    aListIem.showJobStatusInfo = !aListIem.showJobStatusInfo;
    if (aListIem.showJobStatusInfo === true) {
      aListIem.showJobLibraryListInfo = false;
      aListIem.showJobOpenFileInfo = false;
      aListIem.showJobCallStackInfo = false;
      this.loadDetailInfo(aListIem);
    }
  }
  public showJobStatusInfo(aListIem: JobListItemExtended) {
    aListIem.showJobStatusInfo = true;
    aListIem.showJobLibraryListInfo = false;
    aListIem.showJobOpenFileInfo = false;
    aListIem.showJobCallStackInfo = false;
    this.loadDetailInfo(aListIem);
  }
  public showJobLibraryListInfo(aListIem: JobListItemExtended) {
    aListIem.showJobLibraryListInfo = true;
    aListIem.showJobStatusInfo = false;
    aListIem.showJobOpenFileInfo = false;
    aListIem.showJobCallStackInfo = false;
    this.loadDetailInfo(aListIem);
  }
  public showJobOpenFileInfo(aListIem: JobListItemExtended) {
    aListIem.showJobOpenFileInfo = true;
    aListIem.showJobStatusInfo = false;
    aListIem.showJobLibraryListInfo = false;
    aListIem.showJobCallStackInfo = false;
    this.loadDetailInfo(aListIem);
  }

  public showJobCallStackInfo(aListIem: JobListItemExtended) {
    aListIem.showJobCallStackInfo = true;
    aListIem.showJobStatusInfo = false;
    aListIem.showJobLibraryListInfo = false;
    aListIem.showJobOpenFileInfo = false;
    this.loadDetailInfo(aListIem);
  }
  public refreshDetails(aListIem: JobListItemExtended) {
    aListIem.jobDate.update(() => '');
    this.loadDetailInfo(aListIem);
  }

}

