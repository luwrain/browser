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

package org.luwrain.doctree.dtbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.nio.file.*;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.luwrain.doctree.filters.DTBookXml;
import org.luwrain.doctree.dtbook.Smil;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class DTBook
{
    final static int DEFAULT_WIDTH=1024; 

    public enum Version {v3,v2};
    private Version version;

    // root directory combined with any file inside book 
    private Path rootDir;
    private Path realPath(String path)
    {
	return Paths.get(rootDir.toString(),path); 
    }

    public Vector<DTBookFile> dtbookFiles=new Vector<DTBookFile>();
	public Vector<String> itemOrder=new Vector<String>();

    // DTBook is a multiple files format
    // starts with *.opf in format 3.0 and ncc.html in 2.0
    // all files path relative rootDir bur startFile can be absolute path
    public void open(Path rootDir, Path startFile) throws Exception
    {
	if(rootDir==null) rootDir=startFile.getParent();
	// save root dir
	this.rootDir=rootDir;
	// clean up
	dtbookFiles=new Vector<DTBookFile>();
	itemOrder=new Vector<String>();
	// detect DTBook version, first step
	String fn=startFile.getFileName().toString().toLowerCase();
	if(fn.endsWith(".opf"))
	{ // 3.0
	    version=Version.v3;
	    open_v3(startFile.toFile());
	} else
	    if(fn.endsWith(".html")||fn.endsWith(".htm"))
	    {
		version=Version.v2;
		open_v2(startFile.toFile());
	    } else
	    {
		throw new Exception("Unsupported DTBook start file format");
	    }
    }

    // to disable dtd loading from internet while use DocumentBuilder.parse xml
    public static class NoOpEntityResolver implements EntityResolver
    {
	public InputSource resolveEntity(String publicId, String systemId)
	{
	    //			System.out.println("EntityResolver: "+publicId+", "+systemId);
	    return new InputSource(new StringBufferInputStream(""));
	}
    }

    public void open_v3(File startFile) throws Exception
    {
	// read files list
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setValidating(false);
	dbf.setIgnoringComments(true);
	dbf.setIgnoringElementContentWhitespace(true);
	DocumentBuilder db = dbf.newDocumentBuilder();
	db.setEntityResolver(new NoOpEntityResolver());
	org.w3c.dom.Document xmlDoc=db.parse(startFile);
	org.w3c.dom.NodeList obfMan=xmlDoc.getElementsByTagName("manifest");
	if(obfMan.getLength()==0)
	    throw new Exception("Bad OBF format. Have no 'manifest' element");
	org.w3c.dom.NodeList nList=obfMan.item(0).getChildNodes();
	for(int i=0;i<nList.getLength(); i++)
	{
	    org.w3c.dom.Node n = nList.item(i);
	    String nodeName=n.getNodeName().toLowerCase();
	    if(nodeName.equals("item"))
	    {
		NamedNodeMap nm=n.getAttributes();
		if(nm==null)
		    throw new Exception("Bad OBF format. Have no attributes in ITEM element");
		org.w3c.dom.Node id=nm.getNamedItem("id");
		if(id==null)
		    throw new Exception("Bad OBF format. Have no attribute 'id' in ITEM element");
		org.w3c.dom.Node href=nm.getNamedItem("href");
		if(href==null)
		    throw new Exception("Bad OBF format. Have no attribute 'href' in ITEM element");
		org.w3c.dom.Node type=nm.getNamedItem("media-type");
		if(type==null)
		    throw new Exception("Bad OBF format. Have no attribute 'media-type' in ITEM element");
		DTBookFile dtbf=new DTBookFile(id.getNodeValue(),href.getNodeValue(),type.getNodeValue().toLowerCase());
		dtbookFiles.add(dtbf);
	    }
	    // FIXME: now all other elements was ignored
	}
	org.w3c.dom.NodeList obfSpine=xmlDoc.getElementsByTagName("spine");
	if(obfSpine.getLength()==0)
	    throw new Exception("Bad OBF format. Have no 'spine' element");
	nList=obfSpine.item(0).getChildNodes();
	for(int i=0;i<nList.getLength(); i++)
	{
	    org.w3c.dom.Node n = nList.item(i);
	    String nodeName=n.getNodeName().toLowerCase();
	    if(nodeName.equals("itemref"))
	    {
		NamedNodeMap nm=n.getAttributes();
		if(nm==null)
		    throw new Exception("Bad OBF format. Have no attributes in ITEMREF element");
		org.w3c.dom.Node id=nm.getNamedItem("idref");
		if(id==null)
		    throw new Exception("Bad OBF format. Have no attribute 'idref' in ITEMREF element");
		itemOrder.add(id.getNodeValue());
	    }
	    // FIXME: now all other elements was ignored
	}
	// load needed files
	for(DTBookFile dtbf:dtbookFiles)
	{
	    if(dtbf.type.equals("application/smil"))
	    { // smil
		Smil parser=new Smil(false,realPath(dtbf.name).toString());
		dtbf.document=parser.constructDocument();
		System.out.println(dtbf.name+": "+dtbf.document.getRoot().subnodes[0].subnodes.length);
		//System.exit(0);
	    } else
		if(dtbf.type.equals("application/x-dtbook+xml"))
		{ // dtbook xml
		    DTBookXml parser=new DTBookXml(false,realPath(dtbf.name).toString());
		    dtbf.document=parser.constructDocument();
		    System.out.println(dtbf.name+": "+dtbf.document.getRoot().subnodes.length);
		} else
		    if(dtbf.type.equals("application/x-dtbncx+xml"))
		    { // catalog
						} else
			if(dtbf.type.equals("text/xml"))
			{ // other xml
			}
	}
    }
	
    public void open_v2(File startFile)
    {

	}
	
	// generate and rewrite document's files into specified directory
	// navigation - .ncx
	// root text document .xml or ncc.html
	// audio link .smil file (one or more)
	public void write(Path rootDir)
	{
		
	}
}
