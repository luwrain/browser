/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.browser.selectors;

import org.luwrain.browser.*;

/** The selector for iteration over all elements on the page*/
public class AllNodesSelector extends Selector
{
    /** Consider only visible elements of the page*/
    protected boolean visible;

    public AllNodesSelector(boolean visible)
    {
	this.visible = visible;
    }

public boolean isVisible()
    {
	return visible;	
    }

public void setVisible(boolean visible)	
    {
	this.visible=visible;
    }

    // return true if current element is visible
public boolean checkVisible(BrowserIterator it)
    {
	return true;
    }

    /** return true if current element suits the condition of this selector.*/
    @Override public boolean suits(BrowserIterator it)
    {
	if(visible&&!checkVisible(it))
	    return false;
	return true;
    }
}
