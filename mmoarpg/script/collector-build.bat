@echo.

echo �������룬�����Ҫ30�룬���Ե�...
call script\collector-gradle.bat

rem ��ǰʱ����Ϊ������ʱ�侫ȷ������
set date=%Date:~0,4%%Date:~5,2%%Date:~8,2%%time:~0,2%%time:~3,2%
echo ����Ϊ�������Ŀ��λ�ã�dist/Collector%date%.tar.gz
@echo.

@echo.
echo ���ƽű����������ɰ汾��...
rem ���ɵ�Ŀ��λ��
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
echo �汾��[Collector%date%.tar.gz]����ɡ�
@echo.