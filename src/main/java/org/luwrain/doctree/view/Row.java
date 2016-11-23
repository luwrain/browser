
package org.luwrain.doctree.view;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

class Row
{
    /** Absolute horizontal position in the area*/
int x = 0;

    /** Absolute vertical position in the area*/
    int y = 0;

    private RowPart[] parts;
    private int partsFrom = -1;
    private int partsTo = -1;

    Row(RowPart[] parts)
    {
	this.parts = parts;
    }

    //returns null if there is no suitable
    Run getRunUnderPos(int pos)
    {
	if (isEmpty())
	    return null;
	final int index = getPartIndexUnderPos(pos);
	if (index < 0)
    return null;
	return parts[index].run;
    }

    //returns null if there is no suitable
    Run[] getRuns()
    {
	if (isEmpty())
	    return new Run[0];
	final Vector<Run> res = new Vector<Run>();
	for(int i = partsFrom;i < partsTo;++i)
	{
	    final Run run = parts[i].run;
	    int k = 0;
	    for(k = 0;k < res.size();++k)
		if (res.get(k) == run)
		    break;
	    if (k >= res.size())
		res.add(run);
	}
	return res.toArray(new Run[res.size()]);
    }

    //returns -1 if index is invalid
    public int runBeginsAt(Run run)
    {
	NullCheck.notNull(parts, "parts");
	NullCheck.notNull(run, "run");
	if (isEmpty())
	    return -1;
	int offset = 0;
	for(int i = partsFrom;i < partsTo;++i)
	{
	    final String text = parts[i].getText();
	    if (text == null || text.isEmpty())
		continue;
	    if (parts[i].run == run)
		return offset;
	    offset += text.length();
	}
	return offset;
    }

    public String text()
    {
	if (isEmpty())
	    return "";
	final StringBuilder b = new StringBuilder();
	for(int i = partsFrom;i < partsTo;++i)
	    b.append(parts[i].getText());
	return b.toString();
    }

    public boolean isEmpty()
    {
	return partsFrom < 0 || partsTo < 0;
    }

    public int getRowX()
    {
	return x;
    }

    public int getRowY()
    {
	return y;
    }

    RowPart getFirstPart()
    {
	if (isEmpty())
	    return null;
	return parts[partsFrom];
    }

    //returns -1 if there is no matching pos
    private int getPartIndexUnderPos(int pos)
    {
	if (isEmpty())
	    return -1;
	int offset = 0;
	for(int i = partsFrom;i < partsTo;++i)
	{
	    final String text = parts[i].getText();
	    if (text == null || text.isEmpty())
		continue;
	    if (pos >= offset && pos < offset + text.length())
		return i;
	    offset += text.length();
	}
	return -1;
    }

    void mustIncludePart(int index)
    {
	//We are registering a first part only
	if (partsFrom < 0)
	    partsFrom = index;
	if (partsTo < index + 1)
	    partsTo = index + 1;
    }
}
