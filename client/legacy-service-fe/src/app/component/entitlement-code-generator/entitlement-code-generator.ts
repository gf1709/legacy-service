import { Component, signal, WritableSignal } from '@angular/core';
import { BkService } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


export class CSVRecord {
  public description: string = '';
  public nature: string = '';
  public entitlement: string = '';
  public code: string = '';
  public application: string = '';
}

@Component({
  selector: 'app-entitlement-code-generator',
  imports: [FormsModule, CommonModule],
  templateUrl: './entitlement-code-generator.html',
  styleUrl: './entitlement-code-generator.css',
})
export class EntitlementCodeGenerator {

  m_records: WritableSignal<CSVRecord[]> = signal([]);
  m_toggleShowAddNewRecord: boolean = false;

  constructor(private bkService: BkService, private message_service: MessageHelperService) {
  }

  createEntitlementCode() {
  }


  uploadListener($event: any): void {
    this.m_toggleShowAddNewRecord = false;
    let text = [];
    let files = $event.srcElement.files;
    if (this.isValidCSVFile(files[0])) {
      let input = $event.target;
      let reader = new FileReader();
      reader.readAsText(input.files[0]);
      reader.onload = () => {
        let csvData = reader.result;
        let csvRecordsArray = (<string>csvData).split(/\r\n|\n/);
        let headersRow = this.getHeaderArray(csvRecordsArray);
        let csvRecords: CSVRecord[] = this.getDataRecordsArrayFromCSVFile(csvRecordsArray, headersRow.length);
        this.m_records.set(csvRecords);
      };
      reader.onerror = function () {
        console.log('error is occured while reading file!');
      };

    } else {
      alert("Please import valid .csv file.");
      this.fileReset();
    }
  }
  getDataRecordsArrayFromCSVFile(csvRecordsArray: any, headerLength: any) {
    let csvArr = [];
    for (let i = 1; i < csvRecordsArray.length; i++) {
      let curruntRecord = (<string>csvRecordsArray[i]).split(',');
      if (curruntRecord.length == headerLength) {
        let csvRecord: CSVRecord = new CSVRecord();
        csvRecord.description = curruntRecord[0].trim();
        csvRecord.nature = curruntRecord[1].trim();
        csvRecord.entitlement = curruntRecord[2].trim();
        csvRecord.code = curruntRecord[3].trim();
        csvRecord.application = curruntRecord[4].trim();
        csvArr.push(csvRecord);
      }
    }
    return csvArr;
  }
  isValidCSVFile(file: any) {
    return file.name.endsWith(".csv");
  }
  getHeaderArray(csvRecordsArr: any) {
    let headers = (<string>csvRecordsArr[0]).split(',');
    let headerArray = [];
    for (let j = 0; j < headers.length; j++) {
      headerArray.push(headers[j]);
    }
    return headerArray;
  }
  fileReset() {
    if (!confirm('Sicuro di voler eliminare tutti gli entitlement ?'))
      return
    this.m_records.set([]);
  }
  toggleNewEntitlement() {
    this.m_toggleShowAddNewRecord = !this.m_toggleShowAddNewRecord;
  }

  m_description: string = '';
  m_nature: string = '';
  m_entitlement: string = '';
  m_code: string = '';
  m_application: string = '';

  saveNewEntitlement() {
    let csvRecord: CSVRecord = new CSVRecord();
    csvRecord.description = this.m_description;
    csvRecord.nature = this.m_nature;
    csvRecord.entitlement = this.m_entitlement;
    csvRecord.code = this.m_code;
    csvRecord.application = this.m_application;
    console.log('saveNewEntitlement...', csvRecord);

    if (!csvRecord.description || !csvRecord.nature || !csvRecord.entitlement || !csvRecord.code || !csvRecord.application) {
      this.message_service.messageShow(this.message_service.msg_type.Error, 'All fields are required');
      return;
    }
    if (csvRecord.description.length < 3 || csvRecord.description.length > 20) {
      this.message_service.messageShow(this.message_service.msg_type.Error, 'Description must be between 3 and 20 characters');
      return;
    }
    if (csvRecord.nature.length < 3 || csvRecord.nature.length > 20) {
      this.message_service.messageShow(this.message_service.msg_type.Error, 'Nature must be between 3 and 20 characters');
      return;
    }
    if (csvRecord.entitlement.length < 3 || csvRecord.entitlement.length > 20) {
      this.message_service.messageShow(this.message_service.msg_type.Error, 'Entitlement must be between 3 and 20 characters');
      return;
    }
    if (csvRecord.code.length < 3 || csvRecord.code.length > 20) {
      this.message_service.messageShow(this.message_service.msg_type.Error, 'Code must be between 3 and 20 characters');
      return;
    }
    if (csvRecord.application.length < 3 || csvRecord.application.length > 20) {
      this.message_service.messageShow(this.message_service.msg_type.Error, 'Application must be between 3 and 20 characters');
      return;
    }
    this.m_records.update(records => [...records, csvRecord]);
    this.m_description = '';
    this.m_nature = '';
    this.m_entitlement = '';
    this.m_code = '';
    this.m_application = '';
    this.m_toggleShowAddNewRecord = !this.m_toggleShowAddNewRecord;

  }
}

