from pathlib import Path
import zipfile,hashlib,json
root=Path(__file__).resolve().parents[1]
baseline=root/'backup/before-user-piano/pianofx-0.1.0-score-staff-fixed.jar'
output=root/'dist/pianofx-0.1.0-user-piano.jar'
assets=root/'assets-user-piano'
replacements={p.relative_to(assets).as_posix():p.read_bytes() for p in assets.rglob('*') if p.is_file()}
replacements['com/pianofx/client/fx/NoteSound.class']=(root/'build/user-piano/com/pianofx/client/fx/NoteSound.class').read_bytes()
with zipfile.ZipFile(baseline) as old,zipfile.ZipFile(output,'w',zipfile.ZIP_DEFLATED) as new:
    for entry in old.infolist():
        name=entry.filename
        if name.startswith('licenses/bright-piano/'):continue # Obsolete GM soundfont and its build source.
        new.writestr(entry,replacements.pop(name,old.read(name)))
    for name,data in replacements.items():new.writestr(name,data)
with zipfile.ZipFile(baseline) as old,zipfile.ZipFile(output) as new:
    original={n:old.read(n) for n in old.namelist() if not n.endswith('/')}
    updated={n:new.read(n) for n in new.namelist() if not n.endswith('/')}
    changed=sorted(n for n in original if n in updated and original[n]!=updated[n])
    removed=sorted(set(original)-set(updated))
    added=sorted(set(updated)-set(original))
    assert changed==sorted(['com/pianofx/client/fx/NoteSound.class',*(f'assets/pianofx/sounds/bright_piano/{m}.ogg' for m in range(128))]),changed
    assert all(n.startswith('licenses/bright-piano/') for n in removed),removed
    assert added==['PIANOFX_AUDIO_SOURCE.txt'],added
    assert len(json.loads(updated['assets/pianofx/sounds.json']))==128
    for name in ['Spiral','Ribbon','Flyer','FxManager','ScoreStaff','ScoreStaffParticle']:
        key=f'com/pianofx/client/fx/{name}.class'
        assert original[key]==updated[key],name
result={'jar_sha256':hashlib.sha256(output.read_bytes()).hexdigest().upper(),'changed_entries':changed,'removed_obsolete_gm_source_entries':removed,'added_entries':added,'all_visuals_and_ending_logic_unchanged':True,'test_fixture_packaged':False,'sound_definitions':128}
(root/'verification/user-piano/jar-diff.json').write_text(json.dumps(result,indent=2),encoding='utf-8')
print('PASS: only 128 audio samples and audio label changed; obsolete GM source removed; all visuals, endings and original test class byte-identical.')
print(output)
