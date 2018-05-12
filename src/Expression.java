
import java.io.*;
import java.util.*;

public class Expression
 {
	private Stack<String> stack;
	//输入的表达式
	private String input;
	//输出道arraylist
	private ArrayList<String> arrayList;
	//记录数字的数组
	private String num;

	public Expression(){
		stack=new Stack<String>();
		arrayList=new ArrayList<String>();
		num="";input="";
	}

	public void doTrans(String input)throws Exception{
		if(!isEmpty(input))
		{
			throw new Exception("");
		}
		this.input=input;
		for(int i=0;i<input.length();i++)
		{ 
			char s=input.charAt(i);
			if(s=='+'||s=='-')
			{
				if(!num.equals("")){
					arrayList.add(num);
					num="";
				}
				if(i==0)
					arrayList.add("0");
				else if(input.charAt(i-1)=='('||input.charAt(i-1)==',')
					arrayList.add("0");
				getOperator(s+"",1);
			}
			else if((s=='*')||(s=='/'))
			{
				if(!num.equals(""))
				{
					arrayList.add(num);
					num="";
				}
				getOperator(s+"",2);
			}
			else if((s=='^')||s=='√')
			{
				if(!num.equals(""))
				{
					arrayList.add(num);
					num="";
				}
				if(s=='√')
				{
					if(i==0)
						arrayList.add("2");
					else if(!((input.charAt(i-1)<='9'&&input.charAt(i-1)>='0')||input.charAt(i-1)==')'||input.charAt(i-1)=='x'))
						arrayList.add("2");
				}
				getOperator(s+"",3);
			}
			else if((s==','))
			{
				if(!num.equals(""))
				{
					arrayList.add(num);
					num="";
				}
				getOperator(s+"",0);
			}
			else if(s=='(')
			{
				if(!num.equals(""))
				{
					arrayList.add(num);
					num="";
				}
				stack.push(s+"");
			}
			else if(s==')')
			{
				if(!num.equals(""))
				{
					arrayList.add(num);
					num="";
				}
				getOperator(s+"");
			}
			else
			{
				if(!(s>='0'&&s<='9'||s=='.')){
					int c=0;
					if (!num.equals("")) {
						arrayList.add(num);
						num = "";
						getOperator("*", 2);
						c=1;
					}
					int index=subfs(input, "+-*/^√,()", i);
					num=input.substring(i,index);
					i+=(index-i-1);
					if ("x,t,π,e,a,b,c,m,n".indexOf(num)!=-1) {
						arrayList.add(num);
						num="";
					}
				} else {
					num+=s;
				}
				ishs(Constant.FUNCTION_NAME);
			}
			if(i==input.length()-1&&!num.equals(""))
			{
				arrayList.add(num);
			}
		}
		while(!stack.empty())
		{
			arrayList.add(""+stack.pop());
		}
	}
	public void ishs(String[] ba)
	{
		for(int i=0;i<ba.length;i++)
		if(num.equals(ba[i]))
		{
			getOperator(num,4);
			num="";
			break;
		}
	}
	public int subfs(String a, String b, int i) {
        for (int i2=i;i2<a.length();i2++) {
            char c=a.charAt(i2);
            for (int i3=0;i3<b.length();i3++) {
                if (c==b.charAt(i3)) {
                    return i2;
                }
            }
        }
        return a.length();
    }
	
	public void getOperator(String opthis,int prec)
	{
		while(!stack.empty())
		{
			String top=stack.pop();
			if(top.equals("("))
			{
				stack.push(top);
				break;
			}
			else
			{
				int precX;
				if(top.equals(","))
				{
					precX=0;
				}
				else if(top.equals("+")||top.equals("-"))
				{
					precX=1;
				}
				else if(top.equals("*")||top.equals("/"))
				{
					precX=2;
				}
				else if(top.equals("^")||top.equals("√"))
					precX=3;
				else precX=4;
				if(precX<prec)
				{
					stack.push(top);
					break;
				}
				else
				{
					arrayList.add(top);
				}
			}
		}
		stack.push(opthis);
	}
	public void getOperator(String opthis)
	{
		while(!stack.empty())
		{
			String top=stack.pop();
			if(top.equals("("))
			{
				break;
			}
			else
			{
				arrayList.add(top);
			}
		}
	}
	
	private boolean isEmpty(String input)
	{
		boolean b=true;
		if(input==null||input.length()==0)
		{
			b=false;
		}
		return b;
	}
	public String display(){
		String displayStr="";
		for(int i=0;arrayList!=null&&i<arrayList.size();i++)
		{
			displayStr+=(arrayList.get(i)+"·");
		}
		return displayStr.substring(0,displayStr.length()-1);
	}
	public float calculate_check(String ii,double[] d1,String[] s1)throws Exception{
		String[] in=ii.split("·");
		Stack<Double> st=new Stack<Double>();
		uu:for(int i=0;i<in.length;i++)
		{
				if(in[i].length()==1)
				{
					if(d1!=null&&s1!=null)
					for(int u=0;u<d1.length;u++)
						if(in[i].equals(s1[u]))
						{st.push(d1[u]);continue uu;}
				if(in[i].equals(","))
					st.push(st.pop());
				else if(in[i].equals("+"))
					st.push(st.pop()+st.pop());
				else if(in[i].equals("-"))
				{double x=st.pop(),y=st.pop();
					st.push(y-x);}
				else if(in[i].equals("*"))
					st.push(st.pop()*st.pop());
				else if(in[i].equals("/"))
				{double x=st.pop(),y=st.pop();
					st.push(y/x);}
				else if(in[i].equals("^"))
				{double x=st.pop(),y=st.pop();
					st.push(Math.pow(y,x));}
				else if(in[i].equals("π"))
					st.push(Math.PI);
				else if(in[i].equals("e"))
					st.push(Math.E);
				else if(in[i].equals("√"))
					st.push(Math.pow(st.pop(),1/st.pop()));
				else
					st.push(Double.valueOf(in[i]));
				}
				else if(in[i].length()==2)
				{
					if(in[i].equals("lg"))
						st.push(Math.log10(st.pop()));
					else if(in[i].equals("ln"))
						st.push(Math.log(st.pop()));
					else
						st.push(Double.valueOf(in[i]));
				}
				else if(in[i].length()==3)
				{if(in[i].equals("sin"))
					st.push(Math.sin(st.pop()));
				else if(in[i].equals("cos"))
					st.push(Math.cos(st.pop()));
				else if(in[i].equals("tan"))
					st.push(Math.tan(st.pop()));
				else if(in[i].equals("log"))
					{double x=st.pop(),y=st.pop();
					st.push(Math.log10(x)/Math.log10(y));}
				else if(in[i].equals("abs"))
					st.push(Math.abs(st.pop()));
				else if(in[i].equals("max"))
					st.push(Math.max(st.pop(),st.pop()));
				else if(in[i].equals("min"))
					st.push(Math.min(st.pop(),st.pop()));
				else
					st.push(Double.valueOf(in[i]));}
				else if(in[i].length()==4)
				{if(in[i].equals("sinh"))
					st.push(Math.sinh(st.pop()));
				else if(in[i].equals("cosh"))
					st.push(Math.cosh(st.pop()));
				else if(in[i].equals("tanh"))
					st.push(Math.tanh(st.pop()));
				else
					st.push(Double.valueOf(in[i]));
				}
				else if(in[i].length()==6)
				{if(in[i].equals("arcsin"))
					st.push(Math.asin(st.pop()));
				else if(in[i].equals("arccos"))
					st.push(Math.acos(st.pop()));
				else if(in[i].equals("arctan"))
					st.push(Math.atan(st.pop()));
				else
					st.push(Double.valueOf(in[i]));}
				else
					st.push(Double.valueOf(in[i]));}
		if(st.size()!=1){throw new Exception();}
		return (float)(double)st.pop();
	}
	public float calculate(String ii,double[] d1,String[] s1){

		String[] in=ii.split("·");
		Stack<Double> st=new Stack<Double>();
		uu:for(int i=0;i<in.length;i++)
		{
			//int pp=fh("+-*/^",in[i].charAt(0));
			if(in[i].length()==1)
			{
				for(int u=0;u<d1.length;u++)
					if(in[i].equals(s1[u]))
					{st.push(d1[u]);continue uu;}
				if(in[i].equals(","))
					st.push(st.pop());
				else if(in[i].equals("+"))
					st.push(st.pop()+st.pop());
				else if(in[i].equals("-"))
				{double x=st.pop(),y=st.pop();
					st.push(y-x);}
				else if(in[i].equals("*"))
					st.push(st.pop()*st.pop());
				else if(in[i].equals("/"))
				{double x=st.pop(),y=st.pop();
					st.push(y/x);}
				else if(in[i].equals("^"))
				{double x=st.pop(),y=st.pop();
					st.push(Math.pow(y,x));}
				else if(in[i].equals("π"))
					st.push(Math.PI);
				else if(in[i].equals("e"))
					st.push(Math.E);
				else if(in[i].equals("√"))
					st.push(Math.pow(st.pop(),1/st.pop()));
				else
					st.push(Double.valueOf(in[i]));
			}
			else if(in[i].length()==2)
			{
				if(in[i].equals("lg"))
					st.push(Math.log10(st.pop()));
				else if(in[i].equals("ln"))
					st.push(Math.log(st.pop()));
				else
					st.push(Double.valueOf(in[i]));
			}
			else if(in[i].length()==3)
			{if(in[i].equals("sin"))
					st.push(Math.sin(st.pop()));
				else if(in[i].equals("cos"))
					st.push(Math.cos(st.pop()));
				else if(in[i].equals("tan"))
					st.push(Math.tan(st.pop()));
				else if(in[i].equals("log"))
				{double x=st.pop(),y=st.pop();
					st.push(Math.log10(x)/Math.log10(y));}
				else if(in[i].equals("abs"))
					st.push(Math.abs(st.pop()));
				else if(in[i].equals("max"))
					st.push(Math.max(st.pop(),st.pop()));
				else if(in[i].equals("min"))
					st.push(Math.min(st.pop(),st.pop()));
				else
					st.push(Double.valueOf(in[i]));}
			else if(in[i].length()==4)
			{if(in[i].equals("sinh"))
					st.push(Math.sinh(st.pop()));
				else if(in[i].equals("cosh"))
					st.push(Math.cosh(st.pop()));
				else if(in[i].equals("tanh"))
					st.push(Math.tanh(st.pop()));
				else
					st.push(Double.valueOf(in[i]));
			}
			else if(in[i].length()==6)
			{if(in[i].equals("arcsin"))
					st.push(Math.asin(st.pop()));
				else if(in[i].equals("arccos"))
					st.push(Math.acos(st.pop()));
				else if(in[i].equals("arctan"))
					st.push(Math.atan(st.pop()));
				else
					st.push(Double.valueOf(in[i]));}
			else
				st.push(Double.valueOf(in[i]));
		}
		//if(st.size()!=1){throw new Exception();}
		return (float)(double)st.pop();
	}
}
