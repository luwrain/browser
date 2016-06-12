
package org.luwrain.doctree.books;

import java.io.*;
import java.nio.file.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

import org.luwrain.core.NullCheck;
import org.luwrain.doctree.Book.Note;

public class BookSaving
{
    static public boolean saveNotes(Path path, Note[] notes)
    {
	NullCheck.notNull(path, "path");
	NullCheck.notNullItems(notes, "notes");
	final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;
	try {
	    builder = factory.newDocumentBuilder();
	    Document doc = builder.newDocument();
	    final Element root = doc.createElement("book");
	    doc.appendChild(root);
	    for(Note n: notes)
		root.appendChild(makeNote(doc, n));
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();                             
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    DOMSource source = new DOMSource(doc);
	    final OutputStream os = Files.newOutputStream(path);
	    final StreamResult streamResult = new StreamResult(os);
	    transformer.transform(source, streamResult);
	    os.flush();
	    return true;
	} 
	catch (Exception e)
	{
	    e.printStackTrace();
	    return false;
	}
    }

    static private Node makeNote(Document doc, Note note)
    {
	NullCheck.notNull(doc, "doc");
	NullCheck.notNull(note, "note");
	final Element n = doc.createElement("note");
	n.appendChild(makeText(doc, n, "doc", "" + note.docId()));
	n.appendChild(makeText(doc, n, "pos", "" + note.associatedPos()));
	n.appendChild(makeText(doc, n, "text", note.text()));
	n.appendChild(makeText(doc, n, "uniref", note.uniRef()));
	return n;
    }

    static private Node makeText(Document doc, Element element,
				 String name, String value)
    {
	final Element node = doc.createElement(name);
	node.appendChild(doc.createTextNode(value));
	return node;
    }
}
