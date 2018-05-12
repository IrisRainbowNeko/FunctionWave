
import java.io.*;
import java.util.*;

public class WaveMaker
{
	private static byte[]  RIFF="RIFF".getBytes();  
    private static byte[] RIFF_SIZE=new byte[4];  
    private static byte[] RIFF_TYPE="WAVE".getBytes();  


    private static byte[] FORMAT="fmt ".getBytes();  
    private static byte[] FORMAT_SIZE=new byte[4];  
	private static byte[] FORMATTAG=new byte[2];  
	private static byte[] CHANNELS=new byte[2];  
    private static byte[] SamplesPerSec =new byte[4];  
    private static byte[] AvgBytesPerSec=new byte[4];  
    private static byte[] BlockAlign =new byte[2];  
    private static byte[] BitsPerSample =new byte[2];  

    private static byte[] DataChunkID="data".getBytes();  
    private static byte[] DataSize=new byte[4];  
    public static boolean isrecording=false;  

	Vector<WaveChannel> channels=new Vector<WaveChannel>();
	
	public WaveChannel addChannel(WaveChannel ch){
		channels.add(ch);
		return ch;
	}

	public void init(){  
//这里主要就是设置参数，要注意revers函数在这里的作用  

		FORMAT_SIZE=new byte[]{(byte)16,(byte)0,(byte)0,(byte)0};  
		byte[] tmp=revers(intToBytes(1));  
		FORMATTAG=new byte[]{tmp[0],tmp[1]};  
		CHANNELS=new byte[]{tmp[0],tmp[1]};  
		SamplesPerSec=revers(intToBytes(48000));//采样率
		AvgBytesPerSec=revers(intToBytes(96000));//比特率，每一帧2字节
		tmp=revers(intToBytes(2));  
		BlockAlign=new byte[]{tmp[0],tmp[1]};  
		tmp=revers(intToBytes(16));  
		BitsPerSample=new byte[]{tmp[0],tmp[1]};  
	}  
	public byte[] revers(byte[] tmp){  
		byte[] reversed=new byte[tmp.length];  
		for(int i=0;i<tmp.length;i++){  
			reversed[i]=tmp[tmp.length-i-1];  

		}  
		return reversed;  
	}  
	public byte[] intToBytes(int num){  
		byte[]  bytes=new byte[4];  
		bytes[0]=(byte)(num>>24);  
		bytes[1]=(byte)((num>>16)& 0x000000FF);  
		bytes[2]=(byte)((num>>8)& 0x000000FF);  
		bytes[3]=(byte)(num & 0x000000FF);  
		return bytes;  

	}  

	public int[] getdata(){
		int len=0;
		int[][] data_ch=new int[channels.size()][];
		for(int i=0;i<data_ch.length;i++){
			data_ch[i]=channels.get(i).getdata();
			len=Math.max(len,data_ch[i].length);
		}
		int[] data=new int[len];
		for(int i=0;i<len;i++){
			for(int u=0;u<data_ch.length;u++)
			data[i]+=(i>=data_ch[u].length?0:data_ch[u][i]);
		}
		return data;
	}

	public void writedata(String path){  
		int[] data=getdata();
		DataSize=revers(intToBytes(data.length));  
		RIFF_SIZE=revers(intToBytes(data.length+36-8));  
		File wavfile= new File(path);  
		FileOutputStream file=null;  

		try {  
			file=new FileOutputStream(wavfile);  
			BufferedOutputStream fw=new BufferedOutputStream(file);  
			init();  

			fw.write(RIFF);  
			fw.write(RIFF_SIZE);  
			fw.write(RIFF_TYPE);  
			fw.write(FORMAT);  
			fw.write(FORMAT_SIZE);  
			fw.write(FORMATTAG);  
			fw.write(CHANNELS);  
			fw.write(SamplesPerSec);  
			fw.write(AvgBytesPerSec);  
			fw.write(BlockAlign);  
			fw.write(BitsPerSample);  

			fw.write(DataChunkID);  
			fw.write(DataSize);  
			fw.write(int2byte_arr(data));
			fw.flush();  
		} catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
	}
	public byte[] int2byte_arr(int[] arr){
		byte[] byarr=new byte[arr.length];
		for(int i=0;i<arr.length;i++){
			byarr[i]=(byte)arr[i];
		}
		return byarr;
	}
}
