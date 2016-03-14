
package org.luwrain.doctree;

import java.util.*;
import java.net.*;

import org.luwrain.core.NullCheck;

public interface Book
{
    static public class Section
    {
	private int level;
	private String title;
	private String href;

	public Section(int level,
		       String title, String href)
	{
	    NullCheck.notNull(title, "title");
	    NullCheck.notNull(href, "href");
	    this.level = level;
	    this.title = title;
	    this.href = href;
	}

	@Override public String toString()
	{
	    return title;
	}

	public int level() {return level;}
	public String title() {return title;}
	public String href() {return href;}
    }

    Document[] getDocuments();
    Map<URL, Document> getDocumentsWithUrls();
    Document getStartingDocument();
    Document openHref(String href);
    AudioInfo findAudioForId(String ids);
    String findTextForAudio(String audioFileUrl, long msec);
    //Expecting that href is absolute
    Document getDocument(String href);
    Section[] getBookSections();
}
