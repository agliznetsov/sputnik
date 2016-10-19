@echo off
if %1.==. (
    echo new version number is required.
) else (
    call mvn versions:set -DnewVersion=%1
    call mvn versions:commit
    git commit -a -m "Release v%1"
    git tag -a v%1 -m "Release v%1"
)
