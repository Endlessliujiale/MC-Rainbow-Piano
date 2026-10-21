package com.pianofx.client.fx;
import java.util.Random;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_703;

/** The original helix feeds a bounded, animated score; new arrivals retire the previous slot owner. */
public final class Flyer {
    public static final int SUBSTEPS=1,BATCH_SIZE=12,MAX_TOP_WAIT=120;
    public static final double SPEED=0.22;
    private static final double FADE_OUT=90,REPLACE_FADE=36;
    public final int midi,velocity,arm,ring,glyphColor;
    public final float glyphScale;
    public final class_2960 glyphId;
    public final Random rng;
    public class_703 glyph,halo,halo2;
    public class_243 pos;
    public double progressU,age,lifeTicks;
    public boolean fading,glyphDirty;
    private final class_243 home,away;
    private final double startU,spiralTicks,lineSpeed,endAngle,orbitHold,topRadius,topY,birthClock;
    private double releaseAt=Double.POSITIVE_INFINITY,releaseAngle,releaseClock,landingAngle,blendTicks=200;
    private int slot=-1,pitch;
    private boolean entered,replaced;
    private double joinedAt,retiredAt=Double.POSITIVE_INFINITY;
    private double endingAt=Double.POSITIVE_INFINITY;
    private class_243 endingOrigin;

    public void endSong(){
        if(Double.isFinite(endingAt)||dead())return;
        endingAt=age+Math.floorMod(midi*17+arm*29+(int)(birthClock*13),81);
        lifeTicks=Math.min(lifeTicks,endingAt+120);
        endingOrigin=pos;
    }

