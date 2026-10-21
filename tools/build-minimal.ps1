$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot\..
$taskJdk=Join-Path $PSScriptRoot 'zulu21.46.19-ca-jdk21.0.9-win_x64\bin'
$taskCp=Get-Content tools/classpath.txt -Raw
New-Item -ItemType Directory -Force build/minimal,build/minimal-test,dist | Out-Null
& "$taskJdk/javac.exe" -encoding UTF-8 -proc:none -cp $taskCp -d build/minimal backup/before-score-staff/fx/Flyer.java backup/before-score-staff/fx/FxManager.java
if($LASTEXITCODE -ne 0){throw 'Minimal fix compilation failed'}
Copy-Item original/pianofx-0.1.0.jar dist/pianofx-0.1.0-planet-fix.jar -Force
& "$taskJdk/jar.exe" uf dist/pianofx-0.1.0-planet-fix.jar -C build/minimal com/pianofx/client/fx/Flyer.class -C build/minimal com/pianofx/client/fx/FxManager.class
if($LASTEXITCODE -ne 0){throw 'Minimal fix packaging failed'}
& "$taskJdk/javac.exe" -encoding UTF-8 -proc:none -cp "$PWD/build/minimal;$taskCp" -d build/minimal-test tools/PlanetAutoTest.java
if($LASTEXITCODE -ne 0){throw 'Fixture compilation failed'}
Copy-Item dist/pianofx-0.1.0-planet-fix.jar test-game/mods/pianofx-0.1.0-planet-fix.jar -Force
& "$taskJdk/jar.exe" uf test-game/mods/pianofx-0.1.0-planet-fix.jar -C build/minimal-test .
if(Test-Path test-game/mods/pianofx-0.1.1-planet.jar){Move-Item test-game/mods/pianofx-0.1.1-planet.jar verification/withdrawn/test-only-pianofx-0.1.1.jar -Force}
Get-FileHash dist/pianofx-0.1.0-planet-fix.jar
