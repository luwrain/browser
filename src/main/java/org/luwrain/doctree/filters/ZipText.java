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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.*;

import org.luwrain.doctree.Document;
import org.luwrain.doctree.Factory;
import org.luwrain.doctree.Node;
import org.luwrain.doctree.NodeImpl;
import org.luwrain.doctree.NodeFactory;

public class ZipText
{
    private String fileName = "";
    private String wholeText;

    public ZipText(String fileName)
    {
	this.fileName = fileName;
	if (fileName == null)
	    throw new NullPointerException("fileName may not be null");
    }

    public Document constructDocument(int width)
    {
	try {
		final NodeImpl root = NodeFactory.create(Node.ROOT);
		final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();

		FileInputStream input = new FileInputStream(fileName);
		ZipInputStream zip=new ZipInputStream(input);
		ZipEntry entry = zip.getNextEntry();
		while (entry!= null)
		{ // read all zip file content and merge into single document as is
			if(entry.isDirectory()) continue;
			int format=Factory.suggestFormat(entry.getName());
			if(format==Factory.UNRECOGNIZED) continue;
			
			byte[] buffer=new byte[(int)entry.getSize()]; // FEXME: make inmemory file load better
			int len=zip.read(buffer);
			// FIXME: make better encoding detection
			String utf8str=new String(buffer,"UTF-8");
			//String encoding=HtmlEncoding.getEncoding(utf8str);
//System.out.println("# file "+entry.getName()+" encoding "+encoding);       
			String content=utf8str;
			//if(!encoding.equals("UTF-8")) 
			//	content=new String(buffer,encoding);
System.out.println(content.substring(0,Math.min(64,content.length())));
			Document subdoc=Factory.loadFromText(format,content,width);
			for(NodeImpl node:subdoc.getRoot().subnodes)
				subnodes.add(node);
			entry = zip.getNextEntry();
		}
		zip.close();
		input.close();

		//Range range = doc.getRange();
		root.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
		return new Document(root, width);
	} catch (IOException e)
	{
	    e.printStackTrace();
	    return null;
	}
    }

}
