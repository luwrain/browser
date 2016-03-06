
package org.luwrain.doctree;

import java.util.regex.*;

import org.luwrain.core.NullCheck;

public class AudioInfo
{
	static private final Pattern TIME_PATTERN = Pattern.compile("^(((?<hour>\\d{1,})\\:)?(?<min>\\d{1,2})\\:)?(?<sec>\\d{1,})(\\.(?<ms>\\d{1,}))?(?<n>h|min|s|ms)?$");

    private String src;
    /** Starting pos in milliseconds*/
private long beginPos;

    /** Ending pos in milliseconds*/
private long endPos;

    public AudioInfo(String src,
long beginPos)
    {
	NullCheck.notNull(src, "src");
	this.src = src;
	this.beginPos = beginPos;
	this.endPos = -1;
    }

    public AudioInfo(String src)
    {
	NullCheck.notNull(src, "src");
	this.src = src;
	this.beginPos = -1;
	this.endPos = -1;
    }

    public AudioInfo(String src,
		     long beginPos, long endPos)
    {
	NullCheck.notNull(src, "src");
	this.src = src;
	this.beginPos = beginPos;
	this.endPos = endPos;
    }

    // parse clock value, example: 3:22:55.91 or 43:15.044 or 102.02h
    static public long ParseTime(String value)
    {
	/*
	NullCheck.notNull(value, "value");
	Matcher m=p.matcher(value);
	if(!m.matches()) 
throw new IllegalArgumentException("Invalid time value:" + value);
	long res=0;
	final String hour = m.group("hour");
	final String min = m.group("min");
	final String sec = m.group("sec");
	final String ms = m.group("ms");
	final String n = m.group("n");
	if(hour != null) 
res += Long.parseLong(hour)*3600000;
	if(min != null) 
res += Long.parseLong(min)*60000;
	if(sec != null) 
res += Long.parseLong(sec)*1000;
	if(ms != null) 
	{
	    if(ms.length() > 3) 
ms = ms.substring(0,3);else
		if(ms.length() == 1) 
ms += "00";else
		    if(ms.length()==2) ms+="0";
	    res += Long.parseLong(ms);
	}
	if(n!=null)
	{
	    if(m.group("hour")!=null||m.group("min")!=null||m.group("sec")==null)
		throw new IllegalArgumentException("Illegal time format:" + value);
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
	*/
	return 0;
    }
}
