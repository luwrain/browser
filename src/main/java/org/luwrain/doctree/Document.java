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

import java.net.*;
import java.util.*;

import org.luwrain.core.NullCheck;
import org.luwrain.core.Log;

public class Document 
{
    private final Properties props = new Properties();
    private String title;
    //    private URL url;
    //    private String format;
    //    private String charset;
    private String[] hrefs;
    //    private Map<String, String> infoAttr = new HashMap<String, String>();


    private NodeImpl root;
    private Layout layout = new Layout(this);
    private ParagraphImpl[] paragraphs; //Only paragraphs which appear in document, no paragraphs without row parts
    public RowPart[] rowParts;
    private RowImpl[] rows;

    public HashMap<String,NodeImpl> idx=new HashMap<String,NodeImpl>();

    public Document(NodeImpl root)
    {
	this.root = root;
	NullCheck.notNull(root, "root");
	title = "";
    }

    public Document(String title, NodeImpl root)
    {
	this.root = root;
	this.title = title;
	NullCheck.notNull(root, "root");
	NullCheck.notNull(title, "title");
    }

    public void buildView(int width)
    {
	try {
	    Log.debug("doctree", "building view for width=" + width);
	    root.commit();
	    root.setEmptyMark();
	    root.removeEmpty();
	    root.calcWidth(width);
	    final RowPartsBuilder rowPartsBuilder = new RowPartsBuilder();
	    rowPartsBuilder.onNode(root);
	    rowParts = rowPartsBuilder.parts();
	    if (rowParts == null)
		rowParts = new RowPart[0];
	    Log.debug("doctree", "" + rowParts.length + " row parts prepared");
	    if (rowParts.length <= 0)
		return;
	    paragraphs = rowPartsBuilder.paragraphs();
	    Log.debug("doctree", "" + paragraphs.length + " paragraphs prepared");
	    root.calcHeight();
	    calcAbsRowNums();
	    root.calcPosition();
	    rows = RowImpl.buildRows(rowParts);
	    Log.debug("doctree", "" + rows.length + " rows prepared");
	    layout.calc();
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }

    public int getLineCount()
    {
	return layout.getLineCount();
    }

    public String getLine(int index)
    {
	return layout.getLine(index);
    }

    public Iterator getIterator()
    {
	return new Iterator(this);
    }

    private void calcAbsRowNums()
    {
	int currentParaTop = 0;
	for(ParagraphImpl p: paragraphs)
	{
	    p.topRowIndex = currentParaTop;
	    for(RowPart r: p.rowParts)
		r.absRowNum = r.relRowNum + currentParaTop;
	    currentParaTop += p.height;
	}
    }

    // recreate hash map index for all node's ids
    public void makeIndex()
    {
    	idx=new HashMap<String,NodeImpl>();
    	makeIndex(root);
    }
    public void makeIndex(NodeImpl node)
    {
    	// TODO: duplicate id in document, it would but can not be supported, make decision, what to do with this
    	if(node.id!=null) idx.put(node.id,node);
    	for(NodeImpl n:node.subnodes)
    		makeIndex(n);
    }

    public void setProperty(String propName, String value)
    {
	NullCheck.notEmpty(propName, "propName");
	NullCheck.notNull(value, "value");
	props.setProperty(propName, value);
    }

    public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	final String res = props.getProperty(propName);
	return res != null?res:"";
    }

    /*
    public void setUrl(URL url)
    {
	NullCheck.notNull(url, "url");
	this.url = url;
    }
    */

    public void setHrefs(String[] hrefs)
    {
	NullCheck.notNullItems(hrefs, "hrefs");
	this.hrefs = hrefs;
    }

    /*
    public void setInfoAttr(Map<String, String> infoAttr)
    {
	NullCheck.notNull(infoAttr, "infoAttr");
	this.infoAttr = infoAttr;
    }

    public void setFormat(String format)
    {
	NullCheck.notNull(format, "format");
	this.format = format;
    }

    public void setCharset(String charset)
    {
	NullCheck.notNull(charset, "charset");
	this.charset = charset;
    }
    */

    public URL getUrl()
    {
	final String value = getProperty("url");
	if (value.isEmpty())
	    return null;
	try {
	    return new URL(value);
	}
	catch(MalformedURLException e)
	{
	    return null;
	}
    }

    public String getTitle() { return title != null?title:""; }
    public NodeImpl getRoot() { return root; }
    public ParagraphImpl[] getParagraphs() { return paragraphs; }
    public RowImpl[] getRows() { return rows; }
    public RowPart[] getRowParts() { return rowParts; }
    //    public URL getUrl() {return url;}
    public String[] getHrefs(){return hrefs;}
    /*
    public Map<String, String> getInfoAttr() {return infoAttr;}
    public String getFormat() {return format;}
    public String getCharset() {return charset;}
    public Properties getProperties() {return props;}
    */


}
