$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot\..
$taskJdk=Join-Path $PSScriptRoot 'zulu21.46.19-ca-jdk21.0.9-win_x64\bin'
$taskAsm=Join-Path (Get-Location) 'test-game/libraries/org/ow2/asm/asm/9.8/asm-9.8.jar'
New-Item -ItemType Directory -Force build/hidden-agent,tools/world-seed,test-game/saves | Out-Null
& "$taskJdk/javac.exe" -cp $taskAsm -d build/hidden-agent tools/HiddenWindowAgent.java
if($LASTEXITCODE -ne 0){throw 'Hidden agent compilation failed'}
"Premain-Class: HiddenWindowAgent`n" | Set-Content -LiteralPath tools/hidden-manifest.mf -Encoding ascii
& "$taskJdk/jar.exe" cfm tools/hidden-window-agent.jar tools/hidden-manifest.mf -C build/hidden-agent .
$taskLaunch=Get-Content tools/launch-args.txt -Raw
$taskPrefix='"-javaagent:'+((Join-Path (Get-Location) 'tools/hidden-window-agent.jar') -replace '\\','/')+'"'
($taskPrefix+"`n"+$taskLaunch+"`n"+'"--quickPlaySingleplayer"'+"`n"+'"PlanetTest"') | Set-Content tools/hidden-launch-args.txt -Encoding ascii
@'
version:3955
fullscreen:false
pauseOnLostFocus:false
maxFps:40
enableVsync:false
renderDistance:6
simulationDistance:5
graphicsMode:1
particles:0
entityShadows:false
gamma:0.7
guiScale:2
soundCategory_master:0.0
onboardAccessibility:false
joinedFirstServer:true
skipMultiplayerWarning:true
'@ | Set-Content test-game/options.txt -Encoding ascii
@'
eula=true
'@ | Set-Content tools/world-seed/eula.txt -Encoding ascii
$taskWorld=(Join-Path (Get-Location) 'test-game/saves/PlanetTest') -replace '\\','/'
@"
level-name=$taskWorld
level-type=minecraft\:flat
generator-settings={"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains","structures":{}}
online-mode=false
server-ip=127.0.0.1
server-port=25576
gamemode=creative
spawn-protection=0
view-distance=3
simulation-distance=3
max-players=1
generate-structures=false
enable-rcon=false
"@ | Set-Content tools/world-seed/server.properties -Encoding ascii
Write-Output 'Hidden render agent and isolated world configuration ready.'
