import { Component, signal, WritableSignal } from '@angular/core';
import { BkService } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';


export class CSVRecord {
    public description: string= '';
    public nature: string= '';
    public entitlement: string= '';
    public code: string= '';
    public application: string= '';
}

@Component({
  selector: 'app-entitlement-code-generator',
  imports: [],
  templateUrl: './entitlement-code-generator.html',
  styleUrl: './entitlement-code-generator.css',
})
export class EntitlementCodeGenerator {

  records: WritableSignal<CSVRecord[]> = signal([]);

  constructor(private bkService: BkService, private message_service: MessageHelperService) {
  }

  createEntitlementCode() {
  }


  uploadListener($event: any): void {
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
        this.records.set(csvRecords);
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
    // this.csvReader.nativeElement.value = "";
    this.records.set([]);
  }


}
