package com.pianofx.client.fx;

import java.util.Arrays;
import com.pianofx.client.ScorePlayer;
import com.pianofx.client.particle.SparkParticle;
import net.minecraft.class_243;
import net.minecraft.class_638;
import net.minecraft.class_703;
import net.minecraft.class_310;

/** A bounded musical staff outside the original helix; it never edits the helix renderer. */
public final class ScoreStaff {
    public static final int COLUMNS=72, CAPACITY=69;
    public static final double SPIN=0.007, LINE_GAP=0.5;
    private static final Flyer[] notes=new Flyer[COLUMNS];
    private static final int[] incoming=new int[COLUMNS];
    private static final class_703[] clefs=new class_703[3];
    private static class_243 home,away;
    private static double clock,radius,az,drive,energy,opacity,bpm=120;
    private static ScoreStaffParticle staff;
    private static long replacements,arrivals,dissolveBursts;
    private static double endingAt=Double.POSITIVE_INFINITY;
    private ScoreStaff(){}

    public static void configure(class_243 h,class_243 a){
        if(home!=null)return;
        home=h;away=a;
        class_243 edge=Spiral.at(0,1,h,a);
        radius=Math.hypot(edge.field_1352-h.field_1352,edge.field_1350-h.field_1350)+2;
        az=Math.atan2(a.field_1350,a.field_1352);
    }
    public static void reset(){
        if(staff!=null)staff.method_3085();staff=null;
        for(int i=0;i<3;i++){if(clefs[i]!=null)clefs[i].method_3085();clefs[i]=null;}
        Arrays.fill(notes,null);Arrays.fill(incoming,0);home=null;away=null;
        clock=drive=energy=opacity=0;arrivals=replacements=dissolveBursts=0;
        endingAt=Double.POSITIVE_INFINITY;
    }
    public static double time(){return clock;}
    public static double radius(){return radius;}
    public static class_243 home(){return home;}
    public static double opacity(){return opacity;}
    public static double beat(double time){return Math.pow(Math.max(0,Math.cos(time*bpm/1200*Math.PI*2)),6);}
    public static double angle(int slot,double time){return az+slot*Math.PI*2/COLUMNS+SPIN*time;}
    public static double wave(double angle,double time){
        // A travelling curved ribbon, with a slower secondary swell. All five lines share it.
        return (0.32+energy*0.10)*Math.sin(angle*3-time*0.048)
            +0.06*Math.sin(angle*5+time*0.022)+0.02*beat(time);
    }
    // Both original anchors (block center + 0.15 / + 0.9) resolve to the piano's supporting surface.
    public static double centerY(){return Math.floor(home.field_1351-0.5)+2;}
    public static double lineY(double step,double angle,double time){return centerY()+(step-4)*(LINE_GAP/2)+wave(angle,time);}
    public static void beginEnding(){if(!Double.isFinite(endingAt))endingAt=clock;}
    public static double visibility(double angle,double time){
        if(!Double.isFinite(endingAt))return 1;
        int section=Math.floorMod((int)Math.floor((angle-az-SPIN*time)*COLUMNS/(Math.PI*2)),COLUMNS);
        double q=Math.max(0,Math.min(1,(time-endingAt-Math.floorMod(section*37,81))/120));
        return 1-q*q*(3-2*q);
    }
    public static class_243 at(int slot,int pitchStep,double time){
        double a=angle(slot,time);
        return new class_243(home.field_1352+radius*Math.cos(a),lineY(pitchStep,a,time)+0.18,home.field_1350+radius*Math.sin(a));
    }
    public static int pitchStep(int midi){
        int[] diatonic={0,0,1,1,2,3,3,4,4,5,5,6};
        int step=Math.floorDiv(midi,12)*7+diatonic[Math.floorMod(midi,12)]-37; // E4 is the bottom line.
        // Keep the pitch class readable instead of piling every bass note on the bottom ledger.
        while(step< -2)step+=7;
        while(step>10)step-=7;
        return step;
    }
    public static int reserve(double projectedAngle,double landingTime){
        int best=-1;double bestCost=Double.POSITIVE_INFINITY;
        for(int i=0;i<COLUMNS;i++){
            if(i%24==0)continue; // Leave a clear column for each of the three clefs.
            double a=angle(i,landingTime);
            double d=Math.atan2(Math.sin(a-projectedAngle),Math.cos(a-projectedAngle));
            double cost=d*d+incoming[i]*0.022;
            if(notes[i]==null)cost-=0.003;
            if(cost<bestCost){bestCost=cost;best=i;}
        }
        incoming[best]++;return best;
    }
    public static void arrive(Flyer f){
        int slot=f.scoreSlot();incoming[slot]=Math.max(0,incoming[slot]-1);
        Flyer old=notes[slot];
        if(old!=null&&old!=f&&!old.dead()){
            old.retireForReplacement();replacements++;
        }
        notes[slot]=f;arrivals++;drive=Math.min(1,drive+0.4);
    }
    public static void remove(Flyer f){
        int slot=f.scoreSlot();if(slot<0)return;
        if(!f.orbiting())incoming[slot]=Math.max(0,incoming[slot]-1);
        if(notes[slot]==f)notes[slot]=null;
    }
    public static int count(){int n=0;for(Flyer f:notes)if(f!=null&&!f.dead())n++;return n;}
    public static int pending(){int n=0;for(int i:incoming)n+=i;return n;}
    public static Flyer noteAt(int slot){return notes[slot];}
    public static long replacements(){return replacements;}
    public static long arrivals(){return arrivals;}
    public static long dissolveBursts(){return dissolveBursts;}
    public static void pulse(int velocity){drive=Math.max(drive,velocity/127.0*0.7);}
    public static void tick(class_638 world){
        if(home==null)return;
        clock++;drive*=0.91;energy+=(drive-energy)*0.12;
        var score=ScorePlayer.currentScore();if(score!=null)bpm=Math.max(40,Math.min(220,score.bpm()));
        double target=count()>0||pending()>0?1:0;
        if(Double.isFinite(endingAt)){double q=Math.max(0,Math.min(1,(clock-endingAt)/240));target*=1-q*q*(3-2*q);}
        opacity+=(target-opacity)*0.08;
        if(world==null)return; // Numerical tests advance the same score clock without creating render handles.
        if(Double.isFinite(endingAt)&&(int)clock%4==0&&clock-endingAt<200){
            for(int i=0;i<9;i++){
                int col=(int)((clock*7+i*13)%COLUMNS);double a=angle(col,clock),v=visibility(a,clock);
                if(v>0.02&&v<0.99)fragment(world,at(col,(int)(clock+i)%5*2,clock),1,0.83f,0.48f,(int)clock+col);
            }
        }
        if(opacity<0.005&&target==0)return;
        if((int)clock%160==0&&staff!=null){staff.method_3085();staff=null;}
        if(staff==null||!staff.method_3086()){
            staff=new ScoreStaffParticle(world);
            class_310.method_1551().field_1713.method_3058(staff);
        }
        for(int i=0;i<3;i++){
            class_243 p=at(i*24,4,clock);
            if((int)clock%120==0&&clefs[i]!=null){clefs[i].method_3085();clefs[i]=null;}
            if(clefs[i]==null||!clefs[i].method_3086()){
                clefs[i]=SparkParticle.spawn(3,world,p.field_1352,p.field_1351,p.field_1350,0.65f,200000,6,FxStyle.G_TREBLE);
            }
            if(clefs[i]!=null){
                clefs[i].method_3063(p.field_1352,p.field_1351,p.field_1350);
                if(clefs[i] instanceof SparkParticle sp){sp.paint(1,0.88f,0.6f);sp.setBrightness((float)(0.65*opacity));}
            }
        }
    }
    public static void fragment(class_638 world,class_243 pos,float r,float g,float b,long seed){
        java.util.Random random=new java.util.Random(seed);
        class_703 p=SparkParticle.spawn(0,world,pos.field_1352,pos.field_1351,pos.field_1350,0.035f+random.nextFloat()*0.035f,22+random.nextInt(18),6);
        if(p!=null){p.method_34753((random.nextDouble()-0.5)*0.045,0.02+random.nextDouble()*0.035,(random.nextDouble()-0.5)*0.045);if(p instanceof SparkParticle sp){sp.paint(r,g,b);sp.setBrightness(0.8f);}}
    }
    public static void dissolve(class_638 world,Flyer f){
        if((int)f.age%4!=0)return;
        dissolveBursts++;
        float[] color=Rainbow.hsb(Math.floorMod(f.midi,12)/12f,0.78f,1);
        for(int i=0;i<3;i++){
            double x=(f.rng.nextDouble()-0.5)*0.45,y=(f.rng.nextDouble()-0.5)*0.55,z=(f.rng.nextDouble()-0.5)*0.45;
            class_703 p=SparkParticle.spawn(0,world,f.pos.field_1352+x,f.pos.field_1351+y,f.pos.field_1350+z,0.025f+f.rng.nextFloat()*0.035f,18+f.rng.nextInt(14),6);
            if(p!=null){p.method_34753(x*0.18,0.018+Math.abs(y)*0.1,z*0.18);if(p instanceof SparkParticle sp){sp.paint(color[0],color[1],color[2]);sp.setBrightness(0.7f);}}
        }
    }
}
