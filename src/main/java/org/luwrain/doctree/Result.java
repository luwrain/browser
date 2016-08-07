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

import java.util.*;
import java.net.*;

import org.luwrain.core.NullCheck;

public class Result
{
    public enum Type {
	UNKNOWN_HOST,  //See "host" property
	HTTP_ERROR, //See "httpcode" property
	FETCHING_ERROR, //See "descr" property
	UNDETERMINED_CONTENT_TYPE,
	OK, EXCEPTION,
	//	HTTP_ERROR, //code variable gets corresponding value 
	INVALID_HTTP_REDIRECT,
	UNEXPECTED_ERROR,
	INVALID_URL,
	UNRECOGNIZED_FORMAT,
    };

    Type type = Type.OK;
    Book book = null;
    Document doc = null;
    int startingRowIndex;
    final Properties props = new Properties();

    Result(Type type)
    {
	NullCheck.notNull(type, "type");
	this.type = type;
    }

    Result(Type type, int httpCode)
    {
	NullCheck.notNull(type, "type");
	this.type = type;
	props.setProperty("httpcode", "" + httpCode);
    }

    Result(Type type, String path)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(path, "path");
	this.type = type;
	props.setProperty("path", path);
    }

    public Result(Book book, Document doc)
    {
	NullCheck.notNull(book, "book");
	NullCheck.notNull(doc, "doc");
	this.type = Type.OK;
	this.book = book;
	this.doc = doc;
    }

    public void clearDoc()
    {
	book = null;
	doc = null;
    }

    public void setStartingRowIndex(int value)
    {
	startingRowIndex = value;
    }

    public String getProperty(String propName)
    {
	NullCheck.notNull(propName, "propName");
	final String res = props.getProperty(propName);
	return res != null?res:"";
    }

    public void setProperty(String propName, String value)
    {
	NullCheck.notNull(propName, "propName");
	NullCheck.notNull(value, "value");
	props.setProperty(propName, value);
    }

    public Type type() { return type; }
    public Document doc() { return doc; }
    public Book book() {return book;}

}
