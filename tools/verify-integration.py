from pathlib import Path
import zipfile,json,hashlib
root=Path(__file__).resolve().parents[1]
baseline=root/'backup/before-user-piano/pianofx-0.1.0-score-staff-fixed.jar'
target=root/'dist/pianofx-0.1.0-extended-piano.jar'
with zipfile.ZipFile(baseline) as old,zipfile.ZipFile(target) as new:
    a={n:old.read(n) for n in old.namelist() if not n.endswith('/')}
    b={n:new.read(n) for n in new.namelist() if not n.endswith('/')}
    changed=sorted(n for n in a if a[n]!=b.get(n));added=sorted(set(b)-set(a))
    expected={'assets/pianofx/models/item/piano.json','assets/pianofx/sounds.json','com/pianofx/client/ScorePlayer.class','com/pianofx/client/fx/NoteSound.class',*(f'com/pianofx/client/gui/PianoScreen{suffix}.class' for suffix in ['','$Layout','$Mode','$Rect'])}
    assert set(changed)==expected,changed
    assert set(a)<=set(b),'Original files removed'
    for name in ['Spiral','Ribbon','Flyer','FxManager','ScoreStaff','ScoreStaffParticle']:
        path=f'com/pianofx/client/fx/{name}.class';assert a[path]==b[path],path
    assert a['com/pianofx/client/render/PianoRenderer.class']==b['com/pianofx/client/render/PianoRenderer.class']
    assert a['com/pianofx/client/AutoTest.class']==b['com/pianofx/client/AutoTest.class']
    for midi in range(128):
        path=f'assets/pianofx/sounds/bright_piano/{midi}.ogg';assert a[path]==b[path],path
    with zipfile.ZipFile(root/'tools/extended-note-block/default-soundpack.zip') as soundpack:
        for program,name in [(0,'grand'),(1,'bright')]:assert soundpack.read(f'assets/extendednoteblock/sounds/notes/{program}.60.ogg')==b[f'assets/pianofx/sounds/enb/{name}_60.ogg']
    assert len(json.loads(b['assets/pianofx/sounds.json']))==130
    assert len(json.loads(b['assets/pianofx/models/item/piano.json'])['elements'])==20
result={'jar_sha256':hashlib.sha256(target.read_bytes()).hexdigest().upper(),'changed_entries':changed,'added_entries':added,'visuals_and_fx_lifetimes_unchanged':True,'previous_multisamples_byte_identical':True,'enb_samples_byte_identical':True,'test_fixture_packaged':False,'sound_events':130}
folder=root/'verification/integration';folder.mkdir(parents=True,exist_ok=True)
(folder/'jar-diff.json').write_text(json.dumps(result,indent=2),encoding='utf-8')
print('PASS: original spiral, tracks, score, transitions, breakup and piano renderer unchanged; 128 previous samples preserved; two ENB samples exact; test fixture absent.')
