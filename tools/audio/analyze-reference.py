import sys,pathlib,json
sys.path.insert(0,'tools/audio/python')
import soundfile as sf,numpy as np
x,sr=sf.read('tools/audio/user-reference/reference.wav');o,osr=sf.read('tools/audio/user-reference/reference.ogg')
y=x.mean(axis=1); n=2048;hop=256
frames=np.lib.stride_tricks.sliding_window_view(y[::2],n)[::hop]
spec=np.abs(np.fft.rfft(frames*np.hanning(n),axis=1))
flux=np.maximum(0,np.diff(spec,axis=0)).sum(axis=1)
peaks=[]
for i in range(2,len(flux)-2):
 if flux[i]==max(flux[i-2:i+3]) and flux[i]>np.percentile(flux,75)*1.5:
  t=(i*hop+n/2)/22050
  if not peaks or t-peaks[-1]>.10:peaks.append(t)
print('Correlation WAV/OGG',float(np.corrcoef(y,o.mean(axis=1))[0,1]),'peak',np.max(abs(y)))
print('First onset times',peaks[:35])
for t in [0,.1,.2,.35,.5,.7,1,1.3,1.5,2,3,4,5,6]:
 a=y[int(t*sr):int((t+.10)*sr)];sp=abs(np.fft.rfft(a*np.hanning(len(a)),65536));freq=np.fft.rfftfreq(65536,1/sr)
 indices=np.where((sp[1:-1]>sp[:-2])&(sp[1:-1]>sp[2:]))[0]+1
 indices=sorted(indices,key=lambda i:sp[i],reverse=True)[:8]
 print(round(t,3),[(round(freq[i],1),round(69+12*np.log2(freq[i]/440),1),round(float(sp[i]),2)) for i in indices if freq[i]>25])
pathlib.Path('verification/user-piano/analysis.json').write_text(json.dumps({'rate':sr,'duration':len(y)/sr,'onsets':peaks,'wav_ogg_correlation':float(np.corrcoef(y,o.mean(axis=1))[0,1])},indent=2))