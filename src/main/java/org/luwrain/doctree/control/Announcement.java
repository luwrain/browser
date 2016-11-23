
package org.luwrain.doctree.control;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.doctree.*;
import org.luwrain.doctree.view.Iterator;
import org.luwrain.doctree.view.Layout;

public class Announcement
{
    protected final ControlEnvironment environment;
    protected final Strings strings;

    public Announcement(ControlEnvironment environment, Strings strings)
    {
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(strings, "strings");
	this.environment = environment;
	this.strings = strings;
    }

    public void announce(Iterator it, boolean briefIntroduction)
    {
	NullCheck.notNull(it, "it");
	if (it.noContent() || it.isEmptyRow())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return;
	}
	if (it.getNode() == null)
	{
	environment.say(it.getText());
	return;
	}

	if (it.isTitleRow())
	{
	    onTitle(it);
	    return;
	}
	announceText(it);
    }

    protected void onTitle(Iterator it)
    {
	final Node node = it.getNode();
	if (node.getType() == Node.Type.ORDERED_LIST)
	    onOrderedList(it); else
	    if (node.getType() == Node.Type.UNORDERED_LIST)
		onUnorderedList(it); else
		if (node instanceof ListItem)
		    onListItem(it); else
		    if (node instanceof TableCell)
			onTableCell(it); else
		    {
			environment.say("title");
		    }
    }

    private void onOrderedList(Iterator it)
    {
	environment.say("Нумерованный список");
    }

    private void onUnorderedList(Iterator it)
    {
	environment.say("Ненумерованный список");
    }

    private void onListItem(Iterator it)
    {
	//	final Node node = it.getTitleParentNode();
	environment.say("Элемент списка ", Sounds.LIST_ITEM);
    }

    private void onTableCell(Iterator it)
    {
	//it.getTitleParentNode();
	final TableCell cell = (TableCell)it.getNode();
	final int rowIndex = cell.getRowIndex();
	final int colIndex = cell.getColIndex();
	if (rowIndex == 0 && colIndex == 0)
	{
	    environment.say("Начало таблицы", Sounds.TABLE_CELL);
	    return;
}
	environment.say("Строка " + (rowIndex + 1) + ", столбец " + (colIndex + 1), Sounds.TABLE_CELL);
    }

    private void announceText(Iterator it)
    {
	NullCheck.notNull(it, "it");
	//Checking if there is nothing to say
	if (it.getText().trim().isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return;
	}
	//Checking should we use any specific sound
final Sounds sound;
	if (it.getIndexInParagraph() == 0 && it.getNode() != null)
	{
		switch(it.getNode().getType())
		{
		case SECTION:
		    sound = Sounds.DOC_SECTION;
		    break;
		case LIST_ITEM:
		    sound = Sounds.LIST_ITEM;
		    break;
		default:
		    sound = null;
		}
	} else
	    sound = null;
	//Speaking with sound if we have chosen any
	if (sound != null)
	{
	    environment.say(it.getText(), sound);
	    return;
	}
	//Speaking with paragraph sound if it is a first row

	if (it.getIndexInParagraph() == 0)
	    environment.say(it.getText(), Sounds.PARAGRAPH); else
		environment.say(it.getText());
    }
}
