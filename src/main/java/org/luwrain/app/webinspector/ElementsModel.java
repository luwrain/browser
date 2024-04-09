/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.webinspector;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.web.*;

final class ElementsModel implements TreeListArea.Model<WebObject>
{
    final App app;
    ElementsModel(App app) { this.app = app; }

    @Override public boolean getItems(WebObject obj, TreeListArea.Collector<WebObject> collector)
    {
	collector.collect(Arrays.asList(obj.getChildren()));
	return true;
    }

    @Override public WebObject getRoot()
    {
	if (app.getTree() == null)
	    return null;
	return app.getTree().getBody();
    }

    @Override public boolean isLeaf(WebObject obj)
    {
	return !obj.hasChildren();
    }
}
