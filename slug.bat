echo off
set CLASSPATH=slug.jar
set CLASSPATH=%CLASSPATH%;lib/antlr-2.7.5.jar
set CLASSPATH=%CLASSPATH%;lib/arq.jar
set CLASSPATH=%CLASSPATH%;lib/commons-logging.jar
set CLASSPATH=%CLASSPATH%;lib/concurrent.jar
set CLASSPATH=%CLASSPATH%;lib/icu4j_3_4.jar
set CLASSPATH=%CLASSPATH%;lib/jakarta-oro-2.0.8.jar
set CLASSPATH=%CLASSPATH%;lib/jena.jar
set CLASSPATH=%CLASSPATH%;lib/log4j-1.2.12.jar
set CLASSPATH=%CLASSPATH%;lib/stax-1.1.1-dev.jar
set CLASSPATH=%CLASSPATH%;lib/stax-api-1.0.jar
set CLASSPATH=%CLASSPATH%;lib/xercesImpl.jar
set CLASSPATH=%CLASSPATH%;lib/xml-apis.jar
set CLASSPATH=%CLASSPATH%;lib/mysql-connector-java-3.1.8-bin.jar

java -classpath %CLASSPATH% -Djava.util.logging.config.file=etc\logging.properties com.ldodds.slug.Scutter %1 %2 %3 %4 %5 %6 %7 %8 %9
