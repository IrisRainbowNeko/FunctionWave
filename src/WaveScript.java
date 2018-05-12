import java.io.*;
import java.util.*;

public class WaveScript
{
	public static final int[][] UP={{0,0,0,0,0,0,0},
									{0,0,0,1,0,0,0},
									{1,0,0,1,0,0,0},
									{1,0,0,1,1,0,0},
									{1,1,0,1,1,0,0},
									{1,1,0,1,1,1,0},
									{1,1,1,1,1,1,0},
									{1,1,1,1,1,1,1}};
	
	public static final int[][] DOWN=
	{{0,0,0,0,0,0,-1},
	{0,0,-1,0,0,0,-1},
	{0,0,-1,0,0,-1,-1},
	{0,-1,-1,0,0,-1,-1},
	{0,-1,-1,0,-1,-1,-1},
	{-1,-1,-1,0,-1,-1,-1},
	{-1,-1,-1,-1,-1,-1,-1}};
	
	public static final int[] tone={0,2,4,5,7,9,11};
	WaveMaker maker;
	WaveChannel nowchannel;
	
	public static int bpm=120;
	public static final int sample_rate=48000;
	public static int one=sample_rate*60/bpm;
	public static int[] sig=new int[7];
	int volume=50;
	float playrate=1;
	Stack<int[]> loopstack=new Stack<int[]>();//count,line
	
	public void loadscript(String file){
		maker=new WaveMaker();
		String[] res=removestr(readwb(file),"/*","*/").split("\n");
		for(int i=0;i<res.length;i++){
			if(res[i].startsWith("//"))continue;
			res[i]=res[i].trim();
			String[] cmd=res[i].split(" ");
			switch(cmd[0].toLowerCase()){
				case "for":
					loopstack.push(new int[]{Integer.parseInt(cmd[1]),i});
					break;
				case "end":
					if(loopstack.size()<=0)break;
					if(--loopstack.lastElement()[0]<=0)loopstack.pop();
					else{
						i=loopstack.lastElement()[1];
					}
					break;
				case "channel":
					nowchannel=maker.addChannel(new WaveChannel());
					volume=50;
					break;
				case "bpm":
					bpm=Integer.parseInt(cmd[1]);
					one=sample_rate*60/bpm;
					break;
				case "keysig":
					int temp=Integer.parseInt(cmd[1]);
					if(temp>=0)sig=UP[temp];
					else sig=DOWN[-temp];
					break;
				case "fun":
					nowchannel.setFunction(cmd[1]);
					break;
				case "volume":
					volume=Integer.parseInt(cmd[1]);
					break;
				case "click":{
					String[] items=cmd[1].split(";");
					int[] yfs=new int[items.length];
					for(int u=0;u<items.length;u++){
						int change=0;
						boolean hy=false;
						if(items[u].endsWith("#"))change=1;
						else if(items[u].endsWith("b"))change=-1;
						else if(items[u].endsWith("※"))hy=true;
						if(hy||change!=0)items[u]=items[u].substring(0,items[u].length()-1);
						int[] yf=str2int(items[u].split(",|，"));
						yfs[u]=(yf[0]+3)*12+3+tone[yf[1]-1]+change+(hy?0:sig[yf[1]-1]);
					}
					nowchannel.noteOnOffNow((int)(Float.parseFloat(cmd[2])*one),yfs,volume);
					}break;
				case "down":{
						int change=0;
						boolean hy=false;
						if(cmd[1].endsWith("#"))change=1;
						else if(cmd[1].endsWith("b"))change=-1;
						else if(cmd[1].endsWith("※"))hy=true;
						
						if(hy||change!=0)cmd[1]=cmd[1].substring(0,cmd[1].length()-1);
						int[] yf=str2int(cmd[1].split(",|，"));
						nowchannel.noteOn((int)(Float.parseFloat(cmd[2])*one),(yf[0]+3)*12+3+tone[yf[1]-1]+change+(hy?0:sig[yf[1]-1]),volume);
					}break;
				case "up":{
						int change=0;
						boolean hy=false;
						if(cmd[1].endsWith("#"))change=1;
						else if(cmd[1].endsWith("b"))change=-1;
						else if(cmd[1].endsWith("※"))hy=true;
						
						if(hy||change!=0)cmd[1]=cmd[1].substring(0,cmd[1].length()-1);
						int[] yf=str2int(cmd[1].split(",|，"));
						nowchannel.noteOff((int)(Float.parseFloat(cmd[2])*one),(yf[0]+3)*12+3+tone[yf[1]-1]+change+(hy?0:sig[yf[1]-1]));
					}break;
				case "arpeggio":{
						String[] items=cmd[1].split(";");
						int[] yfs=new int[items.length];
						for(int u=0;u<items.length;u++){
							int change=0;
							boolean hy=false;
							if(items[u].endsWith("#"))change=1;
							else if(items[u].endsWith("b"))change=-1;
							else if(items[u].endsWith("※"))hy=true;
							
							if(hy||change!=0)items[u]=items[u].substring(0,items[u].length()-1);
							int[] yf=str2int(items[u].split(",|，"));
							yfs[u]=(yf[0]+3)*12+3+tone[yf[1]-1]+change+(hy?0:sig[yf[1]-1]);
						}
						nowchannel.noteArpeggio((int)(Float.parseFloat(cmd[2])*one),yfs,volume);
					}break;
				case "delay":{
						nowchannel.Delay((int)(Float.parseFloat(cmd[1])*one));
					}break;
			}
		}
	}
	
	public WaveMaker getMaker(){
		return maker;
	}
	
	public String removestr(String res,String start,String end){
		int p,ed;
		while((p=res.indexOf(start))!=-1){
			ed=res.indexOf(end,p);
			res=res.substring(0,p)+(ed==-1?"":res.substring(ed+end.length()));
		}
		return res;
	}
	
	public static String readwb(String name)
	{
		String re=null;
		try
		{
			InputStream is=new FileInputStream(name);
			byte[] b=new byte[is.available()];
			is.read(b);re=new String(b);
		}
		catch (IOException e)
		{}
		return re;
	}
	public static int[] str2int(String[] a){
		int[] b=new int[a.length];
		for(int i=0;i<b.length;i++){
			try{
				b[i]=Integer.parseInt(a[i]);
			}catch(Exception e){}
		}
		return b;
	}
	public static float[] str2float(String[] a){
		float[] b=new float[a.length];
		for(int i=0;i<b.length;i++){
			try{
				b[i]=Float.parseFloat(a[i]);
			}catch(Exception e){}
		}
		return b;
	}
}
