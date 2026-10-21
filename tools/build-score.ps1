$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot\..
$taskJdk=Join-Path $PSScriptRoot 'zulu21.46.19-ca-jdk21.0.9-win_x64\bin'
$taskCp=Get-Content tools/classpath.txt -Raw
New-Item -ItemType Directory -Force build/score,build/score-test,dist | Out-Null
$taskNames=@('Flyer','FxManager','ScoreStaff','ScoreStaffParticle','NoteSound')
$taskSources=$taskNames | ForEach-Object {"source/com/pianofx/client/fx/$_.java"}
$taskSources+=@('source/com/pianofx/client/ScorePlayer.java','source/com/pianofx/client/render/PianoRenderer.java')
& "$taskJdk/javac.exe" -encoding UTF-8 -proc:none -cp $taskCp -d build/score @taskSources
if($LASTEXITCODE -ne 0){throw 'Score staff compilation failed'}
Copy-Item original/pianofx-0.1.0.jar dist/pianofx-0.1.0-score-staff.jar -Force
foreach($taskName in $taskNames){& "$taskJdk/jar.exe" uf dist/pianofx-0.1.0-score-staff.jar -C build/score "com/pianofx/client/fx/$taskName.class";if($LASTEXITCODE -ne 0){throw 'Score packaging failed'}}
& "$taskJdk/jar.exe" uf dist/pianofx-0.1.0-score-staff.jar -C build/score com/pianofx/client/ScorePlayer.class -C build/score com/pianofx/client/render/PianoRenderer.class -C assets-build .
if($LASTEXITCODE -ne 0){throw 'Audio and renderer packaging failed'}
Copy-Item dist/pianofx-0.1.0-score-staff.jar dist/pianofx-0.1.0-score-staff-fixed.jar -Force
& "$taskJdk/javac.exe" -encoding UTF-8 -proc:none -cp "$PWD/build/score;$taskCp" -d build/score-test tools/EndingAutoTest.java
if($LASTEXITCODE -ne 0){throw 'Score fixture compilation failed'}
if(Test-Path test-game/mods/pianofx-0.1.0-planet-fix.jar){Move-Item test-game/mods/pianofx-0.1.0-planet-fix.jar backup/before-score-staff/previous-test.jar -Force}
Copy-Item dist/pianofx-0.1.0-score-staff.jar test-game/mods/pianofx-0.1.0-score-staff.jar -Force
& "$taskJdk/jar.exe" uf test-game/mods/pianofx-0.1.0-score-staff.jar -C build/score-test .
Get-FileHash dist/pianofx-0.1.0-score-staff.jar
