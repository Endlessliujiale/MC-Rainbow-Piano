package com.pianofx.client.fx;
import net.minecraft.*;
/** Client-side note lifetime and release envelope adapted from ExtendedNoteBlock's fader design. */
public final class PianoVoice extends class_1109 implements class_1117 {
    public final int midi;
    private final float level;
    private int age,releaseAt;
    private boolean done;
    public PianoVoice(class_2960 id,int midi,float pitch,float volume,double duration){
        super(id,class_3419.field_15250,volume,pitch,class_5819.method_43047(),false,0,class_1113.class_1114.field_5478,0,0,0,true);
        this.midi=midi;level=volume;releaseAt=(int)Math.max(2,Math.min(1200,Math.round(duration*20)));
    }
    public void release(){releaseAt=Math.min(releaseAt,age);}
    public void stop(){done=true;}
    public float envelope(){double q=Math.max(0,Math.min(1,(age-releaseAt)/8.0));return(float)(1-q*q*(3-2*q));}
    @Override public float method_4781(){return level*envelope();}
    @Override public void method_16896(){if(++age>=releaseAt+8)done=true;}
    @Override public boolean method_4793(){return done;}
}
