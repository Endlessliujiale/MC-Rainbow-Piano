$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot\..
$taskLog=Get-Content test-game/score-console.log -Raw
$taskEnd=[regex]::Match($taskLog,'\[ScoreTest\] COMPLETE reachedOrbit=(\d+) completed=(\d+) missingGlyph=(\d+) premature=(\d+) remaining=(\d+)')
$taskStaff=[regex]::Match($taskLog,'\[ScoreTest\] FINAL staffMax=(\d+) arrivals=(\d+) replaced=(\d+) debris=(\d+) remainingScore=(\d+)')
if(!$taskEnd.Success -or !$taskStaff.Success){throw 'Score client has not completed yet'}
$taskNotes=@($taskEnd.Groups | Select-Object -Skip 1 | ForEach-Object {[int]$_.Value})
$taskValues=@($taskStaff.Groups | Select-Object -Skip 1 | ForEach-Object {[int]$_.Value})
if($taskNotes[0] -ne 1185 -or $taskNotes[1] -ne 1185 -or ($taskNotes[2]+$taskNotes[3]+$taskNotes[4]) -ne 0){throw "Journey failure: $taskNotes"}
if($taskValues[0] -gt 69 -or $taskValues[0] -lt 40 -or $taskValues[1] -ne 1185 -or $taskValues[2] -lt 1000 -or $taskValues[3] -lt 1000 -or $taskValues[4] -ne 0){throw "Staff failure: $taskValues"}
$taskDir=Join-Path (Get-Location) 'verification/score-staff'
New-Item -ItemType Directory -Force $taskDir | Out-Null
foreach($taskTick in @(800,1600,2000,2400,2600,3000,3400,4000,4600,6000,6200)){Copy-Item "test-game/screenshots/score-test/t_$taskTick.png" (Join-Path $taskDir "t_$taskTick.png") -Force}
$taskLog -split "`r?`n" | Where-Object {$_ -match '\[ScoreTest\]|notes='} | Set-Content (Join-Path $taskDir 'runtime-audit.log') -Encoding utf8
$taskPeak=([regex]::Matches($taskLog,'notes=\d+ spiral=\d+ top=\d+ transfer=\d+ orbit=\d+ particles=(\d+)') | ForEach-Object {[int]$_.Groups[1].Value} | Measure-Object -Maximum).Maximum
$taskResult=[ordered]@{client='Minecraft 1.21.1 / Fabric 0.16.14';hidden_client_ticks=6401;spawned=1185;score_arrivals=$taskNotes[0];finished=$taskNotes[1];missing_glyph_samples=$taskNotes[2];premature_removals=$taskNotes[3];remaining_notes=$taskNotes[4];max_occupied_score_slots=$taskValues[0];replacements=$taskValues[2];dissolve_bursts=$taskValues[3];remaining_score_slots=$taskValues[4];sampled_peak_particles=$taskPeak;original_spiral_and_renderer_unchanged=$true;shader_pack='none';jar_sha256=(Get-FileHash dist/pianofx-0.1.0-score-staff.jar).Hash}
$taskResult | ConvertTo-Json -Depth 5 | Set-Content (Join-Path $taskDir 'results.json') -Encoding utf8
$taskResult | ConvertTo-Json -Depth 5
