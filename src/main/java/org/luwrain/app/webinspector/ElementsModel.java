// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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
	return app.getTree().root;
    }

    @Override public boolean isLeaf(WebObject obj)
    {
	return !obj.hasChildren();
    }
}
