
package org.luwrain.doctree.view;

import java.net.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

public class View
{
    static public final String DEFAULT_ITERATOR_INDEX_PROPERTY = "defaultiteratorindex";

    protected final Document doc;
    protected final Node root;
    protected final Layout layout;
    protected Paragraph[] paragraphs; //Only paragraphs which appear in document, no paragraphs without row parts
    protected RowPart[] rowParts;
    protected Row[] rows;

    public View(Document doc)
    {
	NullCheck.notNull(doc, "doc");
	this.doc = doc;
	this.root = doc.getRoot();
	this.layout = new Layout(doc);
    }

    public void build(int width)
    {
	Layout.calcWidth(root, width);
	final RowPartsBuilder rowPartsBuilder = new RowPartsBuilder();
	rowPartsBuilder.onNode(root);
	rowParts = rowPartsBuilder.getRowParts();
	NullCheck.notNullItems(rowParts, "rowParts");
	Log.debug("doctree", "" + rowParts.length + " row parts prepared");
	if (rowParts.length <= 0)
	    return;
	paragraphs = rowPartsBuilder.getParagraphs();
	//	    Log.debug("doctree", "" + paragraphs.length + " paragraphs prepared");
	Layout.calcHeight(root);
	Layout.calcAbsRowNums(rowParts);
	Layout.calcPosition(root);
	rows = Layout.buildRows(rowParts);
	Log.debug("doctree", "" + rows.length + " rows prepared");
	layout.calc();
	setDefaultIteratorIndex();
    }

    public org.luwrain.doctree.Iterator getIterator()
    {
	return new org.luwrain.doctree.Iterator(doc);
    }

    public org.luwrain.doctree.Iterator getIterator(int startingIndex)
    {
	return new org.luwrain.doctree.Iterator(doc, startingIndex);
    }


    public int getLineCount()
    {
	return layout.getLineCount();
    }

    public String getLine(int index)
    {
	return layout.getLine(index);
    }

    public Paragraph[] getParagraphs() { return paragraphs; }
    public Row[] getRows() { return rows; }
    public RowPart[] getRowParts() { return rowParts; }

    private void setDefaultIteratorIndex()
    {
	final String id = doc.getProperty("startingref");
		if (id.isEmpty())
	    return;
	Log.debug("doctree", "preparing default iterator index for " + id);
	final org.luwrain.doctree.Iterator it = getIterator();
	while (it.canMoveNext())
	{
	    if (!it.isEmptyRow())
	    {
		final ExtraInfo data = it.getNode().extraInfo;
		if (data != null && data.hasIdInChain(id))
		    break;
		final Run[] runs = it.getRunsOnRow();
		Run foundRun = null;
		for(Run r: runs)
		    if (r instanceof TextRun)
		    {
			final TextRun textRun = (TextRun)r;
			if (textRun.extraInfo.hasIdInChain(id))
			    foundRun = textRun;
		    }
		if (foundRun != null)
		    break;
	    }
	    it.moveNext();
	}
	if (!it.canMoveNext())//FIXME:
	{
	    Log.debug("doctree", "no iterator position found for " + id);
	    doc.setProperty(DEFAULT_ITERATOR_INDEX_PROPERTY, "");
	    return;
	}
	doc.setProperty("defaultiteratorindex", "" + it.getRowAbsIndex());
	Log.debug("doctree", "default iterator index set to " + it.getRowAbsIndex());
    }
}
