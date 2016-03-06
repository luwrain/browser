
package org.luwrain.doctree.books; 

import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.NullCheck;
import org.luwrain.core.Log;
import org.luwrain.doctree.*;
import org.luwrain.util.*;

class Daisy2 implements Book
{
    private final HashMap<URL, Document> docs = new HashMap<URL, Document>();
    private final HashMap<URL, Smil.Entry> smils = new HashMap<URL, Smil.Entry>();
    private Document nccDoc;

    @Override public Document[] getDocuments()
    {
	final LinkedList<Document> res = new LinkedList<Document>();
	for(Map.Entry<URL, Document> e: docs.entrySet())
	    res.add(e.getValue());
	return res.toArray(new Document[res.size()]);
    }

    @Override public Map<URL, Document> getDocumentsWithUrls()
    {
	return docs;
    }

    @Override public Document getStartingDocument()
    {
	return nccDoc;
    }

    @Override public Document getDocument(String href)
    {
	NullCheck.notNull(href, "href");
	URL url, noRefUrl;
	try {
	    url = new URL(href);
	    noRefUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
	}
	catch(MalformedURLException e)
	{
	    e.printStackTrace();
	    return null;
	}
	if (smils.containsKey(noRefUrl))
	{
	    final Smil.Entry entry = smils.get(noRefUrl);
	    final Smil.Entry requested = entry.findById(url.getRef());
	    if (requested != null)
	    {
		if (requested.type() == Smil.Entry.Type.TEXT)
		return getDocument(requested.src()); else
		{
		    Log.warning("doctree-daisy", "URL " + href + " points to a SMIL entry, but its type is " + requested.type());
		    return null;
		}
	    }
	} //smils;
    if (docs.containsKey(noRefUrl))
    {
	final Document res = docs.get(noRefUrl);
	return res;
    }
	return null;
    }

    @Override public Document openHref(String href)
    {
	return null;
    }

    @Override public AudioInfo findAudioForId(String id)
    {
	NullCheck.notNull(id, "id");
	Log.debug("doctree-daisy", "searching audio for " + id);
	for(Map.Entry<URL, Smil.Entry> e: smils.entrySet())
	{
	    final Smil.Entry entry = findSmilEntryWithText(e.getValue(), id);
	    if (entry != null)
		System.out.println("found");
	}
	return null;
    }

    void init(Document nccDoc)
    {
	NullCheck.notNull(nccDoc, "nccDoc");
	final String[] allHrefs = nccDoc.getHrefs();
	final LinkedList<String> textSrcs = new LinkedList<String>();
	for(String h: allHrefs)
	    try {
URL url = new URL(h);
url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
		if (url.getFile().toLowerCase().endsWith(".smil"))
		    loadSmil(url, textSrcs); else
		    textSrcs.add(url.toString());
	    }
	    catch(MalformedURLException e)
	    {
		e.printStackTrace();
	    }
	Log.debug("doctree-daisy", "" + smils.size() + " SMIL(s) loaded");

	for(String s: textSrcs)
	    try {
URL url = new URL(s);
url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
		    loadDoc(url);
	    }
	    catch(MalformedURLException e)
	    {
		e.printStackTrace();
	    }
	Log.debug("doctree-daisy", "" + docs.size() + " documents loaded");
	this.nccDoc = nccDoc;
    }

    private void loadSmil(URL url, LinkedList<String> textSrcs)
    {
	if (smils.containsKey(url))
	    return;
	Log.debug("doctree-daisy", "reading SMIL " + url.toString());
	final Smil.Entry smil = Smil.fromUrl(url, url);
	smils.put(url, smil);
	smil.saveTextSrc(textSrcs);
    }

    private void loadDoc(URL url)
    {
	if (docs.containsKey(url))
	    return;
	Result res;
	try {
	    res = Factory.fromUrl(url, "", "");
	}
	catch(Exception e)
	{
	    Log.error("doctree-daisy", "unable to read a document from URL " + url.toString());
	    e.printStackTrace();
	    return;
	}
	if (res.type() != Result.Type.OK)
	{
	    Log.warning("doctree-daisy", "unable to load a document by URL " + url + ":" + res.toString());
	    return;
	}
	if (res.book() != null)
	{
	    Log.debug("doctree-daisy", "the URL " + url + "references a book, not including to current one");
	    return;
	}
	docs.put(url, res.doc());
    }

    static private Smil.Entry findSmilEntryWithText(Smil.Entry entry, String src)
    {
	NullCheck.notNull(entry, "entry");
	NullCheck.notNull(src, "src");
	switch(entry.type() )
	{
	case TEXT:
	    return (entry.src() != null && entry.src().equals(src))?entry:null;
	case AUDIO:
	    return null;
	case FILE:
	case SEQ:
	    if (entry.entries() == null)
		return null;
	    for (int i = 0;i < entry.entries().length;++i)
	    {
		final Smil.Entry res = findSmilEntryWithText(entry.entries()[i], src);
		if (res == null)
		    return null;
		if (i == 0)
		    return entry;
		return entry.entries()[i];
	    }
	    return null;
	case PAR:
	    if (entry.entries() == null)
		return null;
	    for(Smil.Entry e: entry.entries())
	    {
		final Smil.Entry res = findSmilEntryWithText(e, src);
		if (res != null)
		    return entry;
	    }
	    return null;
	default:
	    Log.warning("doctree-daisy", "unknown SMIL entry type:" + entry.type());
	    return null;
	}
    }

    static private void collectAudioStartingAtEntry(Smil.Entry entry, LinkedList<AudioInfo> audioInfos)
    {
	NullCheck.notNull(entry, "entry");
	NullCheck.notNull(audioInfos, "audioInfos");
	if (entry.type() == Smil.Entry.Type.AUDIO)
	{
	    audioInfos.add(entry.getAudioInfo());
	    return;
	}
	if (entry.type() == Smil.Entry.Type.TEXT)
	    return;
	if (entry.type() == Smil.Entry.Type.PAR && entry.entries() != null)
	{
	    for(Smil.Entry e: entry.entries())
		collectAudioStartingAtEntry(e, audioInfos);
	    return;
	}
	if (entry.type() == Smil.Entry.Type.SEQ && entry.entries() != null &&
	    entry.entries().length >= 1)
	{
	    collectAudioStartingAtEntry(entry.entries()[0], audioInfos);
	    return;
	}
	Log.warning("doctree-daisy", "unknown SMIL entry type:" + entry.type());
    }
}
