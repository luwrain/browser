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

public class Run
{
    String text = "";
    String href = "";
    TextAttr textAttr = new TextAttr();
    ParagraphImpl parentParagraph;
    ExtraInfo extraInfo = null;

    public Run(String text)
    {
	this.text = text;
	NullCheck.notNull(text, "text");
    }

    public Run(String text, String href)
    {
	this.text = text;
	this.href = href;
	NullCheck.notNull(text, "text");
    }

    public Run(String text, String href,
ExtraInfo extraInfo)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(href, "href");
	NullCheck.notNull(extraInfo, "extraInfo");
	this.text = text;
	this.href = href;
	this.extraInfo = extraInfo;
    }

    public String href()
    {
	return href != null?href:"";
    }

    @Override public String toString()
    {
	return text != null?text:"";
    }

    void prepareText()
    {
	final StringBuilder b = new StringBuilder();
	boolean wasSpace = false;
	for(int i = 0;i < text.length();++i)
	{
	    char c = text.charAt(i);
	    if (c == '\n' || c == '\t' || c == ' ')
		c = ' ';
	    if (Character.isISOControl(c))
		continue;
	    if (wasSpace && Character.isSpace(c))
		continue;
	    b.append(c);
	    wasSpace = Character.isSpace(c);
	}
	text = new String(b);
    }

    public ExtraInfo extraInfo()
    {
	return extraInfo;
    }
}
