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

public interface Node
{
    public enum Type {
	ROOT,
	SECTION,
	PARAGRAPH,
	TABLE,
	TABLE_ROW,
	TABLE_CELL,
	UNORDERED_LIST,
	ORDERED_LIST,
	LIST_ITEM,
	SMIL_PARA // daisy dtbook: Parallel time grouping in which multiple elements (e.g., text, audio, and image) play back simultaneously
    };

    /*
    static public final int ROOT = 1;
    static public final int SECTION = 2;
    static public final int PARAGRAPH = 3;
    static public final int TABLE = 4;
    static public final int TABLE_ROW = 5;
    static public final int TABLE_CELL = 6;
    static public final int UNORDERED_LIST = 7;
    static public final int ORDERED_LIST = 8;
    static public final int LIST_ITEM = 9;
    static public final int SMIL_PAR = 10; // daisy dtbook: Parallel time grouping in which multiple elements (e.g., text, audio, and image) play back simultaneously.
    */

    int getNodeX();
    int getNodeY();
    int getNodeWidth();
    int getNodeHeight();
    String getId();
}
