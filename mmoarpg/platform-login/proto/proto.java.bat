@echo off
echo ---------------------gen pomelo files--------------------------------
set pomelo_java_dir=..\src\proto
if exist %pomelo_java_dir% rmdir /S/Q %pomelo_java_dir%\
mkdir %pomelo_java_dir%

for /f "delims=" %%i in ('dir /b/a "*.proto"') do .\protoc -I=. --java_out=%pomelo_java_dir% %%i

echo -----------------------all done ------------------------------------

@pause