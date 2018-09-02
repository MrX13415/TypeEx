@echo off

echo.
echo Directory: %cd%
echo.
echo Retrieving Minecraft dependencies ...
echo.

call gradlew.bat setupDecompWorkspace

echo.
echo Finalizing eclipse workspace ...
echo.

call gradlew.bat eclipse

timeout 5
