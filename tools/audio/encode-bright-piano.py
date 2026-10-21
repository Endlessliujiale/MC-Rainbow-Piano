import sys, pathlib, json
sys.path.insert(0,str(pathlib.Path(__file__).resolve().parent/'python'))
import soundfile as sf
import numpy as np
root=pathlib.Path(__file__).resolve().parents[2]
target=root/'assets-build/assets/pianofx'
(target/'sounds/bright_piano').mkdir(parents=True,exist_ok=True)
definitions={}
for midi in range(128):
    data,rate=sf.read(root/f'tools/audio/wav/{midi}.wav')
    peak=np.max(np.abs(data))
    if peak<1e-6:
        if midi<=108:raise RuntimeError(f'Silent piano note {midi}')
        base,rate=sf.read(root/'tools/audio/wav/108.wav')
        factor=2**((midi-108)/12)
        data=np.interp(np.arange(0,len(base),factor),np.arange(len(base)),base)
    data=np.clip(data*1.7,-.98,.98)
    data[-2205:]*=np.linspace(1,0,2205)
    sf.write(target/f'sounds/bright_piano/{midi}.ogg',data,rate,format='OGG',subtype='VORBIS')
    definitions[f'bright_piano_{midi}']={'sounds':[{'name':f'pianofx:bright_piano/{midi}','stream':False}]}
(target/'sounds.json').write_text(json.dumps(definitions,indent=2),encoding='utf-8')
print('Encoded 128 non-silent mono OGG pitches: SF2 0..108, upper extension 109..127 transposed offline.')
