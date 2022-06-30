/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.web.chromite;

import java.util.*;

public final class Element
{
private String
	tagName = null;

private List<Element> children = null;

public String getTagName() { return tagName != null?tagName:""; }
    public Element[] getChildren() { return children != null?children.toArray(new Element[children.size()]):new Element[0]; }
    @Override public String toString() { return getTagName(); }
}
