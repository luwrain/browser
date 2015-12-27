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
import java.util.Map.Entry;
import java.io.*;

import org.luwrain.doctree.Document;
import org.luwrain.doctree.Node;
import org.luwrain.doctree.NodeImpl;
import org.luwrain.doctree.NodeFactory;
import org.luwrain.doctree.ParagraphImpl;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;

import org.apache.poi.hwpf.*;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class Epub
{
    private String fileName = "";
    private String wholeText;

    public Epub(String fileName)
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

		EpubReader epubReader = new EpubReader();
		Book book = epubReader.readEpub(new FileInputStream(fileName));
		List<String> titles = book.getMetadata().getTitles();
		for(String s: titles)
		{
			NodeImpl h1=NodeFactory.createSection(1);
			h1.subnodes=new NodeImpl[]{NodeFactory.createPara(s)};
			subnodes.add(h1);
		}
		for(SpineReference r: book.getSpine().getSpineReferences())
		{
		    Resource res = r.getResource();
		    BufferedReader reader = new BufferedReader(res.getReader());
		    String result="";
		    String line;
		    while ( (line = reader.readLine()) != null)
		    {
//			    System.out.println("["+line.substring(0,Math.min(64,line.length()))+"]");
		    	if(line.startsWith("<?xml")) continue; // FIXME: make this fix inside Html parser
		    	result+=line+"\n";
		    }
		    	
			Html html=new Html(false,result);
			Document subdoc=html.constructDocument(width,"UTF-8");
			for(NodeImpl node:subdoc.getRoot().subnodes)
				subnodes.add(node);
		}
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
