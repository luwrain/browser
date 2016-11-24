
package org.luwrain.doctree.control;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.doctree.view.Iterator;

class Jump
{
    final Iterator it;
    final int pos;
    final String text;
    final Sounds sound;

    Jump()
    {
	this.it = null;
	this.pos = 0;
	this.text = "";
	this.sound = null;
    }

    Jump(Iterator it, int pos,
	 String text, Sounds sound)
    {
	NullCheck.notNull(it, "it");
	NullCheck.notNull(text, "text");
	this.it = it;
	this.pos = pos;
	this.text = text;
	this.sound = sound;
    }

    boolean isEmpty()
    {
	return it == null;
    }

    void announce(ControlEnvironment environment)
    {
	NullCheck.notNull(environment, "environment");
	if (isEmpty())
	{
	    environment.playSound(Sounds.BLOCKED);
	    return;
	}
	if (sound != null)
	    environment.say(text, sound); else
	    environment.say(text);
    }

    static Jump nextParagraph(Iterator fromIt, int fromPos)
    {
	NullCheck.notNull(fromIt, "fromIt");
	if (fromIt.isEmptyRow())
	    return new Jump();
	//Looking for the beginning of the next paragraph
	final Iterator it = (Iterator)fromIt.clone();
	if (!it.moveNext())
	    return new Jump();
		do {
		    if (it.isParagraphBeginning() && !it.isTitleRow())
		return new Jump(it, 0, getParagraphText(it), chooseSound(it));
		} while (it.moveNext());
	return new Jump();
    }

    static private String getParagraphText(Iterator fromIt)
    {
	NullCheck.notNull(fromIt, "fromIt");
	if (fromIt.isEmptyRow())
	    return "";
	final Iterator it = (Iterator)fromIt.clone();
	final StringBuilder b = new StringBuilder();
	do {
	    b.append(it.getText() + " ");
	} while(it.moveNext() && !it.isParagraphBeginning());
	return new String(b).trim();
    }

    static private Sounds chooseSound(Iterator it)
    {
	NullCheck.notNull(it, "it");
	if (it.isEmptyRow() || !it.isParagraphBeginning())
	    return null;
	switch(it.getNode().getType())
	{
	case LIST_ITEM:
	    return Sounds.LIST_ITEM;
	case SECTION:
	    return Sounds.DOC_SECTION;
	default:
	    return Sounds.PARAGRAPH;
	}
    }
}
