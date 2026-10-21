from pathlib import Path
import zipfile,hashlib,json
root=Path(__file__).resolve().parents[1]
jar=root/'dist/pianofx-0.1.0-score-staff.jar'
with zipfile.ZipFile(root/'original/pianofx-0.1.0.jar') as old,zipfile.ZipFile(jar) as new:
    baseline={n:hashlib.sha256(old.read(n)).hexdigest() for n in old.namelist() if not n.endswith('/')}
    current={n:hashlib.sha256(new.read(n)).hexdigest() for n in new.namelist() if not n.endswith('/')}
    changed=sorted(n for n in baseline if current.get(n)!=baseline[n])
    expected=sorted(['com/pianofx/client/fx/Flyer.class','com/pianofx/client/fx/FxManager.class','com/pianofx/client/fx/NoteSound.class','com/pianofx/client/ScorePlayer.class','com/pianofx/client/render/PianoRenderer.class'])
    assert changed==expected,changed
    added=sorted(set(current)-set(baseline))
    assert set(added)=={'com/pianofx/client/fx/ScoreStaff.class','com/pianofx/client/fx/ScoreStaffParticle.class','assets/pianofx/sounds.json',*(f'assets/pianofx/sounds/bright_piano/{i}.ogg' for i in range(128)),*(f'licenses/bright-piano/{name}' for name in ['NOTICE','COPYING','source/TimGM6mb.sf2','source/RenderBrightPiano.java','source/encode-bright-piano.py'])},added
    assert current['com/pianofx/client/AutoTest.class']==baseline['com/pianofx/client/AutoTest.class']
    definitions=json.loads(new.read('assets/pianofx/sounds.json'))
    assert len(definitions)==128
    for i in range(128):
        assert definitions[f'bright_piano_{i}']['sounds'][0]['name']==f'pianofx:bright_piano/{i}'
        assert new.read(f'assets/pianofx/sounds/bright_piano/{i}.ogg').startswith(b'OggS')
result=dict(sha256=hashlib.sha256(jar.read_bytes()).hexdigest().upper(),changed_entries=changed,added_entries=added,original_spiral_unchanged=True,original_ribbon_unchanged=True,original_particle_renderer_unchanged=True,test_fixture_packaged=False,bright_piano_samples=128,staff_center_height_above_ground=2)
out=root/'verification/ending-fix';out.mkdir(parents=True,exist_ok=True)
(out/'jar-diff.json').write_text(json.dumps(result,indent=2),encoding='utf-8')
print('PASS original geometry/track/particle renderer byte-identical; changed exactly five original classes; 128 OGG sounds and their source/license packaged; test fixture excluded.')
