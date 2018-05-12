
import java.util.*;public class WaveChannel
{
	public static final int NOTE_ON=1;
	public static final int NOTE_OFF=2;
	public static final int DELAY=3;
	
	public String function;
	ArrayList<int[]> playEvents=new ArrayList<int[]>();//type,delay,info……
	Expression ex;
	
	public WaveChannel(){
		setFunction("sin(x)");
	}
	
	public void setFunction(String fun){
		ex=new Expression();
		try{
			ex.doTrans(fun);
			function=ex.display();
			ex.calculate_check(function,new double[]{0},new String[]{"x"});
		}catch (Exception e){System.out.println(e.toString());}
	}
	
	public void noteOn (int delay, int note, int volume)
	{
		int[] data = {NOTE_ON,delay,note,volume};
		playEvents.add (data);
	}

	public void noteOff (int delay, int note)
	{
		int[] data = {NOTE_OFF,delay,note};
		playEvents.add (data);
	}
	
	public void Delay(int delay){
		int[] data = {DELAY,delay};
		playEvents.add (data);
	}
	
	public void noteOnOffNow (int duration, int note, int volume)
	{
		noteOn (0, note, volume);
		noteOff (duration, note);
	}
	public void noteOnOffNow (int duration, int[] note, int volume)
	{
		for(int i:note){noteOn (0, i, volume/note.length);}
		noteOff (duration, note[0]);
		for(int i=1;i<note.length;i++)noteOff (0, note[i]);
	}
	public void noteArpeggio (int duration, int[] note, int volume)
	{
		noteOn(0,note[0],volume/note.length);
		for(int i=1;i<note.length;i++)noteOn((int)(WaveScript.one*0.05f), note[i], volume/note.length);
		noteOff (duration-(int)(WaveScript.one*0.05f)*note.length-1, note[0]);
		for(int i=1;i<note.length;i++)noteOff (0, note[i]);
	}
	
	public ArrayList<int[]> toTones(){
		ArrayList<int[]> tones=new ArrayList<int[]>();//time,tone,delay,vol
		ArrayList<int[]> queue=new ArrayList<int[]>();
		for(int i=0;i<playEvents.size();i++){
			int[] data=playEvents.get(i);
			if(i>0)data[1]+=playEvents.get(i-1)[1];
			switch(data[0]){
				case NOTE_ON:{
					queue.add(data);
				}break;
				case NOTE_OFF:{
					for(int u=0;u<queue.size();u++){
						int[] temp=queue.get(u);
						if(temp[0]==NOTE_ON&&temp[2]==data[2]){
							queue.remove(u);
							tones.add(new int[]{temp[1],temp[2],data[1]-temp[1],temp[3]});
							break;
						}
					}
				}break;
				case DELAY:{
					int st=(i==0?0:playEvents.get(i-1)[1]);
					tones.add(new int[]{st,-1,data[1]-st});
				}break;
			}
		}
		Collections.sort(tones, new Comparator<int[]>(){
				@Override
				public int compare(int[] p1, int[] p2)
				{
					return p1[0]-p2[0];
				}
			});
		return tones;
	}
	
	public int[] getdata(){
		int index=0;
		ArrayList<int[]> tones=toTones();
		ArrayList<int[]> msgq=new ArrayList<int[]>();
		int[] last=tones.get(tones.size()-1);
		int[] data=new int[last[0]+last[2]];
		
		for(int i=0;i<data.length;i++){
			if(index<tones.size())
			while(tones.get(index)[0]<=i){
				//System.out.println(tones.get(index)[1]);
				msgq.add(tones.get(index++));
				if(index>=tones.size())break;
			}
			for(int u=0;u<msgq.size();u++)
			if(msgq.get(u)[0]+msgq.get(u)[2]<=i){
				msgq.remove(u--);
			}
			if(msgq.size()==0)data[i]=0;
			else{
				for(int u=0;u<msgq.size();u++){
					int[] temp=msgq.get(u);
					if(temp[1]==-1)continue;
					data[i]+=(temp[3]*ex.calculate(function,new double[]{Math.PI*Constant.FREQUENCY[temp[1]]*(i-temp[0])/WaveScript.sample_rate},new String[]{"x"}));
				}
			}
			if(i%WaveScript.sample_rate==0)System.out.println("解析脚本:"+i/WaveScript.sample_rate+"/"+data.length/WaveScript.sample_rate);
		}
		return data;
	}
}
