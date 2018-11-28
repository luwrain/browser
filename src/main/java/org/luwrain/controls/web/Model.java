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

final class Model
{
    static private final String LOG_COMPONENT = WebArea.LOG_COMPONENT;
    
    final Container[] containers;

    Model(Container[] containers)
    {
	NullCheck.notNullItems(containers, "containers");
	this.containers = containers;
    }

    View buildView()
    {
	return new View(this);
    }

    Container[] getContainers()
    {
	return containers.clone();
    }

    int getContainerCount()
    {
	return containers.length;
    }

    Container getContainer(int index)
    {
	return containers[index];
    }

    public void dumpToFile(File file) throws IOException
    {
	NullCheck.notNull(file, "file");
	final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
	try {
	    w.write(String.format("%d", containers.length) + " containers");
	    w.newLine();
	for(int i = 0;i < containers.length;++i)
	{
	    final Container c = containers[i];
	    StringBuilder b = new StringBuilder();
	    	    b.append("#").append(String.format("%d", i));
		    b.append("<").append(c.tagName).append("> (").append(c.className).append(")");
	    w.write(new String(b));
	    w.newLine();
	    b = new StringBuilder();
	    b.append("Rect: ").append(String.format("%d,%d,%d,%d", c.x, c.y, c.width, c.height));
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

}
