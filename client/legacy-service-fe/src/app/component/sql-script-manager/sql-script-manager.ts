import { Component, NgModule, signal, WritableSignal } from '@angular/core';
import { BkService } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { CommonModule } from '@angular/common';
import { FormsModule, NgModel } from '@angular/forms';


export enum View_Mode {
  VIEW_GRID = 'GRID',
  VIEW_CARD = 'CARD'
}


@Component({
  selector: 'app-sql-script-manager',
  imports: [CommonModule, FormsModule],
  templateUrl: './sql-script-manager.html',
  styleUrl: './sql-script-manager.css',
})
export class SqlScriptManager {

  m_sql: string = 'select * from anagrafe_gpm where rownum<10';
  m_recno: number = 50;
  m_result: WritableSignal<any[]> = signal([]);
  m_view_mode: string = View_Mode.VIEW_GRID;
  m_history_sql: WritableSignal<string[]> = signal([]);
  m_seconds: WritableSignal<number> = signal(0);

  constructor(private bkService: BkService, private message_service: MessageHelperService) {
    this.bkService.retrieveHistorySql().subscribe(
      data => {
        this.m_history_sql.set(data);
        console.log('retrieved history sql is', this.m_history_sql);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta retrieveHistorySql');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta retrieveHistorySql');
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
    return this.m_result().length > 0;
  }
  execSql() {
    console.log("execSql...", this.m_sql, this.m_recno);
    this.m_result.set([]);
    var startDate = new Date();
    this.bkService.openResultSet(this.m_sql, this.m_recno).subscribe(
      data => {
        console.log("data received:", data);
        this.m_result.set(data);
        var endDate = new Date();
        this.m_seconds.set((endDate.getTime() - startDate.getTime()) / 1000);
        console.log("execSql result is ", this.m_result());
        this.addSqlToHistory(this.m_sql);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta', err);
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta: ' + err.error);
      }
    );
  }
  showGridMode() {
    this.m_view_mode = View_Mode.VIEW_GRID;
  }
  showCardMode() {
    this.m_view_mode = View_Mode.VIEW_CARD;
  }
  isGridMode() {
    return this.m_view_mode === View_Mode.VIEW_GRID;
  }
  isCardMode() {
    return this.m_view_mode === View_Mode.VIEW_CARD;
  }
  addSqlToHistory(anSql: string) {
    let historyContainsSql: boolean = false;
    this.m_history_sql().forEach((sql, index) => {
      if (sql.trim() === anSql.trim())
        historyContainsSql = true;
    });

    if (!historyContainsSql) {
      let tempHistory = this.m_history_sql();
      tempHistory.push(anSql);
      tempHistory = tempHistory.slice().reverse();
      this.m_history_sql.set(tempHistory);
    }
  }
}
