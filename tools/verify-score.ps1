$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot\..
& 'C:/Users/Administrator/.cache/codex-runtimes/codex-primary-runtime/dependencies/python/python.exe' tools/verify-ending.py
if($LASTEXITCODE -ne 0){throw 'Ending build verification failed'}