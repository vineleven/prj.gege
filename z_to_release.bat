@echo off
rem �Զ��ƶ���Դ�����ô���ű� 2016/8/9 14:42





set DESC_PRJ_ROOT=D:\workspace\prj\prj.gege_release
set SRC_PRJ_ROOT=D:\workspace\prj\prj.gege



rmdir /S /Q %DESC_PRJ_ROOT%\Assets

mkdir %DESC_PRJ_ROOT%\Assets

xcopy %SRC_PRJ_ROOT%\Assets %DESC_PRJ_ROOT%\Assets\ /c /e /q






