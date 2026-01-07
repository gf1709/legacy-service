import { Component, signal, WritableSignal } from '@angular/core';
import { MessageHelperService } from '../../services/message-helper.service';
import { BkService } from '../../services/bk.service';
import {CommonModule} from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';

@Component({
  selector: 'app-dsplog-manager',
  imports: [CommonModule, FormsModule],
  templateUrl: './dsplog-manager.html',
  styleUrl: './dsplog-manager.css',
})
export class DsplogManager {
  m_sql: string = this.getDSPLOGSql();

  readonly m_result: WritableSignal<any[]> = signal([]);

  constructor(private bkService: BkService, private message_service: MessageHelperService) {
  }
  getDSPLOGSql(): string {
    let start_date: Date = new Date();
    start_date.setHours(start_date.getHours() - 5);
    let end_date: Date = new Date();

    let res: string = "SELECT message_id AS MSGID, message_type AS MSGTYPE, severity AS SEV, message_timestamp as TIMESTAMP, from_program AS PGM, message_text as MESSAGE_TEXT, message_second_level_text as EXTENDED_MESSAGE_TXT, from_job_user || ' [' || from_user || ']' as JOBUSER, from_job_name AS JOBNAME, from_job_number AS JOBNUM \
\nFROM TABLE(QSYS2.HISTORY_LOG_INFO(START_TIME => '";
    res += this.formatDateTime(start_date) + "', END_TIME => '";
    res += this.formatDateTime(end_date) + "' ) ) AS X where 1 = 1  and severity > 0  order by message_timestamp desc";
    return res;
  }

  getJOURNALAuthorizationFailureSql(): string {
    let start_date: Date = new Date();
    start_date.setHours(start_date.getHours() - 5);
    let end_date: Date = new Date();

    let res: string =
      "SELECT ENTRY_TIMESTAMP as TIMESTAMP, SEQUENCE_NUMBER as SEQUENCE, RECEIVER_LIBRARY AS RECEIVER_LIB,  RECEIVER_NAME AS RECEIVER, journal_code AS JRNCODE, journal_entry_type AS JRNTYPE /*, COUNT_OR_RRN*/ \
\n,(JOB_NUMBER CONCAT '/' CONCAT TRIM(JOB_USER) CONCAT '/' CONCAT JOB_NAME) as JOB, trim(PROGRAM_NAME) || '/' ||  trim(PROGRAM_LIBRARY) AS PROGRAM /*, REFERENTIAL_CONSTRAINT, TRIGGER*/ \
\n,INTERPRET(substring(entry_data, 1, 32000)   as char(32000) CCSID 1144) as DES \
\nFROM TABLE (QSYS2.DISPLAY_JOURNAL(JOURNAL_LIBRARY=>'QSYS', JOURNAL_NAME=>'QAUDJRN', STARTING_RECEIVER_NAME=>'*CURCHAIN', STARTING_TIMESTAMP=>'";
    res += this.formatDateTime(start_date) + "', ENDING_TIMESTAMP=>'";
    res += this.formatDateTime(end_date) + "', JOURNAL_ENTRY_TYPES=>'AF' /* Authority failure*/ /*,job=>'420530/FCFCSR/SSFCBHHJS5'*/ )) AS JT ";
    res += "\n ORDER BY entry_timestamp ASC";
    return res;
  }
  getJOURNALFileChangesSql(): string {
    let start_date: Date = new Date();
    start_date.setHours(start_date.getHours() - 10);
    let end_date: Date = new Date();

    let res: string =
      "SELECT ENTRY_TIMESTAMP as TIMESTAMP, SEQUENCE_NUMBER as SEQUENCE, RECEIVER_LIBRARY AS RECEIVER_LIB,  RECEIVER_NAME AS RECEIVER, journal_code AS JRNCODE, journal_entry_type AS JRNTYPE /*, COUNT_OR_RRN*/ \
\n,USER_NAME as USER,(JOB_NUMBER CONCAT '/' CONCAT TRIM(JOB_USER) CONCAT '/' CONCAT JOB_NAME) as JOB, trim(PROGRAM_NAME) || '/' ||  trim(PROGRAM_LIBRARY) AS PROGRAM /*, REFERENTIAL_CONSTRAINT, TRIGGER*/ \
\n,INTERPRET(substring(entry_data, 1, 32000)   as char(32000) CCSID 1144) as DES \
\nFROM TABLE (QSYS2.DISPLAY_JOURNAL(JOURNAL_LIBRARY=>'LBCCSUJRN', JOURNAL_NAME=>'JRNCC', OBJECT_LIBRARY=>'LBCCSUDT', OBJECT_NAME=>'Z80' ,OBJECT_OBJTYPE=>'*FILE', OBJECT_MEMBER=>'*ALL', STARTING_RECEIVER_NAME=>'*CURCHAIN', STARTING_TIMESTAMP=>'";
    res += this.formatDateTime(start_date) + "', ENDING_TIMESTAMP=>'";
    res += this.formatDateTime(end_date) + "', JOURNAL_ENTRY_TYPES=>'*RCD' )) AS JT   ";
    res += "\n ORDER BY entry_timestamp ASC";
    return res;
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }
  formatDateTime(date: Date): string {
    const formattedDate = this.formatDate(date); // Reuse formatDate function
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${formattedDate} ${hours}:${minutes}:${seconds}`;
  }
  dsplog() {
    console.log("dsplog...", this.m_sql);
    this.m_result.set([]);
    var startDate = new Date();
    this.bkService.dsplog(this.m_sql).subscribe(
      data => {
        this.m_result.set(data);
        var endDate = new Date();
        // this.m_seconds = (endDate.getTime() - startDate.getTime()) / 1000;
        console.log("dsplog result is ", this.m_result());
        // this.addSqlToHistory(this.m_sql);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta', err);
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta: ' + err.error);
      }
    );
  }

  getColumns() {
    if (this.m_result().length > 0)
      return Object.keys(this.m_result()[0]);
    else
      return [];
  }
  hasResult() {
    return this.m_result.length > 0;
  }
  sourceLogChanged(value: any) {
    let selectedValue = value.target.value;
    console.log('sourceLogChanged. this.m_source_log is ', selectedValue);
    if (selectedValue === "0")
      this.m_sql = this.getDSPLOGSql();
    else if (selectedValue === "1")
      this.m_sql = this.getJOURNALAuthorizationFailureSql();
    else if (selectedValue === "2")
      this.m_sql = this.getJOURNALFileChangesSql();
  }
}


