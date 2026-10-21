package com.pianofx.client.fx;
import com.pianofx.score.NoteEvent;
import com.pianofx.client.gui.PianoControls;
import net.minecraft.*;
import java.util.*;
/** ExtendedNoteBlock piano source + note release envelope, with the proven multisample bank retained. */
public final class NoteSound {
    private static final List<PianoVoice> voices=new ArrayList<>();
    public record Choice(String sound,int root,float pitch,boolean multisample){}
    private NoteSound(){}
    public static String preset(){return "ExtendedNoteBlock piano + original Bright Piano multisample fallback";}
    public static Choice choose(int note,int mode){
        int midi=Math.max(0,Math.min(127,note));
        if(mode==1||midi<48||midi>72)return new Choice("bright_piano_"+midi,midi,1,true);
        return new Choice(mode==2?"enb_grand":"enb_bright",60,(float)Math.pow(2,(midi-60)/12.0),false);
    }
    public static void play(NoteEvent note){
        class_310 mc=class_310.method_1551();if(mc.field_1724==null||note.velocity()<=0)return;
        Choice choice=choose(note.midi(),PianoControls.INSTANCE.soundMode);
        float velocity=Math.max(0,Math.min(127,note.velocity()))/127f;
        PianoVoice voice=new PianoVoice(class_2960.method_60655("pianofx",choice.sound()),note.midi(),choice.pitch(),.60f*velocity,note.durSec());
        voices.removeIf(PianoVoice::method_4793);
        while(voices.size()>=192)voices.remove(0).stop();
        voices.add(voice);mc.method_1483().method_4873(voice);
    }
    public static void release(int midi){for(PianoVoice voice:voices)if(voice.midi==midi)voice.release();}
    public static void releaseAll(){for(PianoVoice voice:voices)voice.release();}
}
