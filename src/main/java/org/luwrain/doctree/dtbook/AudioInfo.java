package org.luwrain.doctree.dtbook;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AudioInfo
{
	public String src;
	// number of milliseconds
	public long begin;
	public long end;
	
	public AudioInfo(String src,long begin,long end)
	{
		this.src=src;
		this.begin=begin;
		this.end=end;
	}
	// parse clock value, example: 3:22:55.91 or 43:15.044 or 102.02h
	public static long ParseClockValue(String clock) throws Exception
	{
		final String pattern="^(((?<hour>\\d{1,})\\:)?(?<min>\\d{1,2})\\:)?(?<sec>\\d{1,})(\\.(?<ms>\\d{1,}))?(?<n>h|min|s|ms)?$";
		Pattern p=Pattern.compile(pattern);
		Matcher m=p.matcher(clock);
		if(!m.matches()) throw new Exception("Bad clock format");
		long res=0;
		String hour=m.group("hour");
		if(hour!=null) res+=Long.parseLong(hour)*3600000;
		String min=m.group("min");
		if(min!=null) res+=Long.parseLong(min)*60000;
		String sec=m.group("sec");
		if(sec!=null) res+=Long.parseLong(sec)*1000;
		String ms=m.group("ms");
		if(ms!=null) 
		{
			if(ms.length()>3) ms=ms.substring(0,3);else
		   	if(ms.length()==1) ms+="00";else
		   	if(ms.length()==2) ms+="0";
			res+=Long.parseLong(ms);
		}
		// check for Timecount format
		String n=m.group("n");
		if(n!=null)
		{
			// check for hour and min empty but sec not
			if(m.group("hour")!=null||m.group("min")!=null||m.group("sec")==null)
				throw new Exception("Bad clock format, timecount must be number");
			String cnt=m.group("sec");
			if(n.equals("ms"))
			{
				if(ms!=null)
					throw new Exception("Bad clock format, timecount of ms must be integer");
				res=Long.parseLong(cnt);
			} else
			if(n.equals("s"))
			{
				res=Long.parseLong(cnt)*1000;
				if(ms!=null) res+=Long.parseLong(ms);
			} else
			if(n.equals("min"))
		   	{
				res=Long.parseLong(cnt)*60000;
				if(ms!=null) res+=Long.parseLong(ms)*60;
			} else
			if(n.equals("h"))
			{
				res=Long.parseLong(cnt)*3600000;
				if(ms!=null) res+=Long.parseLong(ms)*3600;
	   		}
		}
		return res;
	}
}
