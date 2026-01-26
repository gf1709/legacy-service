import { AfterContentChecked, ChangeDetectorRef, Component, signal, WritableSignal } from '@angular/core';
import { IfsManagerSearchParams, ProgramCallRequest, ProgramCallResponse } from './../../services/bk.service';
import { BkService, IfsFile, IfsFileListFileResult, JobListItem, JobListItemExtended } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { formatDate } from '@angular/common';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';

enum SortMode {
  Name_ASC,
  Name_DESC,
  Date_ASC,
  Date_DESC,
  Type_ASC,
  Type_DESC,
  Size_ASC,
  Size_DESC,
  undefined
}
class IfsFileContent {
  lineNumber: number = 0;
  lineContent: string = '';
  requests: ProgramCallRequest[] = [];
  requestsShowDetails: boolean = false;
  responses: ProgramCallResponse[] = [];
  responsesShowDetails: boolean = false;
}
@Component({
  selector: 'app-ifs-manager',
  imports: [CommonModule, FormsModule],
  templateUrl: './ifs-manager.html',
  styleUrl: './ifs-manager.css',
})
export class IfsManager implements AfterContentChecked {

  m_fileList: WritableSignal<IfsFileListFileResult> = signal(new IfsFileListFileResult());
  m_current_file: { dir: string, file: string, idx: number } = { dir: '', file: '', idx: -1 };
  m_file_content: WritableSignal<IfsFileContent[]> = signal([]);
  m_sort_mode: SortMode = SortMode.Date_ASC;
  SortMode = SortMode; // per usarlo nell'html
  m_allRowsSelected: boolean = false;

  m_isyDsInputLineNumberToShow: number = -1;
  m_isyDsOutputLineNumberToShow: number = -1;

  m_currentRow: number = -1;
  m_requestResponseRows: WritableSignal<number[]> = signal([]);

  locationName: string = '399';

  constructor(private bkService: BkService, private message_service: MessageHelperService, public cdRef: ChangeDetectorRef) {
  }

  ngAfterContentChecked(): void {
    console.log('ngAfterContentChecked');

    if (this.m_isyDsInputLineNumberToShow > 0) {
      let lineNumber = this.m_isyDsInputLineNumberToShow;
      this.m_isyDsInputLineNumberToShow = -1;
      if (this.m_file_content()[lineNumber].requests.length === 0) {
        let line: string = this.m_file_content()[lineNumber].lineContent;
        this.bkService.showISYDsInput(line).subscribe(
          data => {
            this.m_file_content.update(
              (values) => {
                values[lineNumber].requests = data;
                return values;
              }
            );
            this.cdRef.detectChanges();
          }
          , err => {
            console.log('errore in fase di esecuzione della richiesta');
            this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
          }
        );
      }
    }

    if (this.m_isyDsOutputLineNumberToShow > 0) {
      let lineNumber = this.m_isyDsOutputLineNumberToShow;
      this.m_isyDsOutputLineNumberToShow = -1;
      if (this.m_file_content()[lineNumber].responses.length === 0) {
        let line: string = this.m_file_content()[lineNumber].lineContent;
        this.bkService.showISYDsOutput(line).subscribe(
          data => {
            this.m_file_content.update(
              (values) => {
                values[lineNumber].responses = data;
                return values;
              }
            );
            this.cdRef.detectChanges();
          }
          , err => {
            console.log('errore in fase di esecuzione della richiesta');
            this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
          }
        );
      }
    }

  }

  get ifsManagerSearchParams(): IfsManagerSearchParams {
    return this.bkService.g_ifsManagerSearchParams;
  }
  set ifsManagerSearchParams(value: IfsManagerSearchParams) {
    this.bkService.g_ifsManagerSearchParams = value;
  }

  setCurrentFileEmpty() {
    this.m_current_file = { dir: '', file: '', idx: -1 };
    this.m_file_content.set([]);
  }

