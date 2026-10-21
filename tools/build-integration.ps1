$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot\..
$taskJdk=Join-Path $PSScriptRoot 'zulu21.46.19-ca-jdk21.0.9-win_x64\bin'
$taskCp=Get-Content tools/classpath.txt -Raw
New-Item -ItemType Directory -Force build/integration | Out-Null
$taskSources=@('source/com/pianofx/client/fx/NoteSound.java','source/com/pianofx/client/fx/PianoVoice.java','source/com/pianofx/client/gui/PianoControls.java','source/com/pianofx/client/gui/CustomPianoScreen.java','source/com/pianofx/client/gui/PianoScreen.java','source/com/pianofx/client/ScorePlayer.java')
& "$taskJdk/javac.exe" -encoding UTF-8 -proc:none -cp $taskCp -d build/integration @taskSources
if($LASTEXITCODE -ne 0){throw 'Integration compilation failed'}
Copy-Item backup/before-user-piano/pianofx-0.1.0-score-staff-fixed.jar dist/pianofx-0.1.0-extended-piano.jar -Force
& "$taskJdk/jar.exe" uf dist/pianofx-0.1.0-extended-piano.jar -C build/integration . -C assets-integration .
if($LASTEXITCODE -ne 0){throw 'Integration packaging failed'}
Get-FileHash dist/pianofx-0.1.0-extended-piano.jar
