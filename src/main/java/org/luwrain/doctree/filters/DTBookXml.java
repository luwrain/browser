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
import java.util.regex.*;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.luwrain.doctree.*;
import org.luwrain.doctree.dtbook.AudioInfo;
import org.luwrain.doctree.dtbook.DTBook;
import org.luwrain.doctree.dtbook.DTBook.NoOpEntityResolver;
import org.w3c.dom.NamedNodeMap;
import org.luwrain.core.Log;
import org.luwrain.core.NullCheck;

public class DTBookXml
{
	private String fileName;
	private String src;

	public DTBookXml(boolean shouldRead, String arg)
	{
		NullCheck.notNull(arg, "arg");
		if (shouldRead)
		{
			fileName = arg;
			src = null;
		} else
		{
			fileName = "";
			src = arg;
		}
	}

	public Document constructDocument(int width) throws Exception
	{
		org.w3c.dom.Document xmlDoc=null;
		File file = new File(fileName);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(new DTBook.NoOpEntityResolver());
		if (src == null)
		{ // parse from file
			xmlDoc = db.parse(file);
		} else
		{ // parse from string
			xmlDoc = db.parse(src);
		}
		
		xmlDoc.getDocumentElement().normalize();
		
		// validate smil root element
		org.w3c.dom.Node rootNode=xmlDoc.getDocumentElement();
		if(!rootNode.getNodeName().equalsIgnoreCase("dtbook"))
			throw new Exception("Bad DTBOOK format. Root xml element must be DTBOOK");
		// skip head and read body
		org.w3c.dom.NodeList bookBody=xmlDoc.getElementsByTagName("book");
		if(bookBody.getLength()==0)
			throw new Exception("Bad DTBOOK format. Have no BOOK element");
		
		final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
		parseNode(subnodes,bookBody.item(0));
		final NodeImpl root = NodeFactory.create(Node.ROOT);
		root.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);

		Document luwrainDoc=new Document(root, width);
		
		return luwrainDoc;
	}

	private Vector<String> textSrcs=new Vector<String>();
	private Vector<AudioInfo> audioSrcs=new Vector<AudioInfo>();
	
	public void parseNode(LinkedList<NodeImpl> nodes,org.w3c.dom.Node root) throws Exception
	{
		org.w3c.dom.NodeList nList=root.getChildNodes();
		for(int i=0;i<nList.getLength(); i++)
		{
			org.w3c.dom.Node n = nList.item(i);
			String nodeName=n.getNodeName().toLowerCase();
			// check node have id or smilref attribute, if it, need to store this as section or paragraph
			String id=null;
			String smilref=null;
			NamedNodeMap nm=n.getAttributes(); 
			if(nm!=null)
			{
				org.w3c.dom.Node xmlId=nm.getNamedItem("id");
				if(xmlId!=null) id=xmlId.getNodeValue();
				org.w3c.dom.Node xmlSmil=nm.getNamedItem("smilref");
				if(xmlSmil!=null) smilref=xmlSmil.getNodeValue();
			}
			// have child nodes?
			boolean hasChild=(n.getChildNodes().getLength()>0);
			// current text is equal to node text, if exist or
			// empty text (empty tree child was removed automaticaly later by Document)
			String text=(n.getNodeValue()==null?"":n.getNodeValue().trim());
			// work with know nodes
			//System.out.println(nodeName+"["+id+","+smilref+"]: "+text);			
			if(nodeName.equals("img"))
			{ // image, transform to paragraph with ALT or SRC attribute as text
				if(nm!=null)
				{
					org.w3c.dom.Node xmlSrc=nm.getNamedItem("src");
					if(xmlSrc!=null) text=xmlSrc.getNodeValue();else
					{
						org.w3c.dom.Node xmlAlt=nm.getNamedItem("alt");
						if(xmlAlt!=null) text=xmlAlt.getNodeValue();
					}
				}
				// TODO: create image doctree elements
				final NodeImpl p_node = NodeFactory.createPara("image:"+text);
				p_node.setId(id);
				p_node.setSmil(smilref);
				nodes.add(p_node);
				final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
				parseNode(subnodes,n);
				p_node.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
			} else
			//if(nodeName.equals("level1")||nodeName.equals("level2")||nodeName.equals("level3")||nodeName.equals("level4")||nodeName.equals("level5"))
			if(nodeName.equals("h1")||nodeName.equals("h2")||nodeName.equals("h3")||nodeName.equals("h4")||nodeName.equals("h5")||nodeName.equals("h6")||nodeName.equals("h7")||nodeName.equals("h8")||nodeName.equals("h9"))
			{
				//int lvl=Integer.parseInt(nodeName.substring(5));
				int lvl=Integer.parseInt(nodeName.substring(1));
				final NodeImpl p_node = NodeFactory.createSection(lvl);
				p_node.setId(id);
				p_node.setSmil(smilref);
				nodes.add(p_node);
				final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
				parseNode(subnodes,n);
				p_node.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
			} else
			if(nodeName.equals("list"))
			{ // child - li elements
				String type="";
				if(nm!=null)
				{
					org.w3c.dom.Node xmlType=nm.getNamedItem("type");
					if(xmlType!=null) type=xmlType.getNodeValue().toLowerCase();
				}
				int nodeType=NodeImpl.UNORDERED_LIST;
				if(type.equals("ol")) nodeType=NodeImpl.ORDERED_LIST; 
				
				final NodeImpl p_node = NodeFactory.create(nodeType);
				p_node.setId(id);
				p_node.setSmil(smilref);
				nodes.add(p_node);
				final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
				parseNode(subnodes,n);
				p_node.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
			} else
			if(nodeName.equals("table")||nodeName.equals("tr")||nodeName.equals("td"))
			{
				int nodeType=NodeImpl.TABLE;
				//if(nodeName.equals("table")) nodeType=NodeImpl.TABLE;else
				if(nodeName.equals("tr")) nodeType=NodeImpl.TABLE_ROW;else
				if(nodeName.equals("td")) nodeType=NodeImpl.TABLE_CELL;
				final NodeImpl p_node = NodeFactory.create(nodeType);
				p_node.setId(id);
				p_node.setSmil(smilref);
				nodes.add(p_node);
				final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
				parseNode(subnodes,n);
				p_node.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
			} else
			{ // all other elements are paragraph
				// if have no information, skip
				if(id==null&&smilref==null&&hasChild==false&&text.isEmpty()) continue;
				//
				final NodeImpl p_node = text.isEmpty()?NodeFactory.createPara():NodeFactory.createPara(text);
				p_node.setId(id);
				p_node.setSmil(smilref);
				nodes.add(p_node);
				final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
				parseNode(subnodes,n);
				p_node.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
			}
		}
		
	}
}
