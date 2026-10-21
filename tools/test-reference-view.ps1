$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot\..
$taskJdk=Join-Path $PSScriptRoot 'zulu21.46.19-ca-jdk21.0.9-win_x64\bin'
$taskCp=Get-Content tools/classpath.txt -Raw
& "$taskJdk/javac.exe" -encoding UTF-8 -proc:none -cp "$PWD/build/score;$taskCp" -d build/reference-test tools/ReferenceViewAutoTest.java
if($LASTEXITCODE -ne 0){throw 'Fixture compilation failed'}
Copy-Item dist/pianofx-0.1.0-score-staff.jar test-game/mods/pianofx-0.1.0-score-staff.jar -Force
& "$taskJdk/jar.exe" uf test-game/mods/pianofx-0.1.0-score-staff.jar -C build/reference-test .
if($LASTEXITCODE -ne 0){throw 'Fixture packaging failed'}
Start-Process -FilePath "$taskJdk/java.exe" -ArgumentList '@tools/hidden-launch-args.txt' -WorkingDirectory $PWD -RedirectStandardOutput "$PWD/test-game/reference-console.log" -RedirectStandardError "$PWD/test-game/reference-error.log" -WindowStyle Hidden -PassThru | Select-Object Id