    public Flyer(class_243 h,class_243 a,int arm,int midi,int velocity,long seed,FxStyle style,class_2960 id){this(h,a,arm,midi,velocity,seed,style,id,2400);}
    public Flyer(class_243 home,class_243 away,int arm,int midi,int velocity,long seed,FxStyle style,class_2960 id,double hold){
        this.home=home;this.away=away;this.arm=Math.floorMod(arm,3);this.midi=midi;this.velocity=velocity;this.birthClock=ScoreStaff.time();
        this.glyphId=id;this.glyphColor=style.glyphColor();this.rng=new Random(seed);
        float p=Math.max(0,Math.min(1,(midi-48)/36f));
        float vary=0.82f+0.36f*(1-(float)Math.pow(rng.nextDouble(),3));
        this.glyphScale=style.glyphScale()*(0.84f+0.36f*p+0.28f*velocity/127f)*vary;
        this.lineSpeed=SPEED/Math.max(0.55,Math.min(2.2,style.speed()));this.startU=0.008+rng.nextDouble()*0.022;
        this.spiralTicks=(1-startU)*Spiral.LENGTH[this.arm]/lineSpeed;this.ring=Math.floorMod(midi,2);this.orbitHold=Math.max(600,hold);
        class_243 end=Spiral.at(this.arm,1,home,away);this.endAngle=Math.atan2(end.field_1350-home.field_1350,end.field_1352-home.field_1352);
        this.topRadius=Math.hypot(end.field_1352-home.field_1352,end.field_1350-home.field_1350);this.topY=end.field_1351;
        this.pitch=ScoreStaff.pitchStep(midi);this.blendTicks=Math.max(200,35/lineSpeed);
        this.lifeTicks=spiralTicks+MAX_TOP_WAIT+blendTicks+orbitHold+FADE_OUT;this.pos=positionAt(0);
    }
    public boolean waitingAtTop(){return age>=spiralTicks&&!Double.isFinite(releaseAt);}
    public double topWait(){return Math.max(0,age-spiralTicks);}
    public boolean transferring(){return Double.isFinite(releaseAt)&&!entered;}
    public boolean orbiting(){return entered;}
    public boolean ascending(){return age<spiralTicks;}
    public boolean onScore(){return entered&&!replaced&&!dead();}
    public boolean replaced(){return replaced;}
    public int scoreSlot(){return slot;}
    public int scorePitch(){return pitch;}
    public double joinedAge(){return joinedAt;}
    public boolean dissolving(){return (entered&&(replaced||fading))||age>=endingAt;}
    public class_2960 renderGlyph(){
        if(!entered)return glyphId;
        return velocity>=96?FxStyle.G_EIGHTH:FxStyle.G_QUARTER;
    }
    public void release(){
        if(!waitingAtTop()||Double.isFinite(endingAt))return;
        ScoreStaff.configure(home,away);releaseAt=age;releaseClock=birthClock+age;
        releaseAngle=endAngle+lineSpeed/topRadius*(age-spiralTicks);
        double projected=releaseAngle+(lineSpeed/topRadius+ScoreStaff.SPIN)*0.5*blendTicks;
        slot=ScoreStaff.reserve(projected,releaseClock+blendTicks);
        double target=ScoreStaff.angle(slot,releaseClock+blendTicks);
        landingAngle=target+Math.PI*2*Math.rint((projected-target)/(Math.PI*2));
        lifeTicks=releaseAt+blendTicks+orbitHold+FADE_OUT;
    }
    public void retireForReplacement(){
        if(replaced||dead())return;
        replaced=true;retiredAt=age;lifeTicks=Math.min(lifeTicks,age+REPLACE_FADE);
    }
    public void step(double dt){
        age+=dt;
        if(!Double.isFinite(endingAt)&&waitingAtTop()&&topWait()>=MAX_TOP_WAIT)release();
        if(!entered&&age>=releaseAt+blendTicks){
            entered=true;joinedAt=age;glyphDirty=true;ScoreStaff.arrive(this);
        }
        if(age>=endingAt){
            double q=Math.max(0,(age-endingAt)/120),a=midi*2.399963+arm;
            pos=endingOrigin.method_1031(Math.cos(a)*q*0.8,q*0.9,Math.sin(a)*q*0.8);
        }else {pos=positionAt(age);if(Double.isFinite(endingAt))endingOrigin=pos;}
        fading=replaced||age>lifeTicks-FADE_OUT||age>=endingAt;
    }
    private class_243 positionAt(double t){
        if(t<spiralTicks){progressU=startU+(1-startU)*Math.max(0,t)/spiralTicks;return Spiral.at(arm,progressU,home,away);}
        progressU=1;
        if(t<releaseAt)return polar(topRadius,topY,endAngle+lineSpeed/topRadius*(t-spiralTicks));
        double dt=t-releaseAt;
        if(!entered&&dt<blendTicks){
            double q=dt/blendTicks,q2=q*q,q3=q2*q,k=smooth(q);
            double angle=(2*q3-3*q2+1)*releaseAngle+(q3-2*q2+q)*blendTicks*lineSpeed/topRadius+(-2*q3+3*q2)*landingAngle+(q3-q2)*blendTicks*ScoreStaff.SPIN;
            double y=ScoreStaff.lineY(pitch,angle,ScoreStaff.time())+0.18;
            return polar(topRadius+(ScoreStaff.radius()-topRadius)*k,topY+(y-topY)*k,angle);
        }
        class_243 p=ScoreStaff.at(slot,pitch,ScoreStaff.time());
        if(replaced){
            double f=Math.max(0,Math.min(1,(t-retiredAt)/REPLACE_FADE));
            double a=ScoreStaff.angle(slot,ScoreStaff.time());
            p=p.method_1031(Math.cos(a)*f*0.35,f*0.45,Math.sin(a)*f*0.35);
        }
        return p;
    }
    private class_243 polar(double r,double y,double a){return new class_243(home.field_1352+r*Math.cos(a),y,home.field_1350+r*Math.sin(a));}
    private double scoreScale(){return 0.29+0.055*velocity/127.0;}
    public float opacity(){
        double normal=replaced?1-smooth((age-retiredAt)/REPLACE_FADE):smooth((lifeTicks-age)/FADE_OUT);
        return(float)(normal*(1-smooth((age-endingAt)/120)));
    }
    public float glyphScaleNow(){
        double scale=glyphScale*(1+0.28*Math.min(1,age/spiralTicks));
        if(Double.isFinite(releaseAt)&&!entered){double k=smooth((age-releaseAt)/blendTicks);scale=scale*(1-k)+scoreScale()*k;}
        if(entered)scale=scoreScale()*(1+0.07*ScoreStaff.beat(ScoreStaff.time())+0.12*Math.exp(-(age-joinedAt)/16));
        return(float)(scale*smooth(age/24)*opacity());
    }
    private static double smooth(double q){q=Math.max(0,Math.min(1,q));return q*q*(3-2*q);}
    public void spread(double dt){}
    public boolean dead(){return age>=lifeTicks;}
}
