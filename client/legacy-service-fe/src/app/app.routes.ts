import { AuthGaurdService } from './services/auth-gaurd.service';
import { Routes } from '@angular/router';
import { Login } from './component/login/login';
import { ObjectManager } from './component/object-manager/object-manager';
import { SourceManager } from './component/source-manager/source-manager';
import { SpoolManager } from './component/spool-manager/spool-manager';
import { JobManager } from './component/job-manager/job-manager';
import { NetstatJobInfo } from './component/netstat-job-info/netstat-job-info';
import { ServiziSibankManager } from './component/servizi-sibank-manager/servizi-sibank-manager';
import { LibraryListManager } from './component/library-list-manager/library-list-manager';
import { ZztrutManager } from './component/zztrut-manager/zztrut-manager';
import { SessionManager } from './component/session-manager/session-manager';
import { IfsManager } from './component/ifs-manager/ifs-manager';
import { DsplogManager } from './component/dsplog-manager/dsplog-manager';
import { CdcTableCreationManager } from './component/cdc-table-creation-manager/cdc-table-creation-manager';
import { SqlScriptManager } from './component/sql-script-manager/sql-script-manager';
import { JavaClassGenerator } from './component/java-class-generator/java-class-generator';

export const routes: Routes = [
  { path: '', component: Login },
  { path: 'login', component: Login },
  { path: 'object-manager', component: ObjectManager, canActivate: [AuthGaurdService] },
  { path: 'source-manager', component: SourceManager, canActivate: [AuthGaurdService] },
  { path: 'spool-manager', component: SpoolManager, canActivate: [AuthGaurdService] },
  { path: 'job-manager', component: JobManager, canActivate: [AuthGaurdService] },
  { path: 'library-list-manager', component: LibraryListManager, canActivate: [AuthGaurdService] },
  { path: 'zztrut-manager', component: ZztrutManager, canActivate: [AuthGaurdService] },
  { path: 'session-manager', component: SessionManager, canActivate: [AuthGaurdService] },
  { path: 'cdc-table-creation', component: CdcTableCreationManager, canActivate: [AuthGaurdService] },
  { path: 'sql-script-manager', component: SqlScriptManager, canActivate: [AuthGaurdService] },
  { path: 'netstat-job-info', component: NetstatJobInfo, canActivate: [AuthGaurdService] },
  { path: 'servizi-sibank', component: ServiziSibankManager, canActivate: [AuthGaurdService] },
  { path: 'ifs-manager', component: IfsManager, canActivate: [AuthGaurdService] },
  { path: 'dsplog-manager', component: DsplogManager, canActivate: [AuthGaurdService] },
  { path: 'java-class-generator', component: JavaClassGenerator, canActivate: [AuthGaurdService] },
];
