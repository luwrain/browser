/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.controls.web;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.controls.web.WebArea.Callback.MessageType;

final class View
{
    final Model model;

	View(Model model)
    {
	NullCheck.notNull(model, "model");
	this.model = model;
    }

    boolean isEmpty()
    {
	return model.containers.length == 0;
    }

    Iterator createIterator()
    {
	if (isEmpty())
	    return null;
	return new Iterator(this, 0);
    }


    static final class Iterator
    {
	private final View view;
	private int pos = 0;
	private Container container = null;

	Iterator(View view, int pos)
	{
	    NullCheck.notNull(view, "view");
	    if (pos < 0)
		throw new IllegalArgumentException("pos (" + pos + ") may not be negative");
	    this.view = view;
	    this.pos = pos;
	    this.container = view.model.getContainer(this.pos);
	}

Container.Type getType()
	{
	    return container.type;
	}

	int getLineCount()
	{
	    return container.getRowCount();
	}

	WebObject[] getRow(int index)
	{
	    return container.getRow(index);
	}

	boolean isLastRow(int index)
	{
	    return container.getRowCount() > 0 && index + 1 == container.getRowCount();
	}

		boolean movePrev()
	{
	    if (pos == 0)
		return false;
	    --pos;
container = view.model.getContainer(pos);
	    return true;
	}

	boolean moveNext()
	{
	    if (pos + 1 >= view.model.getContainerCount())
		return false;
	    ++pos;
container = view.model.getContainer(pos);
	    return true;
	}
    }
}
