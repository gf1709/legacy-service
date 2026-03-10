cd C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service
mkdir client

cd client
**Angular 21**
- npm install @angular/cli @angular/core 
- npm install @angular-devkit/build-angular
- npm install bootstrap
- npm install ngx-toastr 
- npm install @angular/router @angular/forms
- npm install bootstrap-icons
- npm install jwt-decode
- npm install jszip
- npm i @types/file-saver

node_modules\.bin\ng new legacy-service-fe
npm install ngx-toastr --save
npm install @angular/animations --save


// Creazione servizi
cd C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\legacy-service-fe\src\app\services
C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng g s bk.service
C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng g s authentication.service
C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng g s auth-gaurd.service
C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng g s loader.service
C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng g s message-helper.service
C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng g s basic-auth-interceptor.service


// Creazione componenti
cd C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\legacy-service-fe\src\app\component
    C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng g c spinner
    C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng g c login
    C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng g c source-manager
    C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng g c spool-manager
cd C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\legacy-service-fe\src\app\component
    npx ng g c job-list-viewer
    npx ng g c netstat-job-info
    npx ng g c library-list-manager
    npx ng g c sql-script-manager
    npx ng g c cdc-table-creation-manager


// avvio applicazionne
C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\client\node_modules\.bin\ng serve
oppure 
1. cd C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service
2. npx ng serve