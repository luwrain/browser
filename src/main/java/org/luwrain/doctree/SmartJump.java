/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of the LUWRAIN.

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
	/*
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
	*/
	return false;
    }

    private boolean findTableRow(int level, int rowIndex)
    {
	/*
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
	*/
	return false;
    }

    private boolean findNextParaBegin()
    {
	    if (!it.moveNext())
		return false;
	while (true)
	{
	    if (it.getRowRelIndex() == 0)
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
