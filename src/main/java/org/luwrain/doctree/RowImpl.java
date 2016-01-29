/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015 Roman Volovodov <gr.rPman@gmail.com>

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

class RowImpl implements Row
{
    /** Absolute horizontal position in the area*/
int x = 0;

    /** Absolute vertical position in the area*/
    int y = 0;

    private int partsFrom = -1;
    private int partsTo = -1;

    @Override public int getRowX()
    {
	return x;
    }

    @Override public int getRowY()
    {
	return y;
    }

    String text(RowPart[] parts)
    {
	StringBuilder b = new StringBuilder();
	for(int i = partsFrom;i < partsTo;++i)
	    b.append(parts[i].text());
	return b.toString();
    }

    String textWithHrefs(RowPart[] parts, String hrefPrefix)
    {
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

    String href(RowPart[] parts, int pos)
    {
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

    boolean hasAssociatedText()
    {
	return partsFrom >= 0 && partsTo >= 0;
    }

    RowPart getFirstPart(RowPart[] parts)
    {
	return parts[partsFrom];
    }

    void mustIncludePart(int index)
    {
	//We are registering only a first part;
	if (partsFrom < 0)
	    partsFrom = index;
	if (partsTo < index + 1)
	    partsTo = index + 1;
    }

    static RowImpl[] buildRows(RowPart[] parts)
    {
	final RowImpl[] rows = new RowImpl[parts[parts.length - 1].absRowNum + 1];
	for(int i = 0;i < rows.length;++i)
	    rows[i] = new RowImpl();
	int current = -1;
	for(int i = 0;i < parts.length;++i)
	    rows[parts[i].absRowNum].mustIncludePart(i);
	return rows;
    }
}