  listFiles() {
    console.log('[0]-listFiles', this.ifsManagerSearchParams.directory, this.ifsManagerSearchParams.filePattern);
    // this.m_job_list = [];
    this.bkService.listIFSFiles(this.ifsManagerSearchParams.directory, this.ifsManagerSearchParams.filePattern).subscribe(
      data => {
        this.m_fileList.set(data);
        this.ifsManagerSearchParams.directory = this.m_fileList().directory;
        if (this.m_fileList().files.length > 0) {
          let parentdir: IfsFile = new IfsFile();
          parentdir.name = '..';
          parentdir.type = 'd';
          parentdir.changeDate = '';
          parentdir.size = 0;
          this.m_fileList().files.unshift(parentdir);
        }
        // console.log('listFiles data is', this.m_fileList());
        this.m_allRowsSelected = false;
      }
      , err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  showFileContent(dir: string, fileName: string, idx: number) {
    console.log('getFile:', dir, fileName);
    this.m_requestResponseRows.set([]);
    this.m_currentRow = -1;
    let aFullFileName: string = this.getFullFileName(dir, fileName);
    this.m_current_file = { dir: dir, file: fileName, idx: idx };
    this.bkService.getIFSFileContent(aFullFileName).subscribe(
      data => {
        let fileContent: IfsFileContent[] = [];
        let responseRows: number[] = [];
        data.forEach(
          (line, index) => {
            let ifsLine: IfsFileContent = new IfsFileContent();
            ifsLine.lineNumber = index + 1;
            ifsLine.lineContent = line;
            ifsLine.requestsShowDetails=false;
            ifsLine.responsesShowDetails=false;
            fileContent.push(ifsLine);
            if (this.isISYCallInputLine(line)
              || this.isISYCallOutputLine(line)
              || this.isXamMessageLine(line)
            ) {
              responseRows.push(index);
            }
            // console.log('showFileContent new ele is ', ifsLine);
          }
        );
        this.m_requestResponseRows.set(responseRows);
        this.m_file_content.set(fileContent);
        // console.log('getFile data is', this.m_file_content());
      }
      , err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }
  downloadFile(dir: string, fileName: string, idx: number) {
    console.log('downloadFile:', dir, fileName);
    let aFullFileName: string = this.getFullFileName(dir, fileName);
    this.m_current_file = { dir: dir, file: fileName, idx: idx };
    this.bkService.getIFSFileContent(aFullFileName).subscribe(
      data => {
        let binaryData: BlobPart[] = [];
        let fileContent: IfsFileContent[] = [];
        data.forEach(
          (line, index) => {
            let ifsLine: IfsFileContent = new IfsFileContent();
            ifsLine.lineNumber = index + 1;
            ifsLine.lineContent = line;
            fileContent.push(ifsLine);
            // console.log('downloadFile new ele is ', ifsLine);
          }
        );
        this.m_file_content.set(fileContent);
        // console.log('downloadFile data is', this.m_file_content);
        this.m_file_content().forEach(
          (ele) => {
            binaryData.push(ele.lineContent + '\n');
          }
        );
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: 'application/octet-stream' }));
        downloadLink.setAttribute('download', aFullFileName);
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
      }
      , err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  downloadFileListedFileContent() {
    console.log('downloadFileListedFileContent:');
    let fNames: string[] = [];
    this.m_fileList().files.forEach(
      (ele) => {
        if (ele.type === 'f' && ele.isSelected)
          fNames.push(this.ifsManagerSearchParams.directory + "/" + ele.name);
      }
    );
    this.bkService.getIFSFilesContent(fNames).subscribe(
      data => {
        let binaryData: BlobPart[] = [];
        // console.log('downloadFile data is', data);
        data.forEach(
          (line) => {
            binaryData.push(line + '\n');
          }
        );
        let now = formatDate(new Date(), '_yyyy/MM/dd_hh_mm_ss', "en-US");
        let downloadedFileName = 'ifsfiles_content_' + now + '.log';

        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: 'application/octet-stream' }));
        downloadLink.setAttribute('download', downloadedFileName);
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
      }
      , err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );

  }

  findSibankCall() {
    console.log('findSibankCall:');
    let fNames: string[] = [];
    this.m_fileList().files.forEach(
      (ele) => {
        if (ele.type === 'f' && ele.isSelected)
          fNames.push(this.ifsManagerSearchParams.directory + "/" + ele.name);
      }
    );

    this.bkService.findSibankCall(fNames).subscribe(
      data => {
        alert(data);
      }
      , err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );

  }

  sort(sortCol: string) {
    console.log('[1] sort, this.m_sort_mode', sortCol, this.m_sort_mode);
    if (sortCol === 'name') {

      if (this.m_sort_mode === SortMode.Name_ASC) {
        this.m_fileList.update(previousVal => {
          return {
            directory: previousVal.directory,
            files: previousVal.files.sort((a, b) => (a.name > b.name ? -1 : 1))
          };
        });
      }
      else {
        this.m_fileList.update(previousVal => {
          return {
            directory: previousVal.directory,
            files: previousVal.files.sort((a, b) => (a.name < b.name ? -1 : 1))
          };
        });
      }
    }


    else if (sortCol === 'date') {
      if (this.m_sort_mode === SortMode.Date_ASC) {
        this.m_fileList.update(
          previousVal => {
            return {
              directory: previousVal.directory,
              files: previousVal.files.sort((a, b) => (a.changeDate > b.changeDate ? -1 : 1))
            };
          }
        );
        this.m_sort_mode = SortMode.Date_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_fileList.update(
          previousVal => {
            return {
              directory: previousVal.directory,
              files: previousVal.files.sort((a, b) => (a.changeDate < b.changeDate ? -1 : 1))
            };
          }
        );
        this.m_sort_mode = SortMode.Date_ASC; // l'ordinamento fatto e' crescente
      }
    }
    else if (sortCol === 'type') {
      if (this.m_sort_mode === SortMode.Type_ASC) {
        this.m_fileList.update(
          previousVal => {
            return {
              directory: previousVal.directory,
              files: previousVal.files.sort((a, b) => (a.type > b.type ? -1 : 1))
            };
          }
        );
        this.m_sort_mode = SortMode.Type_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_fileList.update(
          previousVal => {
            return {
              directory: previousVal.directory,
              files: previousVal.files.sort((a, b) => (a.type < b.type ? -1 : 1))
            };
          }
        );
        this.m_sort_mode = SortMode.Type_ASC; // l'ordinamento fatto e' crescente
      }
    }
    else if (sortCol === 'size') {
      if (this.m_sort_mode === SortMode.Size_ASC) {
        this.m_fileList.update(
          previousVal => {
            return {
              directory: previousVal.directory,
              files: previousVal.files.sort((a, b) => (a.size > b.size ? -1 : 1))
            };
          }
        );
        this.m_sort_mode = SortMode.Size_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_fileList.update(
          previousVal => {
            return {
              directory: previousVal.directory,
              files: previousVal.files.sort((a, b) => (a.size < b.size ? -1 : 1))
            };
          }
        );
        this.m_sort_mode = SortMode.Size_ASC; // l'ordinamento fatto e' crescente
      }
    }
    console.log('[2] sort, this.m_sort_mode', sortCol, this.m_sort_mode);
  }

  deleteAllSelectedFiles() {
    console.log('deleteAllListedFiles:');
    let bFullFileNamesToDelete: string[] = [];
    let bFullFileIndexesToDelete: number[] = [];
    for (let idx: number = 0; idx < this.m_fileList().files.length; idx++) {
      let ele: IfsFile = this.m_fileList().files[idx];
      if (ele.type === 'f' && ele.isSelected) {
        bFullFileNamesToDelete.push(this.getFullFileName(this.ifsManagerSearchParams.directory, ele.name));
        bFullFileIndexesToDelete.push(idx);
      }
    }
    bFullFileIndexesToDelete.reverse()

    this.bkService.deleteIFSFiles(bFullFileNamesToDelete).subscribe(
      (data) => {
        console.log('ifs files deleted ', bFullFileNamesToDelete, data);
        const tmpFiles = this.m_fileList().files;
        for (let idx: number = 0; idx < bFullFileIndexesToDelete.length; idx++) {
          tmpFiles.splice(bFullFileIndexesToDelete[idx], 1);
        }
        this.m_fileList.set({
          directory: this.m_fileList().directory,
          files: tmpFiles
        });
        this.message_service.messageShow(this.message_service.msg_type.Info, 'File cancellati');
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta di cancellazione dei file');
      }
    );
  }

  deleteIFSFile(dir: string, fileName: string, idx: number) {
    let bFullFileName: string = this.getFullFileName(dir, fileName);
    // if (!confirm("Sicuro di voler cancellare il file " + bFullFileName))
    //   return;
    this.setCurrentFileEmpty();
    console.log('delete', dir, fileName);
    this.bkService.deleteIFSFile(bFullFileName).subscribe(
      (data) => {
        console.log('ifs file deleted ', bFullFileName, data);
        const tmpFiles = this.m_fileList().files;
        tmpFiles.splice(idx, 1);
        this.m_fileList.set({
          directory: this.m_fileList().directory,
          files: tmpFiles
        });
        this.message_service.messageShow(this.message_service.msg_type.Info, 'File ' + bFullFileName + " cancellato");
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta di cancellazione del file');
      }
    )
  }

  gotoDir(dir: string, fileName: string) {
    this.ifsManagerSearchParams.directory = this.getFullFileName(dir, fileName);
    this.listFiles();
  }

  getFullFileName(dir: string, fileName: string): string {
    let res: string = '';
    let parts: string[] = (dir + '/' + fileName).split('/');
    for (let i = 0; i < parts.length; i++) {
      let eleDir = parts[i].trim();
      if (eleDir.length > 0 && eleDir !== '..') {
        if (parts[i + 1] !== '..')
          res += '/' + eleDir;
      }
    }
    if (res.length == 0)
      res = '/';
    return res;
  }

  isXamMessage(idx: number): boolean {
    if (idx) {
      let line: string = this.m_file_content()[idx].lineContent;
      return this.isXamMessageLine(line);
    }
    return false;
  }
  isXamMessageLine(line: string): boolean {
    if ((line.indexOf('XAM Call - Messaggio in') > 0) || (line.indexOf('Protocollo - Messaggio XAM in') > 0))
      return true;
    return false;
  }
  isISYCallInput(idx: number): boolean {
    if (idx > 1) {
      let line: string = this.m_file_content()[idx - 1].lineContent;
      return this.isISYCallInputLine(line);
    }
    return false;
  }
  isISYCallInputLine(line: string): boolean {
    if ((line.indexOf('[5b] - Isy Call - Messaggio in ingresso') > 0)
      || (line.indexOf('[5b] - Protocollo - Messaggio ISY in ingresso') > 0))
      return true;
    return false;
  }

  isISYCallOutput(idx: number): boolean {
    if (idx) {
      let line: string = this.m_file_content()[idx - 1].lineContent;
      return this.isISYCallOutputLine(line)
    }
    return false;
  }
  isISYCallOutputLine(line: string): boolean {
    if ((line.indexOf('[5d] - Isy Call - Messaggio in uscita:') > 0)
      || (line.indexOf('[5d] - Protocollo - Messaggio ISY in uscita:') > 0))
      return true;
    return false;
  }
  showISYDsInput(idx: number) {
    console.log('showISYDsInput', idx);
    this.m_isyDsInputLineNumberToShow = idx;
  }

  showISYDsOutput(idx: number) {
    console.log('showISYDsOutput', idx);
    this.m_isyDsOutputLineNumberToShow = idx;
  }

  selectRow(idx: number, event: any) {
    console.log('selectRow', idx, event);
    if (event.shiftKey === false) {
      this.m_fileList().files[idx].isSelected = !this.m_fileList().files[idx].isSelected;
      console.log('selectRow-this.m_fileList().files[idx].isSelected:', this.m_fileList().files[idx].isSelected);
    }
    else {
      console.log('selectRow - shiftkey true.');
      this.m_fileList().files[idx].isSelected = true;
      let minSelectedIndex: number = 99999999;
      // Trovo dell'indice del primo elemento selezionato
      for (let ifsFileIdx: number = 0; ifsFileIdx < this.m_fileList().files.length; ifsFileIdx++) {
        if (this.m_fileList().files[ifsFileIdx].isSelected) {
          minSelectedIndex = ifsFileIdx;
          break;
        }
      }
      let maxSelectedIndex: number = -1;
      // Trovo dell'indice dell'ultimo elemento selezionato
      for (let ifsFileIdx: number = this.m_fileList().files.length - 1; ifsFileIdx >= 0; ifsFileIdx--) {
        if (this.m_fileList().files[ifsFileIdx].isSelected) {
          maxSelectedIndex = ifsFileIdx;
          break;
        }
      }
      console.log('selectRow - minSelectedIndex:', minSelectedIndex, '- maxSelectedIndex:', maxSelectedIndex);
      if (minSelectedIndex >= 0 && maxSelectedIndex < this.m_fileList().files.length) {
        for (let ifsFileIdx: number = minSelectedIndex; ifsFileIdx < maxSelectedIndex; ifsFileIdx++)
          if (this.m_fileList().files[ifsFileIdx].type === 'f')
            this.m_fileList().files[ifsFileIdx].isSelected = true;
          else
            this.m_fileList().files[ifsFileIdx].isSelected = false;
      }
    }
  }

  toggleSelectDeselectAllRows() {
    this.m_allRowsSelected = !this.m_allRowsSelected;
    for (let ifsFileIdx: number = this.m_fileList().files.length - 1; ifsFileIdx >= 0; ifsFileIdx--) {
      this.m_fileList().files[ifsFileIdx].isSelected = this.m_allRowsSelected;
    }
  }
  public get isAlmostOneFileSelected(): boolean {
    for (let ifsFileIdx: number = this.m_fileList().files.length - 1; ifsFileIdx >= 0; ifsFileIdx--) {
      if (this.m_fileList().files[ifsFileIdx].isSelected)
        return true;
    }
    return false;
  }

  hasInput(req: ProgramCallRequest): boolean {
    if (req === null || req.dsin === null || req.values === null || req.values.length === 0)
      return false;
    return true;
  }
  toggleShowInputDetails(line: IfsFileContent) {
    line.requestsShowDetails = !line.requestsShowDetails;
  }
  showInputDetails(line: IfsFileContent): boolean {
    return line.requestsShowDetails;
  }

  toggleShowOutputDetails(line: IfsFileContent) {
    line.responsesShowDetails = !line.responsesShowDetails;
  }
  showOutputDetails(line: IfsFileContent): boolean {
    return line.responsesShowDetails;
  }
  getRealMessageSize(size:number):string
  {
    return ((size/5)-50).toFixed(0);
  }

  gotoFirstCall() {
    if (this.m_requestResponseRows().length > 0) {
      this.m_currentRow = 0;
      this.gotoRow();
    }
  }
  gotoLastCall() {
    if (this.m_requestResponseRows().length > 0) {
      this.m_currentRow = this.m_requestResponseRows().length - 1;
      this.gotoRow();
    }
  }
  gotoNextCall() {
    if (this.m_requestResponseRows().length > 0) {
      this.m_currentRow = this.m_currentRow + 1;
      if (this.m_currentRow >= this.m_requestResponseRows().length || this.m_currentRow < 0)
        this.m_currentRow = 0;
      this.gotoRow();
    }
  }
  gotoPreviousCall() {
    if (this.m_requestResponseRows().length > 0) {
      this.m_currentRow = this.m_currentRow - 1;
      if (this.m_currentRow >= this.m_requestResponseRows().length || this.m_currentRow < 0)
        this.m_currentRow = 0;
      this.gotoRow();
    }
  }
  private gotoRow() {
    let nextRowId: string = this.m_requestResponseRows()[this.m_currentRow].toString();
    const elmnt = document.getElementById(nextRowId);
    elmnt?.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'nearest' });
  }
  public gotoRowNr(idx: number) {
    this.m_currentRow = idx;
    let nextRowId: string = this.m_requestResponseRows()[idx].toString();
    const elmnt = document.getElementById(nextRowId);
    elmnt?.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'nearest' });
  }

}

