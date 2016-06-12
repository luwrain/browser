
package org.luwrain.doctree.books;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

abstract public class Base implements Book
{
    protected Path bookPath;
    protected final LinkedList<Note> notes = new LinkedList<Note>();

    @Override public Note[] getNotes()
    {
	return notes.toArray(new Note[notes.size()]);
    }

    @Override public boolean deleteNote(Note note)
    {
	NullCheck.notNull("note", "note");
	final java.util.Iterator<Note>  it = notes.iterator();
	while(it.hasNext())
	{
	    final Note noteIt = it.next();
	    if (noteIt != note)
		continue;
	    notes.remove(it);
	    writeToFile();
	    return true;
	}
	return false;
    }

    @Override public boolean addNote(Note note)
    {
	NullCheck.notNull(note, "note");
	for(Note n: notes)
	    if (n == note)
		return false;
	notes.add(note);
	writeToFile();
	return true;
    }

    protected boolean writeToFile()
    {
	if (bookPath == null)
	{
	    Log.warning("doctree", "no path to book data, skipping writing book basic info");
	    return false;
	}
	return BookSaving.saveNotes(bookPath, notes.toArray(new Note[notes.size()]));
    }
}
