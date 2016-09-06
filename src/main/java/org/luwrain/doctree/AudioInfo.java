
package org.luwrain.doctree;

import java.util.regex.*;
import java.net.*;

import org.luwrain.core.NullCheck;

public class AudioInfo
{
    private String src;
private long beginPos;
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

    public String src() {return src;}
    public long beginPosMsec() {return beginPos;}
    public long endPosMsec() {return endPos;}

    public boolean covers(String audioFileUrl, long msec)
    {
	if (!src.equals(audioFileUrl))
	    return false;
	if (endPos < 0)
	    return msec >= beginPos;
	return msec >= beginPos && msec <= endPos;
    }

    public boolean covers(String audioFileUrl, long msec, URL baseUrl)
    {
	NullCheck.notNull(baseUrl, "baseUrl");
	try {
	    if (!(new URL(baseUrl, src).toString()).equals(new URL(baseUrl,audioFileUrl).toString()))
		return false;
	}
	catch(MalformedURLException e)
	{
	    e.printStackTrace();
	    return false;
	}
	if (endPos < 0)
	    return msec >= beginPos;
	return msec >= beginPos && msec <= endPos;
    }


    @Override public String toString()
    {
	return "Audio: " + src + " (from " + beginPos + ", to " + endPos + ")";
    }
}
