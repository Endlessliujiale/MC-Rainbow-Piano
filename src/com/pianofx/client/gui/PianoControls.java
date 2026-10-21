package com.pianofx.client.gui;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.util.*;
import com.pianofx.PianoFX;

/** Persistent, normalized key positions and physical keyboard mappings. */
public final class PianoControls {
    private static final int[] DEFAULT_KEYS={81,49,87,50,69,82,51,84,52,65,53,83,68,54,70,55,71,90,56,88,57,67,48,86,66};
    public static final PianoControls INSTANCE=load();
    public int soundMode=0; // 0: Extended Bright (+ multisample range fallback), 1: multisample, 2: Extended Grand.
    public int baseMidi=60;
    public int[] keys=DEFAULT_KEYS.clone();
    public double[] x=new double[25],y=new double[25];
    public PianoControls(){resetLayout();}
    private static Path path(){return FabricLoader.getInstance().getConfigDir().resolve("pianofx-controls.json");}
    private static PianoControls load(){
        try{if(Files.exists(path())){PianoControls data=new Gson().fromJson(Files.readString(path()),PianoControls.class);data.validate();return data;}}
        catch(Exception e){PianoFX.LOGGER.warn("[PianoFX] Cannot load custom keys, using defaults",e);}
        return new PianoControls();
    }
    public void validate(){
        if(keys==null||keys.length!=25)keys=DEFAULT_KEYS.clone();
        Set<Integer> unique=new HashSet<>();
        for(int i=0;i<25;i++)if(!allowed(keys[i])||!unique.add(keys[i]))keys[i]=-1;
        if(x==null||y==null||x.length!=25||y.length!=25){x=new double[25];y=new double[25];resetLayout();}
        for(int i=0;i<25;i++){x[i]=Double.isFinite(x[i])?Math.max(0,Math.min(1,x[i])):.5;y[i]=Double.isFinite(y[i])?Math.max(0,Math.min(1,y[i])):.5;}
        soundMode=Math.floorMod(soundMode,3);baseMidi=Math.max(24,Math.min(84,baseMidi));
    }
    public static boolean allowed(int key){return key>=32&&key<=348&&key!=256&&key!=258&&key!=295&&key!=262&&key!=263;}
    public void resetLayout(){for(int i=0;i<25;i++){x[i]=.05+.225*(i%5);y[i]=.05+.225*(i/5);}}
    public void resetKeys(){keys=DEFAULT_KEYS.clone();}
    public void bind(int index,int key){
        if(!allowed(key))throw new IllegalArgumentException("Reserved key");
        int previous=keys[index];for(int i=0;i<25;i++)if(i!=index&&keys[i]==key)keys[i]=previous;
        keys[index]=key;
    }
    public String soundName(){return switch(soundMode){case 1->"原版多采样 Bright";case 2->"扩展钢琴 Grand";default->"扩展钢琴 Bright";};}
    public void save(){try{validate();Files.createDirectories(path().getParent());Path temp=path().resolveSibling("pianofx-controls.json.tmp");Files.writeString(temp,new GsonBuilder().setPrettyPrinting().create().toJson(this));Files.move(temp,path(),StandardCopyOption.REPLACE_EXISTING);}catch(Exception e){PianoFX.LOGGER.error("[PianoFX] Cannot save custom keys",e);}}
}
