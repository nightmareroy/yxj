@echo.

echo �������룬�����Ҫ30�룬���Ե�...
call script\usercenter-gradle.bat

rem ��ǰʱ����Ϊ������ʱ�侫ȷ������
set date=%Date:~0,4%%Date:~5,2%%Date:~8,2%%time:~0,2%%time:~3,2%
echo ����Ϊ�������Ŀ��λ�ã�dist/UserCenter%date%.tar.gz
@echo.

@echo.
echo ���ƽű����������ɰ汾��...
rem ���ɵ�Ŀ��λ��
set target_dirs=dist\UserCenter%date%\
for %%i in (%target_dirs%) do (
	if not exist %%i (
		md %%i
	)
)
xcopy platform-usercenter\build\libs\platform-usercenter.jar %target_dirs% /g /e /q /y
xcopy platform-usercenter\src\main\resources %target_dirs% /g /e /q /y
xcopy platform-usercenter\script %target_dirs% /g /e /q /y


rem libĿ¼
set target_lib_dir=%target_dirs%lib
if not exist %target_lib_dir% (md %target_lib_dir%)
xcopy platform-usercenter\build\libs\lib %target_lib_dir% /g /e /q /y

cd dist
set temp_tar=UserCenter%date%.tar
..\script\7z.exe a -ttar %temp_tar% UserCenter%date%
..\script\7z.exe a -tgzip %temp_tar%.gz %temp_tar%
del %temp_tar%
@echo.
echo �汾��[UserCenter%date%.tar.gz]����ɡ�
@echo.