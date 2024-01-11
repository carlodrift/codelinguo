@echo off
mvn clean package
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
jpackage --input target\ --name CodeLinguo --main-jar codelinguo-1.0.jar --main-class fr.unilim.saes5.MainKt --type exe
