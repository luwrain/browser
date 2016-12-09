/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of LUWRAIN.

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

import java.util.*;
import java.net.*;

import org.luwrain.core.NullCheck;

public interface Book
{
    static public class Section
    {
	public final int level;
	public final String title;
	public final String href;

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
    //    Note[] getNotes();
    //    boolean addNote(Note note);
    //    boolean deleteNote(Note node);
    //    Note createNote(Document doc, int rowIndex);
    //    String getHrefOfNoteDoc(Note note);
}
