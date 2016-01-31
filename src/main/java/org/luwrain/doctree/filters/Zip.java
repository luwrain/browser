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

package org.luwrain.doctree.filters;

import java.util.*;
import java.util.zip.*;
import java.io.*;

import org.luwrain.doctree.*;
import org.luwrain.core.NullCheck;
import org.luwrain.core.Log;

public class Zip
{
    //        private String fileName = "";
    //    private String wholeText;
    private InputStream is = null;
    private boolean needToClose = false;
    private String itemsContentType = "";
    private String itemsCharset = "";
    private String itemsBaseUrl = "";

    public Zip(String fileName) throws IOException
    {
	//	this.fileName = fileName;
	NullCheck.notNull(fileName, "fileName");
	is = new FileInputStream(fileName);
	needToClose = true;
    }

    public Zip(InputStream is, String itemsContentType,
	       String itemsCharset, String itemsBaseUrl)
    {
	NullCheck.notNull(is, "is");
	NullCheck.notNull(itemsContentType, "itemsContentType");
	NullCheck.notNull(itemsCharset, "itemsCharset");
	NullCheck.notNull(itemsBaseUrl, "itemsBaseurl");
	this.is = is;
	this.itemsContentType = itemsContentType;
	this.itemsCharset = itemsCharset;
	this.itemsBaseUrl = itemsBaseUrl;
    }

    public Document createDoc() throws Exception
    {
	try {
	    final NodeImpl root = NodeFactory.newNode(Node.Type.ROOT);
	    final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
	    final ZipInputStream zip = new ZipInputStream(is);
	    ZipEntry entry = zip.getNextEntry();
	    while (entry != null)
	    {
		if(entry.isDirectory()) 
		    continue;
		/*
		int format=Factory.suggestFormat(entry.getName());
		if(format==Factory.UNRECOGNIZED)
		    continue;
		*/

		final Document subdoc = Factory.fromInputStream(zip, itemsContentType, itemsCharset, itemsBaseUrl, Factory.Format.UNRECOGNIZED);
		//		Document subdoc=Factory.loadFromStream(format,zip,null);
		for(NodeImpl node: subdoc.getRoot().subnodes)
		    subnodes.add(node);
		entry = zip.getNextEntry();
	    }
	    zip.close();
		root.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
		return new Document(root);
	}
	finally 
	{
	    if (needToClose)
		is.close();
	}
    }
}
