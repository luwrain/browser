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

import java.net.*;

public class Result
{
    public enum Type {
	OK,
	HTTP_ERROR, //code variable gets corresponding value 
	INVALID_HTTP_REDIRECT,
	UNEXPECTED_ERROR,
	INVALID_URL,
	UNRECOGNIZED_FORMAT,
    };

    Type type;
    Document doc = null;
    String format = "";
    String charset = "";
    String origAddr = "";
    String resultAddr = "";
    int code = 0;

    Result(Type type)
    {
	this.type = type;
    }

    Result(Type type, int code)
    {
	this.type = type;
	this.code = code;
    }

    public Type type() { return type; }
    public Document doc() { return doc; }
    public int code() { return code; }
    public String format() { return format; }
    public String charset() { return charset; }
    public String origAddr() { return origAddr;}
    public String resultAddr() { return resultAddr; }
}
