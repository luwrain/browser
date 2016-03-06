

package org.luwrain.util;

import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.file.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.parser.*;

import org.luwrain.core.*;
import org.luwrain.doctree.AudioInfo;

public class Smil
{
    static public class Entry
    {
	public enum Type {SEQ, PAR, AUDIO, TEXT, FILE};

	private Type type;
	private String src = "";
	private String id = "";
	Entry[] entries;

	Entry(Type type)
	{
	    NullCheck.notNull(type, "type");
	    this.type = type;
	}

	Entry (Type type,
	       String id, String src)
	{
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(id, "id");
	    NullCheck.notNull(src, "src");
	    this.type = type;
	    this.id = id;
	    this.src = src;
	}

	public void saveTextSrc(List<String> res)
	{
	    if (type == Type.TEXT &&
		src != null && !src.isEmpty())
		res.add(src);
	    if (entries != null)
	    for(Entry e: entries)
		e.saveTextSrc(res);
	}

	public Entry findById(String id)
	{
	    NullCheck.notNull(id, "id");
	    if (this.id != null && this.id.equals(id))
		return this;
	    if (entries == null)
		return null;
	    for(Entry e: entries)
	    {
		final Entry res = e.findById(id);
		if (res != null)
		    return res;
	    }
	    return null;
	}

	public AudioInfo getAudioInfo()
	{
	    return null;
	}

	public Type type(){return type;}
	public Entry[] entries(){return entries;}
	public String src() {return src;}
	public String id() {return id;}
    }

    static public class File extends Entry
    {
	File()
	{
	    super(Type.FILE);
	}
    }

    static public Entry fromUrl(URL url, URL urlBase)
    {
	NullCheck.notNull(url, "url");
	NullCheck.notNull(urlBase, "urlBase");
	try {
	if (url.getProtocol().equals("file"))
	    return fromPath(Paths.get(url.toURI()), urlBase);
	}
	catch(URISyntaxException e)
	{
	    e.printStackTrace();
	    return null;
	}
	org.jsoup.nodes.Document doc = null;
	try {
	    final Connection con=Jsoup.connect(url.toString());
	    con.userAgent(org.luwrain.doctree.Factory.USER_AGENT);
	    con.timeout(30000);
	    doc = con.get();
	}
	catch(Exception e)
	{
	    e.printStackTrace(); 
	    return null;
	}
	final Entry res = new Entry(Entry.Type.FILE);
	    res.entries = onNode(doc.body(), urlBase);
	return res;
    }

    static public Entry fromPath(Path path, URL urlBase)
    {
	NullCheck.notNull(path, "path");
	org.jsoup.nodes.Document doc = null;
	try {
	    doc = Jsoup.parse(Files.newInputStream(path), "utf-8", "", Parser.xmlParser());
	}
	catch(Exception e)
	{
	    e.printStackTrace(); 
	    return null;
	}
	final Entry res = new Entry(Entry.Type.FILE);
	    res.entries = onNode(doc.body(), urlBase);
	return res;
    }

    static private Entry[] onNode(Node node, URL urlBase)
    {
	NullCheck.notNull(node, "node");
	final LinkedList<Entry> res = new LinkedList<Entry>();
	final LinkedList<org.luwrain.doctree.Run> runs = new LinkedList<org.luwrain.doctree.Run>();
	final List<Node> childNodes = node.childNodes();
	for(Node n: childNodes)
	{
	    final String name = n.nodeName();
	    if (n instanceof TextNode)
	    {
		final TextNode textNode = (TextNode)n;
		final String text = textNode.text();
		if (!text.trim().isEmpty())
		    Log.warning("smil", "unexpected text content:" + text);
		continue;
	    }
	    if (n instanceof Element)
	    {
		final Element el = (Element)n;
		switch(name.trim().toLowerCase())
		{
		case "seq":
		    res.add(new Entry(Entry.Type.SEQ));
		    res.getLast().entries = onNode(el, urlBase);
		    break;
		case "par":
		    res.add(new Entry(Entry.Type.PAR));
		    res.getLast().entries = onNode(el, urlBase);
		    break;
		case "audio":
		    res.add(onAudio(el, urlBase));
		    break;
		case "text":
		    res.add(onText(el, urlBase));
		    break;
		default:
		    Log.warning("smil", "unknown tag:" + name);
		}
		continue;
	    }
	}
	return res.toArray(new Entry[res.size()]);
    }

    static private Entry onAudio(Element el, URL urlBase)
    {
	NullCheck.notNull(el, "el");
	final String id = el.attr("id");
	final String src = el.attr("src");
	//	System.out.println(src);
	return new Entry(Entry.Type.AUDIO, id, src);
    }

    static private Entry onText(Element el, URL urlBase)
    {
	NullCheck.notNull(el, "el");
	final String id = el.attr("id");
	String src = el.attr("src");
	try {
	    src = new URL(urlBase, src).toString();
	}
	catch(MalformedURLException e)
	{
	    e.printStackTrace();
	}
	return new Entry(Entry.Type.TEXT, id, src);
    }
}
