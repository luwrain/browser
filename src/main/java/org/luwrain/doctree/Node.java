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

/**
 * The interface for structural items in a document. It is just some
 * common methods for access to {@link NodeImpl} class and constants for
 * designation of various node types. See {@NodeImpl} for further
 * information about nodes and their purposes.
 *
 * @see Document
 */
public interface Node
{
    public enum Type {
	ROOT, SECTION, PARAGRAPH,
	TABLE, TABLE_ROW,TABLE_CELL, 
	UNORDERED_LIST, ORDERED_LIST, LIST_ITEM,
	SMIL_PARAGRAPH // daisy dtbook: Parallel time grouping in which multiple elements (e.g., text, audio, and image) play back simultaneously
    };

    int getNodeX();
    int getNodeY();
    int getNodeWidth();
    int getNodeHeight();
    String getId();
}
