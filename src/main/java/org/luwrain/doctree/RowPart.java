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

class RowPart
{
    Run run;

    /** Starting position in the text of the corresponding run*/
    int posFrom = 0;

    /** Ending position in the text of the corresponding run*/
    int posTo = 0;

    /** Absolute row index in a document*/
    int absRowNum = 0;

    /** Index in the corresponding paragraph*/
    int relRowNum = 0;

    String text()
    {
	if (run == null)
	    throw new NullPointerException("run may not be null");
	return run.text.substring(posFrom, posTo);
    }

    TextAttr textAttr()
    {
	if (run == null)
	    throw new NullPointerException("node may not be null");
	return run.textAttr;
    }

    String href()
    {
	if (run == null)
	    throw new NullPointerException("node may not be null");
	return run.href;
    }

}
