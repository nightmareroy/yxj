@echo.

echo �������룬�����Ҫ30�룬���Ե�...
call script\loginserver-gradle.bat

rem ��ǰʱ����Ϊ������ʱ�侫ȷ������
set date=%Date:~0,4%%Date:~5,2%%Date:~8,2%%time:~0,2%%time:~3,2%
echo ����Ϊ�������Ŀ��λ�ã�dist/LoginServer%date%.tar.gz
@echo.

@echo.
echo ���ƽű����������ɰ汾��...
rem ���ɵ�Ŀ��λ��
set target_dirs=dist\LoginServer%date%\
for %%i in (%target_dirs%) do (
	if not exist %%i (
		md %%i
	)
)
xcopy platform-login\build\libs\platform-login.jar %target_dirs% /g /e /q /y
xcopy platform-login\script %target_dirs% /g /e /q /y

rem confĿ¼
set target_conf_dir=%target_dirs%conf
if not exist %target_conf_dir% (md %target_conf_dir%)
xcopy platform-login\conf %target_conf_dir% /g /e /q /y

cd dist
set temp_tar=LoginServer%date%.tar
..\script\7z.exe a -ttar %temp_tar% LoginServer%date%
..\script\7z.exe a -tgzip %temp_tar%.gz %temp_tar%
del %temp_tar%
@echo.
echo �汾��[LoginServer%date%.tar.gz]����ɡ�
@echo.