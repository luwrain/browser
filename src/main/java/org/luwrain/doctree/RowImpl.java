/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.doctree;

    import java.util.*;
import org.luwrain.core.NullCheck;

class RowImpl implements Row
{
    /** Absolute horizontal position in the area*/
int x = 0;

    /** Absolute vertical position in the area*/
    int y = 0;

    private RowPart[] parts;
    private int partsFrom = -1;
    private int partsTo = -1;

    private RowImpl(RowPart[] parts)
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

    //returns -1 if there is no matching pos
    private int getPartIndexUnderPos(int pos)
    {
	if (isEmpty())
	    return -1;
	int offset = 0;
	for(int i = partsFrom;i < partsTo;++i)
	{
	    final String text = parts[i].text();
	    if (text == null || text.isEmpty())
		continue;
	    if (pos >= offset && pos < offset + text.length())
		return i;
	    offset += text.length();
	}
	return -1;
    }

    //returns -1 if index is invalid
    private int partBeginsAt(int index)
    {
	if (isEmpty() || index < partsFrom || index >= partsTo)
	    return -1;
	if (index == partsFrom)
	    return 0;
	int offset = 0;
	for(int i = partsFrom;i < index;++i)
	{
	    final String text = parts[i].text();
	    if (text == null || text.isEmpty())
		continue;
	    offset += text.length();
	}
	return offset;
    }

    //returns -1 if index is invalid
    int runBeginsAt(Run run)
    {
	NullCheck.notNull(parts, "parts");
	NullCheck.notNull(run, "run");
	if (isEmpty())
	    return -1;
	int offset = 0;
	for(int i = partsFrom;i < partsTo;++i)
	{
	    final String text = parts[i].text();
	    if (text == null || text.isEmpty())
		continue;
	    if (parts[i].run == run)
		return offset;
	    offset += text.length();
	}
	return offset;
    }

    @Override public int getRowX()
    {
	return x;
    }

    @Override public int getRowY()
    {
	return y;
    }

    String text()
    {
	if (isEmpty())
	    return "";
	final StringBuilder b = new StringBuilder();
	for(int i = partsFrom;i < partsTo;++i)
	    b.append(parts[i].text());
	return b.toString();
    }

    String textWithHrefs(String hrefPrefix)
    {
	NullCheck.notNull(hrefPrefix, "hrefPrefix");
	if (isEmpty())
	    return "";
	boolean wasHref = false;
	final StringBuilder b = new StringBuilder();
	for(int i = partsFrom;i < partsTo;++i)
	{
	    final RowPart p = parts[i];
	    final String href = p.href();
	    if (!wasHref &&
		href != null && !href.trim().isEmpty())
		b.append(hrefPrefix);
	    b.append(parts[i].text());
	    wasHref = (href != null && !href.trim().isEmpty());
	}
	return b.toString();
    }

    private String href(int pos)
    {
	if (isEmpty())
	    return "";
	int offset = 0;
	for(int i = partsFrom;i < partsTo;++i)
	{
	    final RowPart p = parts[i];
	    final String text = p.text();
	    if (text == null || text.isEmpty())
		continue;
	    if (pos >= offset && pos < offset + text.length())
		return p.href();
	    offset += text.length();
	}
	return null;
    }

    private String hrefText(int pos)
    {
	if (isEmpty())
	    return "";
	int index = getPartIndexUnderPos(pos);
	if (index < 0 || !parts[index].hasHref())
	    return "";
	final StringBuilder b = new StringBuilder();
	while(index < partsTo && parts[index].hasHref())
	{
	    b.append(parts[index].text());
	    ++index;
	}
	return new String(b);
    }

    private boolean hasHref(int pos)
    {
	int index = getPartIndexUnderPos(pos);
	if (index < 0)
	    return false;
	return parts[index].hasHref();
    }

    //returns -1 if there is no any
    int findNextHref(int pos)
    {
	int index = getPartIndexUnderPos(pos);
	if (index < 0)
	    return -1;
	while (index < partsTo && parts[index].hasHref())
	    ++index;
	if (index >= partsTo)
	    return -1;
	while (index < partsTo && !parts[index].hasHref())
	    ++index;
	if (index >= partsTo)
	    return -1;
	return partBeginsAt(index);
    }

    boolean isEmpty()
    {
	return partsFrom < 0 || partsTo < 0;
    }

    RowPart getFirstPart()
    {
	if (isEmpty())
	    return null;
	return parts[partsFrom];
    }

    private void mustIncludePart(int index)
    {
	//We are registering a first part only
	if (partsFrom < 0)
	    partsFrom = index;
	if (partsTo < index + 1)
	    partsTo = index + 1;
    }

    static RowImpl[] buildRows(RowPart[] parts)
    {
	NullCheck.notNullItems(parts, "parts");
	final RowImpl[] rows = new RowImpl[parts[parts.length - 1].absRowNum + 1];
	for(int i = 0;i < rows.length;++i)
	    rows[i] = new RowImpl(parts);
	int current = -1;
	for(int i = 0;i < parts.length;++i)
	    rows[parts[i].absRowNum].mustIncludePart(i);
	return rows;
    }
}
