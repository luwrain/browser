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

package org.luwrain.doctree.books;

import java.net.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.NullCheck;
import org.luwrain.core.Log;
import org.luwrain.doctree.*;
import org.luwrain.util.*;
import org.luwrain.doctree.loading.*;

public class BookFactory
{
    static public Book initDaisy2(Document nccDoc, UrlLoaderFactory urlLoaderFactory)
    {
	NullCheck.notNull(nccDoc, "nccDoc");
	NullCheck.notNull(urlLoaderFactory, "urlLoaderFactory");
	final Daisy2 book = new Daisy2(urlLoaderFactory);
	book.init(nccDoc);
	return book;
    }
}
