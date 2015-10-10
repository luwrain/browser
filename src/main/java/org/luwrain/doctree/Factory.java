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

import org.luwrain.core.*;
import org.luwrain.doctree.filters.*;

public class Factory
{
    static public final int UNRECOGNIZED = 0;
    static public final int TEXT_PARA_EMPTY_LINE = 1;
    static public final int TEXT_PARA_INDENT = 2;
    static public final int TEXT_PARA_EACH_LINE = 3;
    static public final int HTML = 4;
    static public final int DOC = 5;
    static public final int DOCX = 6;

    static public Document loadFromFile(int format, String fileName, int width, String encoding)
    {
	NullCheck.notNull(fileName, "fileName");
	switch (format)
	{
	case TEXT_PARA_INDENT:
	    return new TxtParaIndent(fileName).constructDocument(encoding, width);
	case TEXT_PARA_EMPTY_LINE:
	    return new TxtParaEmptyLine(fileName).constructDocument(encoding, width);
	case TEXT_PARA_EACH_LINE:
	    return new TxtParaEachLine(fileName).constructDocument(encoding, width);
	case DOC:
	    return new Doc(fileName).constructDocument(width);
	case DOCX:
	    return new DocX(fileName).constructDocument(width);
	case HTML:
	    return new Html(true, fileName).constructDocument(width);
	default:
	    throw new IllegalArgumentException("unknown format " + format);
	}
    }

    static public Document loadFromText(int format, String text, int width)
    {
	NullCheck.notNull(text, "text");
	switch (format)
	{
	case HTML:
	    return new Html(false, text).constructDocument(width);
	default:
	    throw new IllegalArgumentException("unknown format " + format);
	}
    }

    static public int suggestFormat(String path)
    {
	NullCheck.notNull(path, "path");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path may not be empty");
	String ext = FileTypes.getExtension(path);
	if (ext == null || path.isEmpty())
	    return UNRECOGNIZED;
	ext = ext.toLowerCase();
	switch(ext)
	{
	case "txt":
	    return TEXT_PARA_INDENT;
	case "doc":
	    return DOC;
	case "docx":
	    return DOCX;
	case "html":
	case "htm":
	    return HTML;
	default:
	    return UNRECOGNIZED;
	}
    }
}
