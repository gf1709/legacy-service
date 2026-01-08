import { Component, signal, WritableSignal } from '@angular/core';
import { BkService, JobListItemExtended } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Params } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { JobListViewer } from '../job-list-viewer/job-list-viewer';

@Component({
  selector: 'app-netstat-job-info',
  imports: [FormsModule, JobListViewer],
  templateUrl: './netstat-job-info.html',
  styleUrl: './netstat-job-info.css',
})
export class NetstatJobInfo {

  m_job_filter_port: number = 0;
  m_job_filter_userName: string = '';
  m_job_filter_jobName: string = '';

  constructor(private bkService: BkService, private message_service: MessageHelperService, private sanitizer: DomSanitizer, private route: ActivatedRoute) {
  }

  input_port: string = '';
  m_job_list: WritableSignal<JobListItemExtended[]> = signal([]);

  ngOnInit() {
    this.route.queryParams.subscribe(
      (params: Params) => {
        let inputPort: number = params['port'];
        if (inputPort > 0) {
          this.m_job_filter_port = inputPort;
          this.netstat_job_info();
        }
      }
    )
  }

  netstat_job_info() {
    console.log('[0]-input_port', this.input_port);
    this.m_job_list.set([]);
    this.bkService.netstat_job_info(this.m_job_filter_port, this.m_job_filter_userName, this.m_job_filter_jobName).subscribe(
      data => {
        console.log('netstat_job_info data is', data);
        let newJobList: JobListItemExtended[] = [];
        let jobItems: any = data;
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
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }
}
