// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.app.chromite;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.web.chromite.*;

//import static org.luwrain.util.TextUtils.*;

final class MainLayout extends LayoutBase implements TreeArea.ClickHandler
{
    private final App app;
final TreeArea treeArea;
    final SimpleArea attrArea;

    private Elements elements = null;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	treeArea = new TreeArea(treeParams((params)->{
		    params.name = "Страница";
		    params.model = new CachedTreeModel(new Model());
		    params.clickHandler = this;
		}));
	attrArea = new SimpleArea(getControlContext(), "Атрибуты");
	setAreaLayout(AreaLayout.LEFT_RIGHT, treeArea, actions(
				       action("test", "Открыть", new InputEvent(InputEvent.Special.F6), this::actOpen),
				       action("refresh", "Обновить", new InputEvent(InputEvent.Special.F5), this::actRefresh)
					),
		      attrArea, actions());
    }

    @Override public boolean onTreeClick(TreeArea treeArea, Object obj)
    {
	if (obj instanceof Element)
	{
	    final Element el = (Element)obj;
	    attrArea.update(lines->{
		    lines.clear();
		    lines.add("X: " + String.valueOf(el.getX()));
		    		    lines.add("Y: " + String.valueOf(el.getY()));
				    		    		    lines.add("Width: " + String.valueOf(el.getWidth()));
								    				    		    		    lines.add("Height: " + String.valueOf(el.getHeight()));
																    lines.add("");
		});
	    setActiveArea(attrArea);
	    return true;
	}
	return false;
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
