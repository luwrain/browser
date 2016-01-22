
package org.luwrain.doctree;

import org.luwrain.core.NullCheck;

class SmartJump
{
    Iterator it;
    Iterator speakToIt;
    private boolean calculateSpeakToIt;

    SmartJump(Iterator it, boolean calculateSpeakToIt)
    {
	this.it = it;
	this.calculateSpeakToIt = calculateSpeakToIt;
	NullCheck.notNull(it, "it");
    }

    boolean jumpForward()
    {
	if (it.isContainerTableCell())
	{
	    final TableCell cell = it.getTableCell();
	    final Table table = cell.getTable();
	    final int row = cell.getRowIndex();
	    if (row + 1 < table.getRowCount() &&
		findTableRow(table.getTableLevel(), row + 1))
		return finish();
	}
	if (findNextParaBegin())
	    return finish();
	return false;
    }

    private boolean findTableRow(int level, int rowIndex)
    {
	while (true)
	{
	    if (!it.isContainerTableCell())
		return false;
	    final TableCell cell = it.getTableCell();
	    final Table table = cell.getTable();
	    if (table.getTableLevel() == level && cell.getRowIndex() >= rowIndex)
		return true;
	    if (!it.moveNext())
		return false;
	}
    }

    private boolean findNextParaBegin()
    {
	    if (!it.moveNext())
		return false;
	while (true)
	{
	    if (it.getCurrentRowRelIndex() == 0)
		return true;
	    if (!it.moveNext())
		return false;
	}
    }

    private boolean finish()
    {
	if (!calculateSpeakToIt)
	    return true;
	speakToIt = (Iterator)it.clone();
	final SmartJump jump = new SmartJump(speakToIt, false);
	return jump.jumpForward();
    }
}
