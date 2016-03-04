
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
    private final LinkedList<Smil.Entry> smils = new LinkedList<Smil.Entry>();

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

    @Override public Document getFirstDocument()
    {
	final Document[] res = getDocuments();
	return res.length > 0?res[0]:null;
    }

    @Override public Document openHref(String href)
    {
	return null;
    }


    void loadSmil(Path smilFile, URL urlBase)
    {
	NullCheck.notNull(smilFile, "smilFile");
	Log.debug("doctree-daisy", "reading SMIL " + smilFile.toString());
	final Smil.Entry smil = Smil.fromPath(smilFile);
	smils.add(smil);
	final LinkedList<String> textDocs = new LinkedList<String>();
	smil.saveTextSrc(textDocs);
	for(String s: textDocs)
	{
	    URL url = null;
	    try {
		final URL tmpUrl = new URL(urlBase, s);

	    url = new URL(tmpUrl.getProtocol(), tmpUrl.getHost(),
			  tmpUrl.getPort(), tmpUrl.getFile());
	    }
	    catch(MalformedURLException e)
	    {
		Log.error("doctree-daisy", "bad source in SMIL:" + s);
		e.printStackTrace();
		continue;
	    }
	    if (docs.containsKey(url))
		continue;
	    Document doc;
	    try {
		doc = Factory.fromUrl(url, "", "").doc();//FIXME:
	    }
	    catch(Exception e)
	    {
		Log.error("doctree-daisy", "unable to read a document from URL " + url.toString());
		e.printStackTrace();
		continue;
	    }
	    docs.put(url, doc);
	    System.out.println(url.toString());
	}
    }
}


