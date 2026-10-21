from pathlib import Path
import zipfile,json,hashlib
root=Path(__file__).resolve().parents[1]
assets=root/'assets-integration';target=assets/'assets/pianofx'
(target/'sounds/enb').mkdir(parents=True,exist_ok=True)
(target/'models/item').mkdir(parents=True,exist_ok=True)
(assets/'licenses/extendednoteblock').mkdir(parents=True,exist_ok=True)
with zipfile.ZipFile(root/'tools/extended-note-block/extendednoteblock.jar') as enb:
    (assets/'licenses/extendednoteblock/MIT.txt').write_bytes(enb.read('LICENSE_extendednoteblock'))
with zipfile.ZipFile(root/'tools/extended-note-block/default-soundpack.zip') as bank:
    for program,name in [(0,'grand'),(1,'bright')]:
        data=bank.read(f'assets/extendednoteblock/sounds/notes/{program}.60.ogg')
        (target/f'sounds/enb/{name}_60.ogg').write_bytes(data)
with zipfile.ZipFile(root/'backup/before-user-piano/pianofx-0.1.0-score-staff-fixed.jar') as baseline:
    sounds=json.loads(baseline.read('assets/pianofx/sounds.json'))
for name in ['grand','bright']:
    sounds[f'enb_{name}']={'sounds':[{'name':f'pianofx:enb/{name}_60','stream':False}]}
(target/'sounds.json').write_text(json.dumps(sounds,indent=2),encoding='utf-8')

# Dedicated three-dimensional inventory model; the empty world model remains unchanged.
textures={'body':'minecraft:block/black_concrete','wood':'minecraft:block/stripped_dark_oak_log','white':'minecraft:block/white_concrete','trim':'minecraft:block/sea_lantern','particle':'minecraft:block/black_concrete'}
elements=[]
def box(x,y,z,sx,sy,sz,texture,rotation=None):
    element={'from':[round(8+x*10,4),round(1+y*10,4),round(8+z*10,4)],'to':[round(8+(x+sx)*10,4),round(1+(y+sy)*10,4),round(8+(z+sz)*10,4)],'faces':{face:{'texture':'#'+texture,'uv':[0,0,16,16]} for face in ['up','down','north','south','east','west']}}
    if rotation:element['rotation']=rotation
    elements.append(element)
box(-1.1,.6,-1.5,2.2,.46,2.35,'body');box(-.72,.6,-1.85,1.44,.46,.42,'body')
box(-1.13,.56,-1.53,2.26,.07,2.41,'trim')
for x,z in [(-1.02,.85),(.86,.85),(-.08,-1.35)]:box(x,0,z,.16,.6,.16,'wood')
box(-1.1,1.06,-1.5,2.2,.08,2.4,'body',{'origin':[8,11.6,-7],'axis':'x','angle':-22.5})
box(-1.1,.5,.8,2.2,.12,.72,'wood')
for i in range(12):
    black=i in [1,3,6,8,10]
    box(-1.05+i*.175+(.028 if black else .012),.71 if black else .63,.86 if black else .78,.11 if black else .15,.09,.42 if black else .58,'body' if black else 'white')
model={'parent':'minecraft:block/block','gui_light':'front','textures':textures,'elements':elements,'display':{'gui':{'rotation':[30,45,0],'translation':[0,-1,0],'scale':[.42,.42,.42]},'ground':{'translation':[0,3,0],'scale':[.3,.3,.3]},'fixed':{'rotation':[0,180,0],'scale':[.5,.5,.5]},'firstperson_righthand':{'rotation':[0,45,0],'scale':[.35,.35,.35]},'thirdperson_righthand':{'rotation':[75,45,0],'translation':[0,2,0],'scale':[.35,.35,.35]}}}
(target/'models/item/piano.json').write_text(json.dumps(model,indent=2),encoding='utf-8')
(assets/'licenses/extendednoteblock/INTEGRATION.txt').write_text('PianoFX audio integration references ExtendedNoteBlock 1.9.1 ClientSoundManager and ActiveSoundFader (Atemukesu, MIT).\nTwo unmodified C4 samples from its bundled GeneralUser GS sound pack are included. GeneralUser GS: S. Christian Collins, https://schristiancollins.com/generaluser .\nThis is an adapted piano sound module, not the full ExtendedNoteBlock mod. Redstone/network/block features are not included.\nC4 transposition is restricted to +/-12 semitones; the original TimGM6mb multisamples handle other pitches.\n',encoding='utf-8')
print('Prepared two byte-identical ExtendedNoteBlock piano samples, attribution, 130 sound events and a 20-element inventory piano model.')
