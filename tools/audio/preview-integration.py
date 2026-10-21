from pathlib import Path
import sys,zipfile,io
root=Path(__file__).resolve().parents[2]
sys.path.insert(0,str(root/'tools/audio/python'))
import soundfile as sf
import numpy as np
sr=44100;folder=root/'verification/integration'
with zipfile.ZipFile(root/'dist/pianofx-0.1.0-extended-piano.jar') as jar:
    for mode in ['extended-bright','multisample-bright']:
        result=np.zeros(sr*7)
        for time,midi in [(i*.38,n) for i,n in enumerate([60,64,67,72,67,64,60])]+[(3.5,60),(3.5,64),(3.5,67)]:
            if mode=='extended-bright':
                source,rate=sf.read(io.BytesIO(jar.read('assets/pianofx/sounds/enb/bright_60.ogg')))
                if source.ndim==2:source=source.mean(axis=1)
                ratio=2**((midi-60)/12)*rate/sr
            else:
                source,rate=sf.read(io.BytesIO(jar.read(f'assets/pianofx/sounds/bright_piano/{midi}.ogg')))
                if source.ndim==2:source=source.mean(axis=1)
                ratio=rate/sr
            frames=sr;position=np.arange(frames)*ratio
            wave=np.interp(position,np.arange(len(source)),source,left=0,right=0)
            elapsed=np.arange(frames)/sr;q=np.clip((elapsed-.55)/.4,0,1)
            wave*=1-q*q*(3-2*q)
            offset=round(time*sr);result[offset:offset+frames]+=wave*.40
        assert np.max(abs(result))<1,(mode,np.max(abs(result)))
        sf.write(folder/f'{mode}-preview.wav',result,sr)
print('Generated two previews from the actual packaged audio; no recorded-loop extraction used.')
