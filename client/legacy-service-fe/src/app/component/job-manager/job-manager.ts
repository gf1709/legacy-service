import { Component, signal, WritableSignal } from '@angular/core';
import { BkService, JobListItemExtended } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { DomSanitizer } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
// import { JobListViewer } from "../job-list-viewer/job-list-viewer";
import { JobListViewer } from '../job-list-viewer/job-list-viewer';

@Component({
  selector: 'app-job-manager',
  imports: [FormsModule, JobListViewer],
  templateUrl: './job-manager.html',
  styleUrl: './job-manager.css',
})
export class JobManager {

  constructor(private bkService: BkService, private message_service: MessageHelperService, private sanitizer: DomSanitizer) {
  }

  m_job_list: WritableSignal<JobListItemExtended[]> = signal([]);
  m_job_filter_jobName: string = '';
  m_job_filter_jobStatus: string = '';
  m_job_filter_userName: string = '';
  m_job_filter_sortByJobName: boolean = false;
  m_job_filter_sortByJobStatus: boolean = false;

  public get m_can_search(): boolean {
    return (this.m_job_filter_userName + this.m_job_filter_jobName + this.m_job_filter_jobStatus).length > 1;
  }

  getJobList() {
    this.m_job_list.set([]);
    this.bkService.getJobList(this.m_job_filter_userName, this.m_job_filter_jobName, this.m_job_filter_sortByJobName, this.m_job_filter_sortByJobStatus).subscribe(
      data => {
        console.log(data);
        let jobItems: any = data;
        let newJobList: JobListItemExtended[] = [];
        jobItems.forEach((i: any) => {
          let newElementList: JobListItemExtended = new JobListItemExtended();
          newElementList.job.name.update(() => i['name']);
          newElementList.job.number.update(() => i['number']);
          newElementList.job.status.update(() => i['status']);
          newElementList.job.user.update(() => i['user']);
          newElementList.job.userDescription.update(() => i['userDescription']);
          newElementList.job.currentUser.update(() => i['currentUser']);
          newElementList.job.currentUserDescription.update(() => i['currentUserDescription']);
          newElementList.job.function.update(() => i['function']);
          newElementList.job.remoteAddresses.update(() => i['remoteAddresses']);
          newJobList.push(newElementList);
        });
        this.m_job_list.set(newJobList);
      }
      ,
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

}

