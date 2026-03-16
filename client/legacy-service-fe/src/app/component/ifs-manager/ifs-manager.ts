import { AfterContentChecked, ChangeDetectorRef, Component, signal, WritableSignal } from '@angular/core';
import { IfsManagerSearchParams, ProgramCallRequest, ProgramCallResponse } from './../../services/bk.service';
import { BkService, IfsFile, IfsFileListFileResult, JobListItem, JobListItemExtended } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { formatDate } from '@angular/common';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import * as fflate from 'fflate';
import { AuthenticationService } from '../../services/authentication.service';

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
  lineContentShowFullLine: boolean = false;
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

  m_selectedRowIndex: number = -1;
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

  constructor(private bkService: BkService, private message_service: MessageHelperService,
    public cdRef: ChangeDetectorRef, public authService: AuthenticationService) {
  }

  ngAfterContentChecked(): void {
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
            console.error('errore in fase di esecuzione della richiest ngAfterContentChecked', err);
            this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
          }
        );
      }
    }

    if (this.m_isyDsOutputLineNumberToShow > 0) {
      let lineNumber = this.m_isyDsOutputLineNumberToShow;
      // this.m_currentRow = lineNumber;
      // console.log('ngAfterContentChecked[2] - m_currentRow:', this.m_currentRow);
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
            console.error('errore in fase di esecuzione della richiest ngAfterContentChecked', err);
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
    this.m_selectedRowIndex = -1;
    // this.m_job_list = [];
    this.bkService.listIFSFiles(this.ifsManagerSearchParams.directory, this.ifsManagerSearchParams.filePattern, this.ifsManagerSearchParams.fromDate, this.ifsManagerSearchParams.toDate).subscribe(
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
        this.m_allRowsSelected = false;
      }
      , err => {
        console.error('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  showFileContent(dir: string, fileName: string, idx: number) {
    let fileContent: IfsFileContent[] = [];
    let responseRows: number[] = [];

    this.m_requestResponseRows.set([]);
    this.m_currentRow = -1;
    let aFullFileName: string = this.getFullFileName(dir, fileName);
    this.m_current_file = { dir: dir, file: fileName, idx: idx };
    let index: number = 0;

    this.bkService.getIFSFileContentZipped(aFullFileName).subscribe(
      data => {
        data.forEach(
          (b64LineCompressed) => {
            let lines: string[] = this.decompressLines(b64LineCompressed);

            lines.forEach(
              (line) => {
                let ifsLine: IfsFileContent = new IfsFileContent();
                ifsLine.lineNumber = index + 1;
                ifsLine.lineContent = line;
                ifsLine.requestsShowDetails = false;
                ifsLine.responsesShowDetails = false;
                fileContent.push(ifsLine);
                if (this.isISYCallInputLine(line)
                  || this.isISYCallOutputLine(line)
                  || this.isXamMessageLine(line)
                ) {
                  responseRows.push(index);
                }
                index += 1;
              });
          })
        this.m_requestResponseRows.set(responseRows);
        this.m_file_content.set(fileContent);
      }
      , err => {
        console.error('errore in fase di esecuzione della richiesta showFileContent', err);
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  decompressLines(b64LineCompressed: string): string[] {
    var lines: string[] = [];
    var binaryString = atob(b64LineCompressed);
    var bytes = new Uint8Array(binaryString.length);
    for (var i = 0; i < binaryString.length; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    const decompressedStringBytes = fflate.decompressSync(bytes);
    const b64LineDecompressed = fflate.strFromU8(decompressedStringBytes);
    if (b64LineDecompressed.length > 0) {
      var tmpLines = b64LineDecompressed.split('\r\t\nGrEg\r\t\n');
      tmpLines.forEach((value) => {
        if (value.length > 0)  // Non torno le rihe vuote
          lines.push(value);
      })
    }
    return lines;
  }

  showFileContentWorking(dir: string, fileName: string, idx: number) {
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
            ifsLine.requestsShowDetails = false;
            ifsLine.responsesShowDetails = false;
            fileContent.push(ifsLine);
            if (this.isISYCallInputLine(line)
              || this.isISYCallOutputLine(line)
              || this.isXamMessageLine(line)
            ) {
              responseRows.push(index);
            }
          }
        );
        this.m_requestResponseRows.set(responseRows);
        this.m_file_content.set(fileContent);
      }
      , err => {
        console.error('errore in fase di esecuzione della richiesta showFileContent', err);
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  downloadFile(dir: string, fileName: string, idx: number) {
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
          }
        );
        this.m_file_content.set(fileContent);
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
        console.error('errore in fase di esecuzione della richiesta downloadFile', err);
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  downloadFileListedFileContent() {
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
        console.error('errore in fase di esecuzione della richiesta downloadFileListedFileContent', err);
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );

  }

  findSibankCall() {
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
        console.error('errore in fase di esecuzione della richiesta findSibankCall', err);
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );

  }

  sort(sortCol: string) {
    this.m_selectedRowIndex = -1;
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
  }

  deleteAllSelectedFiles() {
    let text = "Conferma la cancellazione dei file selezionati!\nPremi OK o Annulla.";
    if (confirm(text) === false) {
      return;
    }
    this.m_selectedRowIndex = -1;

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
        console.error('errore in fase di esecuzione della richiesta', err);
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta di cancellazione dei file');
      }
    );
  }

  deleteIFSFile(dir: string, fileName: string, idx: number) {
    let bFullFileName: string = this.getFullFileName(dir, fileName);
    // if (!confirm("Sicuro di voler cancellare il file " + bFullFileName))
    //   return;
    this.m_selectedRowIndex = -1;
    this.setCurrentFileEmpty();
    this.bkService.deleteIFSFile(bFullFileName).subscribe(
      (data) => {
        const tmpFiles = this.m_fileList().files;
        tmpFiles.splice(idx, 1);
        this.m_fileList.set({
          directory: this.m_fileList().directory,
          files: tmpFiles
        });
        this.message_service.messageShow(this.message_service.msg_type.Info, 'File ' + bFullFileName + " cancellato");
      },
      err => {
        console.error('errore in fase di esecuzione della richiesta', err);
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
    this.m_isyDsInputLineNumberToShow = idx;
  }

  showISYDsOutput(idx: number) {
    this.m_isyDsOutputLineNumberToShow = idx;
  }

  selectRow(idx: number, event: any) {
    if (event.shiftKey === false) {
      this.m_fileList().files[idx].isSelected = !this.m_fileList().files[idx].isSelected;
    }
    else {
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
    this.m_selectedRowIndex = -1;
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
  getRealMessageSize(size: number): string {
    return ((size / 5) - 50).toFixed(0);
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
      console.log('gotoLastCall[1] - m_currentRow:', this.m_currentRow);
      this.gotoRow();
    }
  }
  gotoNextCall() {
    if (this.m_requestResponseRows().length > 0) {
      this.m_currentRow = this.m_currentRow + 1;
      console.log('gotoNextCall[1] - m_currentRow:', this.m_currentRow);
      if (this.m_currentRow >= this.m_requestResponseRows().length || this.m_currentRow < 0)
        this.m_currentRow = 0;
      this.gotoRow();
    }
  }
  gotoPreviousCall() {
    if (this.m_requestResponseRows().length > 0) {
      this.m_currentRow = this.m_currentRow - 1;
      console.log('gotoPreviousCall[1] - m_currentRow:', this.m_currentRow);
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
    console.log('gotoRowNr[1] - m_currentRow:', this.m_currentRow);
    let nextRowId: string = this.m_requestResponseRows()[idx].toString();
    const elmnt = document.getElementById(nextRowId);
    elmnt?.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'nearest' });
  }
  toggleShowFullLine(line: IfsFileContent) {
    line.lineContentShowFullLine = !line.lineContentShowFullLine;
  }

  split(dir: string, fileName: string, idx: number) {
    this.bkService.splitIFSFile(dir + '/' + fileName).subscribe(
      (result) => {
        console.log('splitIFSFile result:', result);
        this.ifsManagerSearchParams.filePattern = fileName + '*';
        this.listFiles();
      },
      (error) => {
        console.error('Error splitting IFS file:', error);
      }
    );
  }
  clickRow(i: number) {
    this.m_selectedRowIndex = i;
  }

}

