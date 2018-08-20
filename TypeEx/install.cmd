@echo off

echo.
echo Directory: %cd%
echo.
echo Building ...
echo.

call gradlew.bat build

echo.
echo Installing ...
echo.

copy build\libs\TypeEx-1.0.jar %appdata%\.minecraft\mods

timeout 5
