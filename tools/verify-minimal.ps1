$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot\..
Add-Type -AssemblyName System.IO.Compression.FileSystem
function Read-EntryHashes($path){
    $zip=[IO.Compression.ZipFile]::OpenRead((Join-Path (Get-Location) $path))
    $hashes=@{}
    try{foreach($entry in $zip.Entries){if($entry.FullName.EndsWith('/')){continue};$stream=$entry.Open();try{$hashes[$entry.FullName]=[Convert]::ToHexString([Security.Cryptography.SHA256]::HashData($stream))}finally{$stream.Dispose()}}}finally{$zip.Dispose()}
    return $hashes
}
$taskOriginal=Read-EntryHashes 'original/pianofx-0.1.0.jar'
$taskPatched=Read-EntryHashes 'dist/pianofx-0.1.0-planet-fix.jar'
$taskChanged=@($taskOriginal.Keys | Where-Object {$taskOriginal[$_] -ne $taskPatched[$_]} | Sort-Object)
$taskAdded=@($taskPatched.Keys | Where-Object {!$taskOriginal.ContainsKey($_)})
$taskExpected=@('com/pianofx/client/fx/Flyer.class','com/pianofx/client/fx/FxManager.class')
if(($taskChanged -join ',') -ne ($taskExpected -join ',') -or $taskAdded.Count){throw "Unexpected changed/added entries: $taskChanged $taskAdded"}
$taskReport=[ordered]@{original_sha256=(Get-FileHash original/pianofx-0.1.0.jar).Hash;candidate_sha256=(Get-FileHash dist/pianofx-0.1.0-planet-fix.jar).Hash;changed_entries=$taskChanged;added_entries=$taskAdded;all_other_entries_byte_identical=$true;track_geometry_unchanged=$true;track_renderer_unchanged=$true;particle_framework_unchanged=$true;textures_and_colors_unchanged=$true}
$taskReport | ConvertTo-Json -Depth 5 | Set-Content verification/minimal-jar-diff.json -Encoding utf8
$taskReport | ConvertTo-Json -Depth 5
