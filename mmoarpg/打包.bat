@echo off
title 自动打包工具

@echo.
echo 请选择要制作的服务器类：
echo =====================================
@echo.
echo   1 = 游戏服务器（mmoarpg-game.jar）
echo   2 = 登录服务器（platform-login.jar）
echo   3 = 用户中心（platform-usercenter.jar）
echo   4 = 充值中心（platform-paycenter.jar）
echo   5 = 采集器（platform-collector.war）
echo   6 = GMT后台（platform-gm.war）
@echo.                      
echo =====================================

rem 清理目标位置
rd /s /Q dist

:loop
set/p server_type=请输入编号：
if %server_type%==1 goto build-gameserver
if %server_type%==2 goto build-loginserver
if %server_type%==3 goto build-userserver
if %server_type%==4 goto build-payserver
if %server_type%==5 goto build-collector
if %server_type%==6 goto next0

echo 温馨提示：请输入[1-5]编号
@echo.
goto loop

:next0
echo 温馨提示：此编号打包脚本尚未实现
@echo.
goto loop

rem 制作游戏服务器
:build-gameserver
echo 您要制作是【游戏服务器】
call script/gameserver-build.bat
goto exit

rem 制作登录服务器
:build-loginserver
echo 您要制作是【登录服务器】
call script/loginserver-build.bat
goto exit

rem 制作用户中心
:build-userserver
echo 您要制作是【用户中心】
call script/userserver-build.bat
goto exit

rem 制作充值中心
:build-payserver
echo 您要制作是【充值中心】
call script/payserver-build.bat
goto exit

rem 制作采集器
:build-collector
echo 您要制作是【采集器】
call script/collector-build.bat
goto exit

:exit
pause