import { SourceManager } from './../component/source-manager/source-manager';
import { Injectable, WritableSignal, inject, signal } from '@angular/core';
import { environment } from '../environment';
import { AnonymousSubject } from 'rxjs/internal/Subject';
import { catchError, of, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export class OpenFileItem {
  library: string = '';
  file: string = '';
  type: string = '';
  member: string = '';
  actgrp: string = '';
  writeCount: string = '';
  readCount: string = '';
  rrn: string = '';
};

export class CallStackItem {
  level: string = '';
  program: string = '';
  programLib: string = '';
  statement: string = '';
  instructionNumber: string = '';
  procedure: string = '';
  module: string = '';
  moduleLib: string = '';
};
export class JobListItem {
  name: WritableSignal<string>  = signal('');
  user: WritableSignal<string>  = signal('');
  userDescription: WritableSignal<string>  = signal('');
  currentUser: WritableSignal<string>  = signal('');
  currentUserDescription: WritableSignal<string>  = signal('');
  number: WritableSignal<string>  = signal('');
  status: WritableSignal<string>  = signal('');
  function: WritableSignal<string>  = signal('');
  remoteAddresses: WritableSignal<string[]> = signal([]);

  constructor() {}
};

export class JobListItemExtended {
  job: JobListItem;
  showJobStatusInfo: boolean;
  showJobLibraryListInfo: boolean;
  showJobOpenFileInfo: boolean;
  showJobCallStackInfo: boolean;

  jobDate: WritableSignal<string> = signal('')
  cpuUsed: WritableSignal<string> = signal('');
  tempStorageUsed: WritableSignal<string> = signal('');
  subSystem: WritableSignal<string> = signal('');
  statusExtended: WritableSignal<string> = signal('');
  loggingText: WritableSignal<string> = signal('');
  loggingLevel: WritableSignal<number> = signal(0);
  loggingCLPrograms: WritableSignal<string> = signal('');
  jobSwitches: WritableSignal<string> = signal('');
  libraryList: WritableSignal<string[]> = signal([]);
  openFiles: WritableSignal<OpenFileItem[]> = signal([]);
  callStack: WritableSignal<CallStackItem[]> = signal([]);
  userDescription: WritableSignal<string> = signal('');

  constructor() {
    this.job = new JobListItem();
    this.showJobStatusInfo = false;
    this.showJobLibraryListInfo = false;
    this.showJobOpenFileInfo = false;
    this.showJobCallStackInfo = false;
  };
};

export class ObjectDescription {
  library: string;
  name: string;
  type: string;
  attribute: string;
  description: string;

  public constructor(libreria: string, nome: string, tipo: string, description: string,) {
    this.library = libreria;
    this.name = nome;
    this.type = tipo;
    this.attribute = tipo;
    this.description = description;
  };
};

export class ObjectDescriptionDetail {
  library: string;
  name: string;
  type: string;
  description: string;
  creationDate: string;
  creationUser: string;
  changeDate: string;
  changeUser: string;
  attribute: string;
  owner: string;
  size: string;

  constructor() {
    this.library = '';
    this.name = '';
    this.type = '';
    this.description = '';
    this.creationDate = '';
    this.creationUser = '';
    this.changeDate = '';
    this.changeUser = '';
    this.attribute = '';
    this.owner = '';
    this.size = '';
  };
};


export class FFDResult {
  library: string | undefined;
  ddsName: string | undefined;
  fields: [
    {
      fieldNo: number;
      fieldName: string;
      fieldType: string;
      fieldLength: number;
      fieldScale: number;
      fieldDescription: string;
    }
  ] | undefined;
};


export class SourceListResult {
  sources: [
    {
      library: string
      sourceFile: string
      sourceMember: string
      sourceMemberDescription: string
    }
  ] | undefined;
};

export class SourceResult {
  library: string
  sourceFile: string
  sourceMember: string
  sourceType: string
  explodeCOPY: boolean
  source: string[];

  constructor() {
    this.library = '';
    this.sourceFile = '';
    this.sourceMember = '';
    this.sourceType = '';
    this.explodeCOPY = false;
    this.source = [];
  }
};

export class LibraryListItem {
  library: string = '';
  type: string = '';
  description: string = '';
};

export class SpoolFileItem {
  spoolfileName: string = '';
  spoolNumber: number = 0;
  status: string = '';
  creation_ts: string = '';
  userData: string = '';
  size: number = 0;
  pages: number = 0;
  jobName: string = '';
  jobUser: string = '';
  jobNumber: string = '';
  outputQueueName: string = '';
  outputQueueLibrary: string = '';
}

export class ProgramCallRequest {
  program: string = '';
  type: string = '';
  command: string = '';
  flagIO: string = '';
  dsin: string = '';
  dsout: string = '';
  cid: string = '';
  when: string = ProgramCallRequest.getCurrentDateTimeAsString();
  times:string=''
  values: {
    name: string,
    type: string,
    length: number,
    scale: number,
    value: string,
    description: string
  }[] = [];

  static getCurrentDateTimeAsString(): string {
    let d: Date = new Date();
    let res = d.getFullYear() + "." + (d.getMonth() + 1) + "." + d.getDate() + " " + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds()
    return res;
  }

  toString(): string {
    let res: string = '';
    res = this.program + this.type + this.command + this.flagIO + this.dsin + this.dsout;
    this.values.forEach(
      (ele) => { res = res + ele.name + ele.type + ele.length + ele.value }
    )
    return res;
  }
};

export class ProgramCallResponse {
  program: string = '';
  type: string = '';
  command: string = '';
  flagIO: string = '';
  dsin: string = '';
  dsout: string = '';
  cid: string = '';
  values: {
    name: string,
    type: string,
    length: number,
    scale: number
    value: string,
    description: string
  }[] = [];
  result: string = '';
};

export class ServizioSibank {
  port: string = "";
  name: string = "";
  enabled: string = "";
  program: string = "";
  targa: string = "";
  ambiente: string = "";
  abi: string = "";
};


export class IfsFile {
  type: string = '';
  name: string = '';
  size: number = 0;
  changeDate: string = '';
  public isSelected: boolean = false;
};
export class IfsFileListFileResult {
  directory: string = '';
  files: IfsFile[] = [];
};

export class IfsManagerSearchParams {
  directory: string = '/tmp/jotcpworker4';
  filePattern: string = '*';
};

export class ObjectManagerListFilterParams {
  libreria: string = '*LIBL';
  nome: string = '';
  tipo: string = '';
};
export class SourceManagerFilterParams {
  library: string = 'LIBFCSRC';
  file: string = 'QRPGSRC';
  member: string = 'ZR1*';
};
export class SpoolManagerFilterParams {
  userName: string = '';
  spoolJobNameFilter: string = '';
  spoolNameFilter: string = '';
  spoolDateFilter: string = '';
};

export class JobManagerSearchParams {
  jobUser: string = '';
  port: number = 0;
  jobName: string = '';
  sortByJobName: boolean = false;
  sortByJobStatus: boolean = false;
}

export class DspLogParams {
  m_sql_dsplog: string = '';
  m_sql_journal_auth: string = '';
  m_sql_journal_file_changes: string = '';
}

@Injectable({
  providedIn: 'root'
})

export class BkService {

  httpClient: HttpClient = inject(HttpClient);
  private ffdCache = new Map<string, FFDResult>();

  public g_objectManagerListFilterParams: ObjectManagerListFilterParams = new ObjectManagerListFilterParams();
  public g_ifsManagerSearchParams: IfsManagerSearchParams = new IfsManagerSearchParams();
  public g_sourceManagerFilterParams: SourceManagerFilterParams = new SourceManagerFilterParams();
  public g_spoolManagerFilterParams: SpoolManagerFilterParams = new SpoolManagerFilterParams();
  public g_jobManagerFilterParams: JobManagerSearchParams = new JobManagerSearchParams();
  public g_dspLogParams: DspLogParams = new DspLogParams();

  constructor() { }

  getServiziSibank() {
    console.log('[0] getServiziSibank');
    return this.httpClient.get<ServizioSibank[]>(environment.apiUrl + "/socket_service_info");
  }


  getWRKOBJ(alibraryName: string, anObjectName: string, anObjectType: string) {
    console.log('[0] getWRKOBJ. alibraryName:', alibraryName, ', anObjectName:', anObjectName, ',anObjectType:', anObjectType);
    var parms: { library: string, objectName: string, objectType: string } = {
      library: alibraryName, objectName: anObjectName, objectType: anObjectType
    };
    return this.httpClient.post<ObjectDescription[]>(environment.apiUrl + "/wrkobj", parms);
  }

  getDSPOBJD(alibraryName: string, anObjectName: string, anObjectType: string) {
    console.log('[0] getDSPOBJD. alibraryName:', alibraryName, ', anObjectName:', anObjectName, ',anObjectType:', anObjectType);
    var parms: { library: string, objectName: string, objectType: string } = {
      library: alibraryName, objectName: anObjectName, objectType: anObjectType
    };
    return this.httpClient.post<ObjectDescriptionDetail>(environment.apiUrl + "/dspobjd", parms);
  }

  getFFD(alibraryName: string, aFileName: string) {

    const cacheKey = alibraryName + "." + aFileName;

    // Check cache first
    if (this.ffdCache.has(cacheKey)) {
      console.log('[CACHE HIT] getFFD:' + cacheKey);
      return of(this.ffdCache.get(cacheKey)!); // return cached as Observable
    }

    console.log('[0] getFFD (cache miss). library:', alibraryName, ', file:', aFileName);
    var parms = { library: alibraryName, ddsName: aFileName };

    return this.httpClient.post<FFDResult>(environment.apiUrl + "/ffd", parms).pipe(
      tap(result => {
        // Store in cache on success
        this.ffdCache.set(cacheKey, result);
        console.log('[CACHE STORED] getFFD:' + cacheKey);
      }),
      catchError(err => {
        console.error('getFFD error:', err);
        throw err;
      })
    );
  }

  getSourceList(alibraryName: string, aFileName: string, aMemberName: string) {
    console.log('[0] getSourceList. alibraryName:', alibraryName, ', aFileName:', aFileName, ', aMemberName:', aMemberName);
    var parms: { library: string, sourceFile: string, sourceMember: string } = {
      library: alibraryName, sourceFile: aFileName, sourceMember: aMemberName
    };
    return this.httpClient.post<SourceListResult>(environment.apiUrl + "/get-source-list", parms);
  }
  getSource(alibraryName: string, aFileName: string, aMemberName: string, anExplodeCopy: boolean) {
    console.log('[0] getSource. alibraryName:', alibraryName, ', aFileName:', aFileName, ', aMemberName:', aMemberName, ',explodeCopy:', anExplodeCopy);
    var parms: { library: string, sourceFile: string, sourceMember: string, explodeCOPY: boolean } = {
      library: alibraryName, sourceFile: aFileName, sourceMember: aMemberName, explodeCOPY: anExplodeCopy
    };
    return this.httpClient.post<SourceResult>(environment.apiUrl + "/get-source", parms);
  }
  getLibraryList() {
    console.log('[0] getLibraryList');
    return this.httpClient.get<LibraryListItem[]>(environment.apiUrl + "/library-list");
  }
  removeLibraryFromLibraryList(alibraryName: string) {
    console.log('[0] removeLibraryFromLibraryList. alibraryName:', alibraryName);
    return this.httpClient.delete(environment.apiUrl + "/library-list/" + alibraryName);
  }
  addLibraryToLibraryList(alibraryName: string) {
    console.log('[0] addLibraryToLibraryList. alibraryName:', alibraryName);
    return this.httpClient.put(environment.apiUrl + "/library-list/" + alibraryName, null);
  }
  getSpoolFileList(username: string) {
    console.log('[0] getSpoolFileList', username);
    return this.httpClient.get<SpoolFileItem[]>(environment.apiUrl + "/spool-list/" + username);
  }
  getSpoolFileItem(jobName: string, jobuser: string, jobnumber: string, spoolName: string, spoolNumber: number) {
    let fullJobName = jobnumber + "-" + jobuser + "-" + jobName;
    console.log('[0] getSpoolFileItem');
    return this.httpClient.get<string[]>(environment.apiUrl + "/spool-list/" + fullJobName + "/" + spoolName + "/" + spoolNumber);
  }
  deleteSpoolFileItem(jobName: string, jobuser: string, jobnumber: string, spoolName: string, spoolNumber: number) {
    let fullJobName = jobnumber + "-" + jobuser + "-" + jobName;
    console.log('[0] deleteSpoolFileItem', fullJobName);
    return this.httpClient.delete<string[]>(environment.apiUrl + "/spool-list/" + fullJobName + "/" + spoolName + "/" + spoolNumber);
  }
  deleteAllSpools(username: string) {
    console.log('[0] deleteAllSpools');
    return this.httpClient.delete<string[]>(environment.apiUrl + "/spool-list/deleteAll/" + username.toLocaleUpperCase());
  }

  getJobList(aJobUser: string, aJobName: string, aSortByJobName: boolean, aSortByJobStatus: boolean) {
    console.log('[0] getJobList. aJobUser:', aJobName, ', aJobName:', aJobName, ', aSortByJobName:', aSortByJobName, ', aSortByJobStatus:', aSortByJobStatus);
    var parms: { userName: string, jobName: string, sortByJobName: boolean, sortByJobStatus: boolean } = {
      userName: aJobUser, jobName: aJobName, sortByJobName: aSortByJobName, sortByJobStatus: aSortByJobStatus
    };
    return this.httpClient.post(environment.apiUrl + "/wrkactjob", parms);
  }
  netstat_job_info(aPort: number, aJobUser: string, aJobName: string) {
    console.log('[0] netstat_job_info. aJobUser:', aJobName, ', aJobName:', aJobName, 'aPort', aPort);
    var parms: { port: number, userName: string, jobName: string } = {
      port: aPort, userName: aJobUser, jobName: aJobName
    };
    return this.httpClient.post<JobListItem[]>(environment.apiUrl + "/netstat_job_info", parms);
  }
  endJob(aJobUser: string, aJobName: string, aJobNumber: string) {
    console.log('[0] endJob. aJobUser:', aJobUser, ', aJobName:', aJobName, ', aJobNumber:', aJobNumber);
    var parms: { userName: string, jobName: string, jobNumber: string } = {
      userName: aJobUser, jobName: aJobName, jobNumber: aJobNumber
    };
    return this.httpClient.post(environment.apiUrl + "/endjob", parms);
  }

  getJobLog(aJobs: { name: string, user: string, number: string }[]) {
    console.log('[0] getJobLog. aJobUser:', aJobs);
    var parms: { userName: string, jobName: string, jobNumber: string }[] = [];
    aJobs.forEach(element => {
      parms.push({ userName: element.user, jobName: element.name, jobNumber: element.number })
    });
    return this.httpClient.post<string[]>(environment.apiUrl + "/getjoblog", parms);
  }

  setJoblogVerbose(aJobUser: string, aJobName: string, aJobNumber: string) {
    console.log('[0] setJoblogVerbose. name, user, number:', aJobUser, aJobUser, aJobNumber);
    var parms: { userName: string, jobName: string, jobNumber: string } = {
      userName: aJobUser, jobName: aJobName, jobNumber: aJobNumber
    };
    return this.httpClient.post<void>(environment.apiUrl + "/set-joblog-verbose", parms);
  }
  getJobDetail(aJobUser: string, aJobName: string, aJobNumber: string) {
    console.log('[0] getJobDetail. aJobUser:', aJobUser, ', aJobName:', aJobName, ', aJobNumber:', aJobNumber);
    var parms: { userName: string, jobName: string, jobNumber: string } = {
      userName: aJobUser, jobName: aJobName, jobNumber: aJobNumber
    };
    return this.httpClient.post(environment.apiUrl + "/getjobdetail", parms);
  }
  callProgram(input: ProgramCallRequest) {
    console.log('[0] callProgram. input is :', input);
    return this.httpClient.post<ProgramCallResponse>(environment.apiUrl + "/call-program", input);
  }
  getSession() {
    console.log('[0] getSession.');
    return this.httpClient.get(environment.apiUrl + "/session");
  }

  saveHistoryCall(calls: ProgramCallRequest[]) {
    console.log('[0] saveHistoryCall.', calls);
    return this.httpClient.post(environment.apiUrl + "/history-call-save", calls);
  }

  retrieveHistoryCall() {
    console.log('[0] retrieveHistoryCall.');
    return this.httpClient.get<ProgramCallRequest[]>(environment.apiUrl + "/history-call-retrieve");
  }
  logout() {
    console.log('[0] logout.');
    return this.httpClient.get(environment.apiUrl + "/logout");
  }

  createCDCTableDDL(aLibraryName: string, aFileName: string) {
    console.log('[0] createCDCTableDDL. aLibraryName:', aLibraryName, ', aFileName:', aFileName);
    var parms: { library: string, file: string } = {
      library: aLibraryName, file: aFileName
    };
    return this.httpClient.post<string[]>(environment.apiUrl + "/utility/create-cdc-table-DDL", parms);
  }

  openResultSet(aSql: string, aRecno: number) {
    console.log('[0] openResultSet. aSql:', aSql, ', aRecno:', aRecno);
    var parms: { sql: string, recno: number } = {
      sql: aSql, recno: aRecno
    };
    return this.httpClient.post<any[]>(environment.apiUrl + "/open-resultset", parms);
  }

  retrieveHistorySql() {
    console.log('[0] retrieveHistorySql.');
    return this.httpClient.get<string[]>(environment.apiUrl + "/history-sql-retrieve");
  }
  listIFSFiles(aDir: string, aPattern: string) {
    console.log('[0] listIFSFiles. aDir:', aDir, ', aPattern:', aPattern);
    var parms: { directory: string, pattern: string } = {
      directory: aDir, pattern: aPattern
    };
    return this.httpClient.post<IfsFileListFileResult>(environment.apiUrl + "/utility/listFiles", parms);
  }
  getIFSFileContent(aFileName: string) {
    console.log('[0] getIFSFileContent. aFileName:', aFileName);
    return this.httpClient.post<string[]>(environment.apiUrl + "/utility/getIFSFileContent", aFileName);
  }
  getIFSFileContentZipped(aFileName: string) {
    console.log('[0] getIFSFileContentZipped. aFileName:', aFileName);
    return this.httpClient.post<number[]>(environment.apiUrl + "/utility/getIFSFileContentZipped", aFileName);
  }
  getIFSFilesContent(aFileNames: string[]) {
    console.log('[0] getIFSFilesContent. aFileNames:', aFileNames);
    return this.httpClient.post<string[]>(environment.apiUrl + "/utility/getIFSFilesContent", aFileNames);
  }
  deleteIFSFile(aFileName: string) {
    console.log('[0] deleteIFSFile. aFileNames:', aFileName);
    return this.httpClient.post<boolean>(environment.apiUrl + "/utility/deleteIFSFile", aFileName);
  }
  deleteIFSFiles(aFileNames: string[]) {
    console.log('[0] deleteIFSFiles. aFileNames:', aFileNames);
    return this.httpClient.post<boolean>(environment.apiUrl + "/utility/deleteIFSFiles", aFileNames);
  }
  updateLegacyTermialAbiMapper() {
    console.log('[0] updateLegacyTermialAbiMapper');
    return this.httpClient.get<void>(environment.apiUrl + "/update_legacy_terminal_abi_mapper");
  }
  showISYDsInput(aInputString: string) {
    console.log('[0] showISYDsInput. aInputString:', aInputString);
    return this.httpClient.post<ProgramCallRequest[]>(environment.apiUrl + "/show-ISY-input", aInputString);
  }
  showISYDsOutput(aInputString: string) {
    console.log('[0] showISYDsOutput. aInputString:', aInputString);
    return this.httpClient.post<ProgramCallResponse[]>(environment.apiUrl + "/show-ISY-output", aInputString);
  }

  dsplog(aSql: string) {
    console.log('[0] dsplog. aSql:', aSql);
    var parms: { sql: string } = {
      sql: aSql
    };
    return this.httpClient.post<any[]>(environment.apiUrl + "/dsplog", parms);
  }
}
