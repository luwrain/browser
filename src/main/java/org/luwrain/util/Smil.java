

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

	public Type type(){return type;}
    }

    static public class File extends Entry
    {
	File()
	{
	    super(Type.FILE);
	}
    }

    static public boolean fromUrl(URL url)
    {
	NullCheck.notNull(url, "url");
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
	    return false;
	}
	return false;
    }

    static public boolean fromPath(Path path)
    {
	NullCheck.notNull(path, "path");
	org.jsoup.nodes.Document doc = null;
	try {
	    doc = Jsoup.parse(Files.newInputStream(path), "utf-8", "", Parser.xmlParser());
	}
	catch(Exception e)
	{
	    e.printStackTrace(); 
	    return false;
	}
	onNode(doc.body());
	return false;
    }

    static private Entry[] onNode(Node node)
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
		    res.getLast().entries = onNode(el);
		    break;
		case "par":
		    res.add(new Entry(Entry.Type.PAR));
		    res.getLast().entries = onNode(el);
		    break;
		case "audio":
		    res.add(onAudio(el));
		    break;
		case "text":
		    res.add(onText(el));
		    break;
		default:
		    Log.warning("smil", "unknown tag:" + name);
		}
		continue;
	    }
	}
	return res.toArray(new Entry[res.size()]);
    }

    static private Entry onAudio(Element el)
    {
	NullCheck.notNull(el, "el");
	return null;
    }

    static private Entry onText(Element el)
    {
	NullCheck.notNull(el, "el");
	return null;
    }
}
