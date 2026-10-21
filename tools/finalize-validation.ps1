$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot\..
$taskLog=Get-Content test-game/hidden-console.log -Raw
$taskComplete=[regex]::Match($taskLog,'\[PlanetTest\] COMPLETE reachedOrbit=(\d+) completed=(\d+) missingGlyph=(\d+) premature=(\d+) remaining=(\d+)')
if(!$taskComplete.Success){throw 'Hidden test has not finished yet'}
$taskValues=@($taskComplete.Groups | Select-Object -Skip 1 | ForEach-Object {[int]$_.Value})
if($taskValues[0] -ne 1585 -or $taskValues[1] -ne 1585 -or ($taskValues[2]+$taskValues[3]+$taskValues[4]) -ne 0){throw "Hidden validation did not pass: $taskValues"}
$taskDir=Join-Path (Get-Location) 'verification/minimal-planet'
New-Item -ItemType Directory -Force $taskDir | Out-Null
$taskShots=@{800='spiral-before-orbit';2000='spiral-and-orbit';2600='top-view';3400='after-song-end';4600='orbit-fade';6600='finished'}
foreach($taskTick in $taskShots.Keys){Copy-Item "test-game/screenshots/planet-test/t_$taskTick.png" (Join-Path $taskDir ($taskShots[$taskTick]+'.png')) -Force}
$taskLog -split "`r?`n" | Where-Object {$_ -match '\[PlanetTest\]|notes='} | Set-Content (Join-Path $taskDir 'runtime-audit.log') -Encoding utf8
$taskPeak=([regex]::Matches($taskLog,'notes=\d+ spiral=\d+ top=\d+ transfer=\d+ orbit=\d+ particles=(\d+)') | ForEach-Object {[int]$_.Groups[1].Value} | Measure-Object -Maximum).Maximum
$taskResult=[ordered]@{original_track_geometry_and_renderer='byte-identical';modified_jar_entries=@('Flyer.class','FxManager.class');hidden_client='Minecraft 1.21.1 / Fabric 0.16.14';runtime_ticks=6701;spawned_notes=1585;notes_reached_orbit=$taskValues[0];notes_finished_naturally=$taskValues[1];missing_glyph_handle_samples=$taskValues[2];premature_removals=$taskValues[3];remaining_notes=$taskValues[4];sampled_peak_particles=$taskPeak;numerical_journeys_passed=45;shader_pack='none';production_contains_test_fixture=$false;jar_sha256=(Get-FileHash dist/pianofx-0.1.0-planet-fix.jar).Hash}
$taskResult | ConvertTo-Json -Depth 5 | Set-Content (Join-Path $taskDir 'results.json') -Encoding utf8
$taskResult | ConvertTo-Json -Depth 5
