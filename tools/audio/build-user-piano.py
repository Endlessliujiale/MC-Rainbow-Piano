"""Extract pitched partials from the user's polyphonic WAV; build MIDI-keyed samples.

This is harmonic isolation, not recovery of the original instrument or a soundfont.
Only user-supplied audio is used; the old GM samples are not mixed in.
"""
import sys, pathlib, json, hashlib
sys.path.insert(0,str(pathlib.Path(__file__).resolve().parent/'python'))
import numpy as np
import soundfile as sf
root=pathlib.Path(__file__).resolve().parents[2]
source=root/'tools/audio/user-reference/reference.wav'
audio,sr=sf.read(source)
audio=audio.mean(axis=1)
target=root/'assets-user-piano/assets/pianofx'
destination=target/'sounds/bright_piano'
destination.mkdir(parents=True,exist_ok=True)
qa=root/'verification/user-piano'
qa.mkdir(parents=True,exist_ok=True)

def isolate(start,end,midi):
    x=audio[round(start*sr):round(end*sr)]
    n=4096;hop=256;pad=n//2
    padded=np.pad(x,(pad,pad+n))
    frames=np.lib.stride_tricks.sliding_window_view(padded,n)[::hop]
    window=np.hanning(n)
    spectrum=np.fft.rfft(frames*window,axis=1)
    frequencies=np.fft.rfftfreq(n,1/sr)
    f0=440*2**((midi-69)/12)
    mask=np.zeros(len(frequencies))
    for harmonic in range(1,min(64,int(18000/f0))+1):
        center=f0*harmonic
        width=max(8,center*.003)
        mask=np.maximum(mask,np.exp(-.5*((frequencies-center)/width)**2))
    # Keep the source phase, attack and decay while suppressing unrelated chord pitches.
    reconstructed=np.fft.irfft(spectrum*mask[None,:],n=n,axis=1)
    y=np.zeros(len(padded));weight=np.zeros(len(padded))
    for i,frame in enumerate(reconstructed):
        offset=i*hop;y[offset:offset+n]+=frame*window;weight[offset:offset+n]+=window**2
    y=(y/np.maximum(weight,1e-10))[pad:pad+len(x)]
    fadein=min(220,len(y)//8);fadeout=min(2205,len(y)//4)
    y[:fadein]*=np.linspace(0,1,fadein)
    y[-fadeout:]*=np.linspace(1,0,fadeout)**2
    peak=np.max(np.abs(y))
    if peak<1e-5:raise RuntimeError('Silent extracted note')
    y*=.58/peak
    sf.write(qa/f'isolated_{midi}.wav',y,sr)
    return y

# These onsets and pitches were measured from this specific recording.
regions={59:(0,.49),64:(.49,1.04),73:(1.74,2.29),74:(0,.49)}
samples={midi:isolate(*region,midi) for midi,region in regions.items()}
definitions={};report=[]
for midi in range(128):
    base=min(samples,key=lambda key:abs(key-midi))
    source_note=samples[base];ratio=2**((midi-base)/12)
    if ratio>1:
        fft_size=1<<(len(source_note)*2-1).bit_length()
        frequency=np.fft.rfftfreq(fft_size,1/sr)
        cutoff=sr*.46/ratio
        lowpass=np.clip((cutoff-frequency)/(cutoff*.12),0,1)
        source_note=np.fft.irfft(np.fft.rfft(source_note,fft_size)*lowpass,fft_size)[:len(source_note)]
    positions=np.arange(0,len(source_note)-1,ratio)
    y=np.interp(positions,np.arange(len(source_note)),source_note)
    # Avoid multi-second stretched reverb at the very bottom of the MIDI range.
    y=y[:int(sr*3.2)]
    if len(y)>100:
        tail=min(880,len(y)//8);y[-tail:]*=np.linspace(1,0,tail)
        attack=min(440,len(y)//8);y[:attack]*=np.linspace(0,1,attack)**2
    sf.write(destination/f'{midi}.ogg',y,sr,format='OGG',subtype='VORBIS')
    decoded,decoded_sr=sf.read(destination/f'{midi}.ogg')
    assert decoded_sr==sr and np.max(abs(decoded))>.01
    assert np.max(abs(decoded))<.99 and np.isfinite(decoded).all()
    assert abs(decoded[0])<.01 and abs(decoded[-1])<.01,(midi,decoded[0],decoded[-1])
    definitions[f'bright_piano_{midi}']={'sounds':[{'name':f'pianofx:bright_piano/{midi}','stream':False}]}
    report.append({'midi':midi,'source_midi':base,'seconds':len(decoded)/sr,'peak':float(np.max(abs(decoded)))})
(target/'sounds.json').write_text(json.dumps(definitions,indent=2),encoding='utf-8')

# Preview uses exactly the encoded assets that Minecraft will play.
preview=np.zeros(sr*8)
events=[(i*.38,m) for i,m in enumerate([59,62,66,71,73,74,73,71,66,62,59])]
events.extend([(4.8,59),(4.8,62),(4.8,66),(4.8,71)])
for t,midi in events:
    wave,_=sf.read(destination/f'{midi}.ogg');offset=round(t*sr)
    size=min(len(wave),len(preview)-offset)
    preview[offset:offset+size]+=wave[:size]*.40
assert np.max(abs(preview))<.99
sf.write(qa/'user-piano-preview.wav',preview,sr)
sf.write(qa/'user-piano-preview.ogg',preview,sr,format='OGG',subtype='VORBIS')
result={'input_sha256':hashlib.sha256(source.read_bytes()).hexdigest(),'input_seconds':len(audio)/sr,'sample_regions':regions,'method':'harmonic isolation from user polyphonic recording; nearest-region resampling','samples':report,'old_gm_audio_used':False,'limitations':'Approximation from a mixed melody, not the original independent piano instrument. Original sample attack/decay length scales with transposition.'}
(qa/'audio-checks.json').write_text(json.dumps(result,indent=2),encoding='utf-8')
print('PASS: 128 non-silent, finite, unclipped OGG samples decoded; click-free endpoints checked; preview uses packaged OGG assets.')
