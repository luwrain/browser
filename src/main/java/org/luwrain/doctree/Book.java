
package org.luwrain.doctree;

import java.util.*;
import java.net.*;

import org.luwrain.core.NullCheck;

public interface Book
{
    static public class Section
    {
protected int level;
	protected String title;
	protected String href;

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

    static public class Note
    {
	protected String docId;
	protected int associatedPos;
	protected String text;
	protected String uniRef;

	public Note(String docId, int associatedPos)
	{
	    NullCheck.notNull(docId, "docId");
	    this.docId = docId;
	    this.associatedPos = associatedPos;
	    this.text = "";
	    this.uniRef = "";
	}

	public void setText(String value)
	{
	    NullCheck.notNull(value, "value");
	    this.text = value;
	}

	public void setUniRef(String value)
	{
	    NullCheck.notNull(value, "value");
	    this.uniRef = value;
	}

	@Override public String toString()
	{
	    return text;
	}

	public String docId() {return docId;}
	public int associatedPos() {return associatedPos;}
	public String text() {return text;}
	public String uniRef() {return uniRef;}
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
    Note[] getNotes();
    boolean addNote(Note note);
    boolean deleteNote(Note node);
    Note createNote(Document doc, int rowIndex);
    String getHrefOfNoteDoc(Note note);
}
