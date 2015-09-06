/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015 Roman Volovodov <gr.rPman@gmail.com>

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

public class Row
{
    /** Absolute horizontal position in the area*/
    public int x = 0;

    /** Absolute vertical position in the area*/
    public int y = 0;

    public int partsFrom = -1;
    public int partsTo = -1;

    public String text(RowPart[] parts)
    {
	StringBuilder b = new StringBuilder();
	for(int i = partsFrom;i < partsTo;++i)
	    b.append(parts[i].text());
	return b.toString();
    }
    {
    }

    public boolean hasAssociatedText()
    {
	return partsFrom >= 0 && partsTo >= 0;
    }
}
