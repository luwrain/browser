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

package org.luwrain.doctree.filters;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;

import org.luwrain.doctree.*;
import org.luwrain.core.NullCheck;
import org.luwrain.core.Log;

public class Zip
{
    String fileName = "";
    private String itemsContentType = "";
    private String itemsCharset = "";
    private URL itemsBaseUrl;

    //itemsContentType and itemsCharset may be empty (but not null), it content type is empty filter will be suggested using file names
    public Zip(String fileName, String itemsContentType,
	       String itemsCharset, URL itemsBaseUrl) throws IOException
    {
	NullCheck.notNull(fileName, "fileName");
	NullCheck.notNull(itemsContentType, "itemsContentType");
	NullCheck.notNull(itemsCharset, "itemsCharset");
	NullCheck.notNull(itemsBaseUrl, "itemsBaseurl");
	this.fileName = fileName;
	this.itemsContentType = itemsContentType;
	this.itemsCharset = itemsCharset;
	this.itemsBaseUrl = itemsBaseUrl;
    }

    public Document createDoc() throws Exception
    {
	ZipFile zip = null;
	try {
	    final NodeImpl root = NodeFactory.newNode(Node.Type.ROOT);
	    final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
	    zip = new ZipFile(fileName);
	    //	    Enumeration<ZipEntry> entries = zip.entries();
	    for(Enumeration e = zip.entries();e.hasMoreElements();)
	    {
		final ZipEntry entry = (ZipEntry)e.nextElement();
		Log.debug("doctree-zip", "reading zip entry with name \'" + entry.getName() + "\'");
		if(entry.isDirectory()) 
		    continue;
		final UrlLoader.Result res = null;//Factory.fromInputStream(zip.getInputStream(entry), itemsContentType, itemsCharset, itemsBaseUrl, Factory.chooseFilterByExtension(entry.getName()));
		if (res.type() == UrlLoader.Result.Type.OK)
		{
		    final Document subdoc = res.doc();
		    for(NodeImpl node: subdoc.getRoot().subnodes)
			subnodes.add(node);
		} else
		    Log.error("doctree-zip", "subdoc parser has returned code " + res.type());
	    }
	    root.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
	    return new Document(root);
	}
	finally {
	    if (zip != null)
		zip.close();
	}
    }
}
