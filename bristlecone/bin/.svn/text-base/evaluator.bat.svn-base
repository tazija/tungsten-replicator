rem
rem Bristlecone-0.6
rem
rem
rem Bristlecone Cluster Test Tools
rem (c) 2006-2007 Continuent, Inc.. All rights reserved.

set JAVA_OPTS=-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4142,suspend=n
set BHOME=C:\Users\Edward\Desktop\bristlecone
set CP=.
set CP=%CP%;%BHOME%\lib-ext\hsqldb.jar
set CP=%CP%;%BHOME%\lib-ext\mysql-connector-java-5.0.8-bin.jar
set CP=%CP%;%BHOME%\lib\tungsten-commons.jar
set CP=%CP%;%BHOME%\lib-ext\tungsten-sqlrouter.jar
set CP=%CP%;%BHOME%\lib\log4j.jar
set CP=%CP%;%BHOME%\lib\bristlecone.jar
set CP=%CP%;%BHOME%\lib\jcommon-1.0.10.jar
set CP=%CP%;%BHOME%\lib\jfreechart-1.0.6.jar
set CP=%CP%;%BHOME%\config\evaluator
set CP=%CP%;%BHOME%\config
java %JAVA_OPTS% -cp "%CP%" com.continuent.bristlecone.evaluator.Evaluator -graph  %1
