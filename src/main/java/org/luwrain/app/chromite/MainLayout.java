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

package org.luwrain.app.chromite;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.web.chromite.*;

//import static org.luwrain.util.TextUtils.*;

final class MainLayout extends LayoutBase
{
    private final App app;
    private final TreeArea treeArea;

    private Elements elements = null;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	treeArea = new TreeArea(treeParams((params)->{
		    params.name = "Страница";
		    params.model = new CachedTreeModel(new Model());
		}));
	setAreaLayout(treeArea, actions(
				       action("test", "Открыть", new InputEvent(InputEvent.Special.F6), this::actOpen),
				       action("refresh", "Обновить", new InputEvent(InputEvent.Special.F5), this::actRefresh)
				       ));
    }

    private boolean actOpen()
    {
	final String url = app.getConv().openUrl("https://");
	if (url == null || url.trim().isEmpty())
	    return true;
	app.getChromite().navigate(url.trim());
	return true;
    }

    private boolean actRefresh()
    {
	this.elements = app.getChromite().getPage();
	treeArea.refresh();
		return true;
    }

    private final class Model implements CachedTreeModelSource
    {
	    @Override public Object getRoot()
	{
	    if (elements == null)
		return "Нет содержимого";
	    return elements;
	}
    @Override public Object[] getChildObjs(Object obj)
	{
	    if (obj instanceof Elements)
		return ((Elements)obj).getChildren();
	    	    if (obj instanceof Element)
		return ((Element)obj).getChildren();
		    return new Object[0];
	    	    }
	}
}
