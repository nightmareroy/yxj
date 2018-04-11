@echo.

echo 清理并编译，大概需要30秒，请稍等...
call script\collector-gradle.bat

rem 当前时间作为包名，时间精确到分钟
set date=%Date:~0,4%%Date:~5,2%%Date:~8,2%%time:~0,2%%time:~3,2%
echo 正在为您打包，目标位置：dist/Collector%date%.tar.gz
@echo.

@echo.
echo 复制脚本与数据生成版本包...
rem 生成的目标位置
set target_dirs=dist\Collector%date%\
for %%i in (%target_dirs%) do (
	if not exist %%i (
		md %%i
	)
)
xcopy platform-collector\build\libs\platform-collector.jar %target_dirs% /g /e /q /y
xcopy platform-collector\src\main\resources %target_dirs% /g /e /q /y
xcopy platform-collector\script %target_dirs% /g /e /q /y

cd dist
set temp_tar=Collector%date%.tar
..\script\7z.exe a -ttar %temp_tar% Collector%date%
..\script\7z.exe a -tgzip %temp_tar%.gz %temp_tar%
del %temp_tar%
@echo.
echo 版本包[Collector%date%.tar.gz]已完成。
@echo.