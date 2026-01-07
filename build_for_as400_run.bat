rem Lo script compila il progetto angular e il progetto spring boot ed esegue il deploy delle due componenti insieme
cd client\legacy-service-fe

rem  Compilazione del progetto angular e creazione della cartella C:\Users\fc0382\Documents\Apps\ProgettiJava\legacy-service\legacy-service-web\target
call ..\node_modules\.bin\ng build --verbose --configuration="production"

rem Lo script compila e fa il deploy del progetto
cd ..\..\legacy-service-web

rem  Compilazione del progetto spring boot 
call mvn clean validate compile install package 
echo 'Copia in corso...'
copy ..\legacy-service-web\target\legacy-service-web-0.0.1-SNAPSHOT.jar n:\PFC1\JO\test\java\legacy_service
echo 'Compilazione e copia terminata !!!'
echo off

rem Far partire il programma utilizzare il comando:
rem java -jar legacy-service-web\target\legacy-service-web-0.0.1-SNAPSHOT

rem Per AS400 eseguire i comandi
rem CPY OBJ('/qntc/10.120.32.179/fccrt/PFC/PFC1/JO/test/java/legacy-service-web-0.0.1-SNAPSHOT') 
rem     TOOBJ('/tmp/legacy-service-web-0.0.1-SNAPSHOT')   REPLACE(*YES)
rem qsh + java -jar /tmp/legacy-service-web-0.0.1-SNAPSHOT
rem oppure 
rem     JAVA CLASS(legacy-service-web-0.0.1-SNAPSHOT) CLASSPATH('/tmp')
rem oppure  e
rem     java -jar  /qntc/10.120.32.179/fccrt/PFC/PFC1/JO/test/java/legacy-service-web-0.0.1-SNAPSHOT.jar
rem oppure  (Preferita !!!!)
rem     cd /qntc/10.120.32.179/fccrt/PFC/PFC1/JO/test/java/legacy_service  
rem     java -jar  legacy-service-web-0.0.1-SNAPSHOT.jar
rem oppure
rem JAVA                                                                           
rem CLASS('//qntc/10.120.32.179/fccrt/PFC/PFC1/JO/test/java/legacy-service-web-0.0.1
rem -SNAPSHOT.jar')                                                                 
rem CLASSPATH('/qntc/10.120.32.179/fccrt/PFC/PFC1/JO/test/java/')       
rem JOB(FCTXE)           
