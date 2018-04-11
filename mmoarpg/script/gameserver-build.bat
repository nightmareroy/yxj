@echo.

echo �������룬�����Ҫ30�룬���Ե�...
call script\gameserver-gradle.bat

rem ��ǰʱ����Ϊ������ʱ�侫ȷ������
set hh=%time:~0,2%
if /i %hh% LSS 10 (set hh=0%time:~1,1%)
set date=%Date:~0,4%%Date:~5,2%%Date:~8,2%%hh%%time:~3,2%
echo ����Ϊ�������Ŀ��λ�ã�dist/S%date%.tar.gz
@echo.

@echo.
echo ���ƽű����������ɰ汾��...
rem ���ɵ�Ŀ��λ��
set target_dirs=dist\S%date%\
for %%i in (%target_dirs%) do (
	if not exist %%i (
		md %%i
	)
)
xcopy mmoarpg-game\build\libs\mmoarpg-game.jar %target_dirs% /g /e /q /y
xcopy mmoarpg-game\script %target_dirs% /g /e /q /y
xcopy script\agent.jar %target_dirs% /g /e /q /y

rem confĿ¼
set target_conf_dir=%target_dirs%conf
if not exist %target_conf_dir% (md %target_conf_dir%)
xcopy mmoarpg-game\conf %target_conf_dir% /g /e /q /y /EXCLUDE:script\gameserver-exclude.txt

rem dataĿ¼
set target_data_dir=%target_dirs%data
if not exist %target_data_dir% (md %target_data_dir%)
xcopy mmoarpg-game\data %target_data_dir% /g /e /q /y

rem jsonĿ¼
set target_json_dir=%target_dirs%json
if not exist %target_json_dir% (md %target_json_dir%)
xcopy D:\���Ŵ�ʦ��Ŀ��\��ʽ�ű�\json %target_json_dir% /g /e /q /y

cd dist
set temp_tar=S%date%.tar
..\script\7z.exe a -ttar %temp_tar% ".\S%date%\*"
..\script\7z.exe a -tgzip %temp_tar%.gz %temp_tar%
del %temp_tar%
@echo.
echo �汾��[S%date%.tar.gz]����ɡ�
@echo.