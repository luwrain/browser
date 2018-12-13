/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.controls.web;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.controls.web.WebArea.Callback.MessageType;

final class View
{
    static private final String LOG_COMPONENT = WebArea.LOG_COMPONENT;
    
    private final Container[] containers;
    private final String[] lines;

    View(WebArea.Appearance appearance, Container[] containers)
    {
	NullCheck.notNull(appearance, "appearance");
	NullCheck.notNullItems(containers, "containers");
	this.containers = containers;
	this.lines = buildLines(containers, appearance);
    }

    boolean isEmpty()
    {
	return containers.length == 0;
    }

    Container getContainer(int index)
    {
	return containers[index];
    }

    int getContainerCount()
    {
	return containers.length;
    }

    Iterator createIterator()
    {
	if (isEmpty())
	    return null;
	return new Iterator(this, 0);
    }

    static private String[] buildLines(Container[] containers, WebArea.Appearance appearance)
    {
	NullCheck.notNullItems(containers, "containers");
	NullCheck.notNull(appearance, "appearance");
	int lineCount = 0;
	for(Container c: containers)
	    lineCount = Math.max(lineCount, c.textY + c.textHeight);
	Log.debug(LOG_COMPONENT, "preparing " + lineCount + " lines");
	final String[] lines = new String[lineCount];
	for(Container c: containers)
	{
	    for(int i = 0;i < c.rows.length;++i)
	    {
		final ContainerRow row = c.rows[i];
		final String text = appearance.getRowTextAppearance(row.getWebObjs());
		if (text.length() > c.textWidth)
		    Log.warning(LOG_COMPONENT, "row text \'" + text + "\' is longer than the width of the container (" + c.textWidth + ")");
		final int lineIndex = c.textY + i;
		lines[lineIndex] = putString(lines[lineIndex], text, c.textX);	
	    }
	}
	return lines;
    }

	static private String putString(String s, String fragment, int pos)
	{
	    NullCheck.notNull(s, "s");
	    NullCheck.notNull(fragment, "fragment");
	    final StringBuilder b = new StringBuilder();
	    if (pos > s.length())
	    {
		b.append(s);
		for(int i = s.length();i < pos;++i)
		    b.append(" ");
	    } else
		b.append(s.substring(0, pos));
	    b.append(fragment);
	    if (pos + fragment.length() < s.length())
		b.append(s.substring(pos + fragment.length()));
	    return new String(b);
	}
	
        void dumpToFile(File file) throws IOException
    {
	NullCheck.notNull(file, "file");
	int rightBound = 0;
	for(Container c: containers)
	{
	    final int value = c.x + c.width;
	    if (value > rightBound)
		rightBound = value;
	}
	final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
	try {
	    w.write(String.format("%d", containers.length) + " containers");
	    w.newLine();
	    w.write("Right bound: " + String.format("%d", rightBound));
	    w.newLine();
	for(int i = 0;i < containers.length;++i)
	{
	    final Container c = containers[i];
	    StringBuilder b = new StringBuilder();
	    b.append("#<").append(c.tagName).append(">");;
	    w.write(new String(b));
	    w.newLine();
	    b = new StringBuilder();
	    b.append("Graphical rect: ").append(String.format("%d,%d,%d,%d", c.x, c.y, c.x + c.width, c.y + c.height));
	    	    w.write(new String(b));
	    w.newLine();
	    b = new StringBuilder();
	    	    b.append("Text rect: ").append(String.format("%d,%d,%d,%d", c.textX, c.textY, c.textX + c.textWidth, c.textY + c.textHeight));
	    w.write(new String(b));
	    w.newLine();
	    final List<String> content = new LinkedList();
	    for(ContentItem item: c.content)
		dumpContentItem(item, " ", content);
	    for(String l: content)
	    {
		w.write(l);
		w.newLine();
	    }
	    }
	w.flush();
	}
	finally {
	    w.close();
	}
    }

    private void dumpContentItem(ContentItem item, String prefix, List<String> res)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(res, "res");
	if (item.isText())
	{
	    res.add(prefix + item.getText());
	    return;
	}
	res.add(prefix + "<" + item.tagName + ">");
	for(ContentItem i: item.children)
	    dumpContentItem(i, prefix + " ", res);
    }

            static String makeDumpFileName(String url)
    {
	NullCheck.notNull(url, "url");
	return url.replaceAll("/", ".").replaceAll(":", ".");
    }


    static final class Iterator
    {
	private final View view;
	private int pos = 0;
	private Container container = null;

	Iterator(View view, int pos)
	{
	    NullCheck.notNull(view, "view");
	    if (pos < 0)
		throw new IllegalArgumentException("pos (" + pos + ") may not be negative");
	    this.view = view;
	    this.pos = pos;
	    this.container = view.getContainer(this.pos);
	}

Container.Type getType()
	{
	    return container.type;
	}

	int getLineCount()
	{
	    return container.getRowCount();
	}

	WebObject[] getRow(int index)
	{
	    return container.getRow(index);
	}

	boolean isLastRow(int index)
	{
	    return container.getRowCount() > 0 && index + 1 == container.getRowCount();
	}

		boolean movePrev()
	{
	    if (pos == 0)
		return false;
	    --pos;
container = view.getContainer(pos);
	    return true;
	}

	boolean moveNext()
	{
	    if (pos + 1 >= view.getContainerCount())
		return false;
	    ++pos;
container = view.getContainer(pos);
	    return true;
	}
    }
}
