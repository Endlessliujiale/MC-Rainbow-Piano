package com.pianofx.client.gui;
import com.pianofx.client.ScorePlayer;
import com.pianofx.client.fx.*;
import com.pianofx.score.NoteEvent;
import net.minecraft.*;
import java.util.*;
import org.lwjgl.glfw.GLFW;

/** An independent performance layout; edit by dragging keys and right-clicking to rebind. */
public final class CustomPianoScreen extends class_437 {
    private final class_437 parent;
    private final PianoControls config=PianoControls.INSTANCE;
    private final Set<Integer> held=new HashSet<>();
    private boolean editing;
    private int dragging=-1,waiting=-1,mouseNote=-1;
    private double grabX,grabY;
    private String message="演奏：鼠标或键盘；左右方向键切换八度。";
    public CustomPianoScreen(class_437 parent){super(class_2561.method_43470("自定义钢琴"));this.parent=parent;if(ScorePlayer.playing())ScorePlayer.pause();}
    private void button(String text,int x,int y,int width,Runnable action){method_37063(class_4185.method_46430(class_2561.method_43470(text),b->action.run()).method_46434(x,y,width,20).method_46431());}
    @Override protected void method_25426(){
        button(editing?"完成布局":"编辑布局",12,30,66,()->{releaseHeld();editing=!editing;waiting=-1;method_41843();});
        button("音色："+config.soundName(),84,30,Math.min(172,field_22789-148),()->{NoteSound.releaseAll();config.soundMode=(config.soundMode+1)%3;config.save();method_41843();});
        button("重置位置",12,54,66,()->{config.resetLayout();config.save();});
        button("重置绑定",84,54,66,()->{config.resetKeys();config.save();});
        button("返回",field_22789-56,30,44,this::method_25419);
    }
    private int keyW(){return Math.max(28,Math.min(64,(field_22789-60)/8));}
    private int keyH(){return Math.max(16,Math.min(40,(field_22790-140)/6));}
    private int keyX(int index){return 16+(int)Math.round(config.x[index]*Math.max(1,field_22789-32-keyW()));}
    private int keyY(int index){return 116+(int)Math.round(config.y[index]*Math.max(1,field_22790-136-keyH()));}
    private int keyAt(double x,double y){for(int i=24;i>=0;i--)if(x>=keyX(i)&&x<keyX(i)+keyW()&&y>=keyY(i)&&y<keyY(i)+keyH())return i;return -1;}
    public static String noteName(int midi){String[] names={"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};return names[Math.floorMod(midi,12)]+(Math.floorDiv(midi,12)-1);}
    public static String keyName(int key){if(key<0)return "--";String name=GLFW.glfwGetKeyName(key,0);return name==null?"K"+key:name.toUpperCase(java.util.Locale.ROOT);}
    @Override public void method_25394(class_332 ctx,int mx,int my,float delta){
        ctx.method_25294(0,0,field_22789,field_22790,0xDD101827);
        // Screen.render applies the vanilla background blur. Draw the keyboard only after that pass.
        super.method_25394(ctx,mx,my,delta);
        ctx.method_51433(field_22793,"自定义钢琴  |  "+noteName(config.baseMidi)+" — "+noteName(config.baseMidi+24),12,10,0xFFFFDEAC,true);
        ctx.method_51433(field_22793,editing?"左键拖动琴键；右键选中琴键，然后按下新的键盘按键。":message,12,83,0xFFD2DDEC,true);
        ctx.method_51433(field_22793,waiting>=0?"等待绑定："+noteName(config.baseMidi+waiting)+"（Esc 取消）":"位置与绑定自动保存；重复绑定会交换，不会覆盖其他琴键。",12,98,0xFFCCAE79,true);
        for(int i=0;i<25;i++){
            int x=keyX(i),y=keyY(i),w=keyW(),h=keyH(),pitch=(config.baseMidi+i)%12;
            boolean black=pitch==1||pitch==3||pitch==6||pitch==8||pitch==10;
            int color=held.contains(i)?0xFFB88E44:(black?0xFF253044:0xFFE4DCD0);
            ctx.method_25294(x-1,y-1,x+w+1,y+h+1,waiting==i?0xFFFFD36A:0xFF647087);
            ctx.method_25294(x,y,x+w,y+h,color);
            int text=black?0xFFE4DCD0:0xFF1F2630;
            ctx.method_51433(field_22793,noteName(config.baseMidi+i)+" "+keyName(config.keys[i]),x+3,y+(h-8)/2,text,false);
        }
    }
    private void press(int index){if(!held.add(index))return;ScorePlayer.useManualStyle();FxManager.resetEnding();FxManager.playNote(ScorePlayer.anchor(),ScorePlayer.facing(),new NoteEvent(0,config.baseMidi+index,100,60));}
    private void release(int index){if(held.remove(index))NoteSound.release(config.baseMidi+index);}
    private void releaseHeld(){for(int i:new HashSet<>(held))release(i);mouseNote=-1;}
    @Override public boolean method_25402(double mx,double my,int button){
        if(super.method_25402(mx,my,button))return true;
        int i=keyAt(mx,my);if(i<0)return false;
        if(editing){if(button==1){waiting=i;return true;}if(button==0){dragging=i;grabX=mx-keyX(i);grabY=my-keyY(i);return true;}}
        else if(button==0){mouseNote=i;press(i);return true;}
        return false;
    }
    @Override public boolean method_25403(double mx,double my,int button,double dx,double dy){
        if(editing&&dragging>=0&&button==0){config.x[dragging]=Math.max(0,Math.min(1,(mx-grabX-16)/Math.max(1,field_22789-32-keyW())));config.y[dragging]=Math.max(0,Math.min(1,(my-grabY-116)/Math.max(1,field_22790-136-keyH())));return true;}
        return super.method_25403(mx,my,button,dx,dy);
    }
    @Override public boolean method_25406(double mx,double my,int button){if(dragging>=0){dragging=-1;config.save();}if(mouseNote>=0){release(mouseNote);mouseNote=-1;}return super.method_25406(mx,my,button);}
    @Override public boolean method_25404(int key,int scan,int mods){
        if(waiting>=0){if(key==256){waiting=-1;return true;}if(PianoControls.allowed(key)){config.bind(waiting,key);config.save();waiting=-1;}return true;}
        if(key==256||key==295){method_25419();return true;}
        if(key==262||key==263){releaseHeld();config.baseMidi=Math.max(24,Math.min(84,config.baseMidi+(key==262?12:-12)));config.save();return true;}
        if(!editing)for(int i=0;i<25;i++)if(config.keys[i]==key){press(i);return true;}
        return super.method_25404(key,scan,mods);
    }
    @Override public boolean method_16803(int key,int scan,int mods){for(int i=0;i<25;i++)if(config.keys[i]==key)release(i);return super.method_16803(key,scan,mods);}
    @Override public void method_25419(){releaseHeld();config.save();field_22787.method_1507(parent);}
    @Override public void method_25432(){releaseHeld();super.method_25432();}
    @Override public boolean method_25421(){return false;}
}
