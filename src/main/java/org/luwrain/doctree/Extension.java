/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of LUWRAIN.

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
import org.luwrain.core.extensions.*;
import org.luwrain.doctree.loading.UrlLoader;

public class Extension extends EmptyExtension
{
    static public final String FORMATS_OBJECT_NAME = "luwrain.doctree.formats";
    @Override public SharedObject[] getSharedObjects(Luwrain luwrain)
    {
	return new SharedObject[]{

	    new SharedObject(){
		@Override public String getName()
		{
		    return FORMATS_OBJECT_NAME;
		}
		@Override public Object getSharedObject()
		{
		    return new String[]{
			UrlLoader.CONTENT_TYPE_TXT + ";parastyle=" + UrlLoader.PARA_STYLE_EMPTY_LINES,
			UrlLoader.CONTENT_TYPE_TXT + ";parastyle=" + UrlLoader.PARA_STYLE_INDENT,
			UrlLoader.CONTENT_TYPE_TXT + ";parastyle=" + UrlLoader.PARA_STYLE_EACH_LINE,
			UrlLoader.CONTENT_TYPE_DOC,
			UrlLoader.CONTENT_TYPE_DOCX,
			UrlLoader.CONTENT_TYPE_HTML,

			UrlLoader.CONTENT_TYPE_FB2,


		    };
		}
		},

		    };
	}
}
