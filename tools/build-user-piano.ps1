$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot\..
$taskJdk=Join-Path $PSScriptRoot 'zulu21.46.19-ca-jdk21.0.9-win_x64\bin'
$taskCp=Get-Content tools/classpath.txt -Raw
New-Item -ItemType Directory -Force build/user-piano | Out-Null
& "$taskJdk/javac.exe" -encoding UTF-8 -proc:none -cp $taskCp -d build/user-piano source/com/pianofx/client/fx/NoteSound.java
if($LASTEXITCODE -ne 0){throw 'Audio class compilation failed'}
& 'C:/Users/Administrator/.cache/codex-runtimes/codex-primary-runtime/dependencies/python/python.exe' tools/pack-user-piano.py
if($LASTEXITCODE -ne 0){throw 'Audio package verification failed'}
Get-FileHash dist/pianofx-0.1.0-user-piano.jar
