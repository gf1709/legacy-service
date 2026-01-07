rem Lo script compila il progetto angular e il progetto spring boot ed esegue il deploy delle due componenti insieme
cd client\legacy-service-fe

rem  Compilazione del progetto angular e creazione della cartella C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\legacy-service-web\target
@REM nella configurazione development ci sono i puntamenti al locale
call ..\node_modules\.bin\ng build --verbose --configuration="development"

rem Lo script compila e fa il deploy del progetto
cd ..\..\legacy-service-web

rem  Compilazione del progetto spring boot 
call mvn clean validate compile install package 

start java -jar target\legacy-service-web-0.0.1-SNAPSHOT.jar
timeout /t 10
start http://localhost:24998