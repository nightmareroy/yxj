@echo off
title �Զ��������

@echo.
echo ��ѡ��Ҫ�����ķ������ࣺ
echo =====================================
@echo.
echo   1 = ��Ϸ��������mmoarpg-game.jar��
echo   2 = ��¼��������platform-login.jar��
echo   3 = �û����ģ�platform-usercenter.jar��
echo   4 = ��ֵ���ģ�platform-paycenter.jar��
echo   5 = �ɼ�����platform-collector.war��
echo   6 = GMT��̨��platform-gm.war��
@echo.                      
echo =====================================

rem ����Ŀ��λ��
rd /s /Q dist

:loop
set/p server_type=�������ţ�
if %server_type%==1 goto build-gameserver
if %server_type%==2 goto build-loginserver
if %server_type%==3 goto build-userserver
if %server_type%==4 goto build-payserver
if %server_type%==5 goto build-collector
if %server_type%==6 goto next0

echo ��ܰ��ʾ��������[1-5]���
@echo.
goto loop

:next0
echo ��ܰ��ʾ���˱�Ŵ���ű���δʵ��
@echo.
goto loop

rem ������Ϸ������
:build-gameserver
echo ��Ҫ�����ǡ���Ϸ��������
call script/gameserver-build.bat
goto exit

rem ������¼������
:build-loginserver
echo ��Ҫ�����ǡ���¼��������
call script/loginserver-build.bat
goto exit

rem �����û�����
:build-userserver
echo ��Ҫ�����ǡ��û����ġ�
call script/userserver-build.bat
goto exit

rem ������ֵ����
:build-payserver
echo ��Ҫ�����ǡ���ֵ���ġ�
call script/payserver-build.bat
goto exit

rem �����ɼ���
:build-collector
echo ��Ҫ�����ǡ��ɼ�����
call script/collector-build.bat
goto exit

:exit
pause