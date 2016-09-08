@ECHO OFF

SET PERCENT= 30
set MEM=0

FOR /F "delims= skip=1" %%i IN ('wmic computersystem get TotalPhysicalMemory') DO (
	set MEM=%%i
	goto STOP
)
:STOP
IF %MEM% GTR 0 (
	SET /A MEM /= 1024*1024*100/PERCENT
) ELSE (
	SET MEM=1024
)
cd /d %~dp0
java -jar -Xmx%mem%M -jar orbisgis.jar
pause
