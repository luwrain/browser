
package org.luwrain.doctree;

import java.util.regex.*;

import org.luwrain.core.NullCheck;

public class AudioInfo
{
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

    @Override public String toString()
    {
	return "Audio: " + src + " (from " + beginPos + ", to " + endPos + ")";
    }
}
