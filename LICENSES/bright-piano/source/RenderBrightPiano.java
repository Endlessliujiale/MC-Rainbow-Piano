import javax.sound.midi.*;
import javax.sound.sampled.*;
import com.sun.media.sound.AudioSynthesizer;
import java.io.*;
import java.util.*;

/** Offline asset build only. No audio device is opened. GM program 1 is Bright Piano. */
public final class RenderBrightPiano {
    public static void main(String[] args)throws Exception{
        Soundbank bank=MidiSystem.getSoundbank(new File(args[0]));
        Instrument bright=Arrays.stream(bank.getInstruments()).filter(i->i.getPatch().getBank()==0&&i.getPatch().getProgram()==1).findFirst().orElseThrow();
        System.out.println("Source preset: "+bright.getName()+", GM program "+bright.getPatch().getProgram());
        File out=new File(args[1]);out.mkdirs();
        for(int midi=0;midi<128;midi++){
            AudioSynthesizer synth=(AudioSynthesizer)MidiSystem.getSynthesizer();
            AudioFormat format=new AudioFormat(44100,16,1,true,false);
            try(AudioInputStream stream=synth.openStream(format,Map.of("reverb",false,"chorus",false,"load default soundbank",false))){
                synth.loadInstrument(bright);
                Receiver receiver=synth.getReceiver();
                receiver.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE,0,1,0),0);
                receiver.send(new ShortMessage(ShortMessage.NOTE_ON,0,midi,100),0);
                receiver.send(new ShortMessage(ShortMessage.NOTE_OFF,0,midi,0),1_800_000);
                try(AudioInputStream limited=new AudioInputStream(stream,format,(long)(44100*3.2))){
                    AudioSystem.write(limited,AudioFileFormat.Type.WAVE,new File(out,midi+".wav"));
                }
            }finally{synth.close();}
        }
        System.out.println("Rendered 128 individual pitches without octave folding.");
    }
}
