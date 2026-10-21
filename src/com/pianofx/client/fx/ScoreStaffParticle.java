package com.pianofx.client.fx;

import com.pianofx.client.ShaderState;
import com.pianofx.client.particle.AdditiveSheet;
import net.minecraft.class_243;
import net.minecraft.class_638;
import net.minecraft.class_703;
import net.minecraft.class_3999;
import net.minecraft.class_4184;
import net.minecraft.class_4588;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_2960;
import net.minecraft.class_310;

/** New score ornament only. Original SparkParticle, Ribbon and render-sheet classes remain untouched. */
public final class ScoreStaffParticle extends class_703 {
    ScoreStaffParticle(class_638 world){
        super(world,ScoreStaff.home().field_1352,ScoreStaff.home().field_1351+3,ScoreStaff.home().field_1350);
        this.field_3847=1000000;this.field_3862=false;
    }
    @Override public void method_3070(){field_3866++;}
    @Override public class_3999 method_18122(){return ShaderState.active()?class_3999.field_17829:AdditiveSheet.INSTANCE;}
    @Override public void method_3074(class_4588 vc,class_4184 camera,float delta){
        if(ScoreStaff.home()==null)return;
        var atlas=class_310.method_1551().method_1531().method_4619(class_1059.field_17898);
        if(!(atlas instanceof class_1059 a))return;
        class_1058 sprite=a.method_4608(class_2960.method_60655("pianofx","streak"));
        class_243 eye=camera.method_19326();double time=ScoreStaff.time()+delta;
        double r=ScoreStaff.radius();class_243 h=ScoreStaff.home();
        float alpha=(float)(ScoreStaff.opacity()*(0.5+ScoreStaff.beat(time)*0.09));
        int segments=720;
        for(int line=0;line<5;line++){
            class_243 prev=null;
            for(int i=0;i<=segments;i++){
                double ang=ScoreStaff.angle(0,time)+i*Math.PI*2/segments;
                class_243 p=new class_243(h.field_1352+r*Math.cos(ang),ScoreStaff.lineY(line*2,ang,time),h.field_1350+r*Math.sin(ang));
                if(prev!=null)segment(vc,eye,sprite,prev,p,0.035f,alpha*(float)ScoreStaff.visibility(ang,time));
                prev=p;
            }
        }
        for(int col=0;col<ScoreStaff.COLUMNS;col+=6){
            double ang=ScoreStaff.angle(col,time)-Math.PI/ScoreStaff.COLUMNS;
            segment(vc,eye,sprite,new class_243(h.field_1352+r*Math.cos(ang),ScoreStaff.lineY(0,ang,time),h.field_1350+r*Math.sin(ang)),new class_243(h.field_1352+r*Math.cos(ang),ScoreStaff.lineY(8,ang,time),h.field_1350+r*Math.sin(ang)),0.028f,alpha*0.8f*(float)ScoreStaff.visibility(ang,time));
        }
        for(int col=0;col<ScoreStaff.COLUMNS;col++){
            Flyer f=ScoreStaff.noteAt(col);if(f==null||f.dead())continue;
            int pitch=f.scorePitch();if(pitch>=0&&pitch<=8)continue;
            int step=pitch<0?-2:10;double ang=ScoreStaff.angle(col,time),da=0.5/r;
            class_243 p=new class_243(h.field_1352+r*Math.cos(ang-da),ScoreStaff.lineY(step,ang-da,time),h.field_1350+r*Math.sin(ang-da));
            class_243 q=new class_243(h.field_1352+r*Math.cos(ang+da),ScoreStaff.lineY(step,ang+da,time),h.field_1350+r*Math.sin(ang+da));
            segment(vc,eye,sprite,p,q,0.03f,alpha*(float)ScoreStaff.visibility(ang,time));
        }
    }
    private static void segment(class_4588 vc,class_243 eye,class_1058 s,class_243 start,class_243 end,float width,float alpha){
        class_243 p=start.method_1020(eye),q=end.method_1020(eye),d=q.method_1020(p);
        double nx=d.field_1351*p.field_1350-d.field_1350*p.field_1351,ny=d.field_1350*p.field_1352-d.field_1352*p.field_1350,nz=d.field_1352*p.field_1351-d.field_1351*p.field_1352;
        double length=Math.sqrt(nx*nx+ny*ny+nz*nz);if(length<1e-8)return;
        double scale=width*0.5/length;float x=(float)(nx*scale),y=(float)(ny*scale),z=(float)(nz*scale);
        vertex(vc,p,x,y,z,1,s.method_4594(),s.method_4593(),alpha);vertex(vc,p,x,y,z,-1,s.method_4594(),s.method_4575(),alpha);
        vertex(vc,q,x,y,z,-1,s.method_4577(),s.method_4575(),alpha);vertex(vc,q,x,y,z,1,s.method_4577(),s.method_4593(),alpha);
        vertex(vc,q,x,y,z,1,s.method_4577(),s.method_4593(),alpha);vertex(vc,q,x,y,z,-1,s.method_4577(),s.method_4575(),alpha);
        vertex(vc,p,x,y,z,-1,s.method_4594(),s.method_4575(),alpha);vertex(vc,p,x,y,z,1,s.method_4594(),s.method_4593(),alpha);
    }
    private static void vertex(class_4588 vc,class_243 p,float x,float y,float z,int sign,float u,float v,float a){
        vc.method_22912((float)p.field_1352+x*sign,(float)p.field_1351+y*sign,(float)p.field_1350+z*sign).method_22913(u,v).method_22915(0.95f,0.83f,0.58f,a).method_60803(0xF000F0);
    }
}
