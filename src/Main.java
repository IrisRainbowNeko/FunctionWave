import java.util.*;

public class Main
{
	public static void main(String[] args)
	{
		WaveScript script=new WaveScript();
		script.loadscript(Constant.FILE_SD_THIS+"极乐净土.txt");
		script.getMaker().writedata(Constant.FILE_SD_THIS+"极乐净土.wav");
		System.out.println("ok");
	}
}
