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
    static public final int TEXT_PARA_EACH_LINE = 2;
    static public final int HTML = 3;
    static public final int DOC = 4;
    static public final int DOCX = 5;

    public Document loadFromFile(int format, String fileName)
    {
	NullCheck.notNull(fileName, "fileName");
	switch (format)
	{
	case DOC:
	    return new Doc(fileName).constructDocument();
	case DOCX:
	    return new DocX(fileName).constructDocument();
	case HTML:
	    return new Html(true, fileName).constructDocument();
	default:
	    throw new IllegalArgumentException("unknown format " + format);
	}
    }

    public Document loadFromText(int format, String text)
    {
	NullCheck.notNull(text, "text");
	switch (format)
	{
	case HTML:
	    return new Html(false, text).constructDocument();
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
	case "doc":
	    return DOC;
	case "docx":
	    return DOCX;
	case "html":
	case "htm":
	    return HTML;
	case "txt":
	    return TEXT_PARA_EMPTY_LINE;
	default:
	    return UNRECOGNIZED;
	}
    }
}
