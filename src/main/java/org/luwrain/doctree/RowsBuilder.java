/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.doctree;

class RowsBuilder
{
    static public Row[] buildRows(RowPart[] parts)
    {
	Row[] rows = new Row[parts[parts.length - 1].absRowNum + 1];
	for(int i = 0;i < rows.length;++i)
	    rows[i] = new Row();
	int current = -1;
	for(int i = 0;i < parts.length;++i)
	{
	    if (parts[i] == null)
		throw new NullPointerException("parts[" + i + "] may not be null");
	    final int rowNum = parts[i].absRowNum;
	    //We are registering only a first part
	    if (rows[rowNum].partsFrom < 0)
		rows[rowNum].partsFrom = i;
	    if (rows[rowNum].partsTo < i + 1)
		rows[rowNum].partsTo = i + 1;
	    }
	return rows;
    }
}
