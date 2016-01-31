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

package org.luwrain.doctree.dtbook;

import org.luwrain.doctree.Document;

public class DTBookFile
{
    public String id;
    // relative file name
    public String name;
    // mime media-type of file
    public String type;

    public DTBookFile(String id,String name,String type)
    {
	this.id=id;
	this.name=name;
	this.type=type;
    }

    // * files was loaded
    // application/smil
    // application/x-dtbncx+xml
    // text/xml
    // application/x-dtbook+xml
    // * was not loaded
    // application/x-dtbresource+xml
    // audio/mpeg
    // text/css
    // ..
    // parsed version of document, if it needed for DAISY reading
    public Document document=null;
}
