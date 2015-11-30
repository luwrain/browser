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
import org.luwrain.core.Log;
import org.luwrain.core.NullCheck;

public class Smil
{
	private String fileName;
	private String src;

	public Smil(boolean shouldRead, String arg)
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
		if(!rootNode.getNodeName().equalsIgnoreCase("smil"))
			throw new Exception("Bad SMIL format. Root xml element must be SMIL");
		// skip head and read body
		org.w3c.dom.NodeList smilBody=xmlDoc.getElementsByTagName("body");
		if(smilBody.getLength()==0)
			throw new Exception("Bad SMIL format. Have no BODY element");
		
		final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
		parseNode(subnodes,smilBody.item(0));
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
			// <!ELEMENT body (par|seq|text|audio|img|a)+ >
			if(nodeName.equals("seq"))
			{ // smil section - need to continue walk inside
				// get optional section id
				org.w3c.dom.NamedNodeMap nm=n.getAttributes();
				String id=null;
				if(nm!=null)
				{
					org.w3c.dom.Node xmlId=nm.getNamedItem("id");
					if(xmlId!=null)
						id=xmlId.getNodeValue();
				}
				if(nodes==null)
					throw new Exception("Bad SMIL format. Node SEQ can not be used here");
			    final NodeImpl section_node = NodeFactory.createSection(0);
			    section_node.setId(id);
			    nodes.add(section_node);
			    final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
			    // parse section children
			    parseNode(subnodes,n);
			    //
			    section_node.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
			} else
			if(nodeName.equals("par"))
			{ // text-audio element
				org.w3c.dom.NamedNodeMap nm=n.getAttributes();
				if(nm==null)
					throw new Exception("Bad SMIL format. Have no attributes in PAR element");
				org.w3c.dom.Node xmlId=nm.getNamedItem("id");
				if(xmlId==null)
					throw new Exception("Bad SMIL format. Have no attribute ID in PAR element");
				String id=xmlId.getNodeValue();
				// clean up text and audio info
				textSrcs=new Vector<String>();
				audioSrcs=new Vector<AudioInfo>();
				parseNode(null,n);
				// now store id, audio and text information
				if(nodes==null)
					throw new Exception("Bad SMIL format. Node PAR must be inside SEQ");
			    final NodeImpl smilpar_node = new SmilPar(
			    		textSrcs.toArray(new String[textSrcs.size()]),
			    		audioSrcs.toArray(new AudioInfo[audioSrcs.size()]));
			    smilpar_node.setId(id);
			    nodes.add(smilpar_node);
			} else
			// TODO: text and audio was ignored without par
			if(nodeName.equals("text"))
			{ // text-audio element
				org.w3c.dom.NamedNodeMap nm=n.getAttributes();
				if(nm==null)
					throw new Exception("Bad SMIL format. Have no attributes in TEXT element");
				org.w3c.dom.Node xmlSrc=nm.getNamedItem("src");
				if(xmlSrc==null)
					throw new Exception("Bad SMIL format. Have no attribute 'src' in TEXT element");
				//
				String text=xmlSrc.getNodeValue();
				textSrcs.add(text);
			} else
			if(nodeName.equals("audio"))
			{ // text-audio element
				org.w3c.dom.NamedNodeMap nm=n.getAttributes();
				if(nm==null)
					throw new Exception("Bad SMIL format. Have no attributes in AUDIO element");
				org.w3c.dom.Node xmlSrc=nm.getNamedItem("src");
				if(xmlSrc==null)
					throw new Exception("Bad SMIL format. Have no attribute 'src' in AUDIO element");
				org.w3c.dom.Node xmlBegin=nm.getNamedItem("clipBegin");
				if(xmlBegin==null)
					throw new Exception("Bad SMIL format. Have no attribute 'clipBegin' in AUDIO element");
				long begin=0;
				try
				{
					begin=AudioInfo.ParseClockValue(xmlBegin.getNodeValue());
				}
				catch(Exception e)
				{
					throw new Exception("Bad SMIL format. Error in attribute 'clipBegin' in AUDIO element. "+e.getMessage());
				}
				org.w3c.dom.Node xmlEnd=nm.getNamedItem("clipEnd");
				if(xmlEnd==null)
					throw new Exception("Bad SMIL format. Have no attribute 'clipEnd' in AUDIO element");
				long end=0;
				try
				{
					end=AudioInfo.ParseClockValue(xmlEnd.getNodeValue());
				}
				catch(Exception e)
				{
					throw new Exception("Bad SMIL format. Error in attribute 'clipEnd' in AUDIO element. "+e.getMessage());
				}
				//
				AudioInfo audio=new AudioInfo(xmlSrc.getNodeValue(),begin,end);
				audioSrcs.add(audio);
			} else
			{ // ignore all other elements
				//Log.debug("daisy-read","ignore smil element '"+n.getNodeName()+"'");
			}
		}
		
	}
}
