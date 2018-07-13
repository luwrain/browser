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

//LWR_API 1.0

package org.luwrain.browser.selectors;

import org.luwrain.core.*;
import org.luwrain.browser.*;


/**
 * An abstract selector for manipulation of {@link BrowserIterator}
 * states. During processing of the web-page, several types of filters
 * could be needed (for example, to filter only visible elements).  This
 * class implements general navigation procedures, such as jumping to the
 * next or previous element, calling the abstract method {@code suits()}
 * forexamining if the particular element is suitable or not.
 */
public abstract class Selector
{
    abstract public boolean suits(BrowserIterator it);

    /** Moves the iterator to the first element, approved by the {@code suits()} 
     * method. If there is no such element, the method restores 
     * the iterator's original state.
     *
     * @param it The iterator to move
     * @return True if the iterator gets the necessary position, false otherwise
     */
    public boolean moveFirst(BrowserIterator it)
    {
	final int origState = it.getPos();
	final int count = it.getBrowser().numElements();
	if (count == 0)
	    return false;
	it.setPos(0);
	while(it.getPos() < count && !suits(it)) 
	    it.setPos(it.getPos()+1);
	if(it.getPos() >= count)
	{
	    it.setPos(origState);
	    return false;
	}
	return true;
    }

    /** Moves the iterator to the next element, approved by the {@code suits()} 
     * method. If there is no such element, the method restores 
     * the iterator's original state.
     *
     * @param it The iterator to move
     * @return True if the iterator gets the necessary position, false otherwise
     */
    public boolean moveNext(BrowserIterator it)
    {
	final int origState = it.getPos();
	final int count = it.getBrowser().numElements();
	if (count == 0 || it.getPos() + 1 >= count)
	    return false;
	it.setPos(it.getPos()+1);
	while(it.getPos() < count && !suits(it)) 
	    it.setPos(it.getPos()+1);
	if(it.getPos() >= count)
	{
	    it.setPos(origState);
	    return false;
	}
	return true;
    }

    /** Moves the iterator to the previous element, approved by the {@code suits()} 
     * method. If there is no such element, the method restores 
     * the iterator's original state.
     *
     * @param it The iterator to move
     * @return True if the iterator gets the necessary position, false otherwise
     */
    public boolean movePrev(BrowserIterator it)
    {
	final int origState = it.getPos();
	final int count = it.getBrowser().numElements();
	if (count == 0 || it.getPos() == 0)
	    return false;
	it.setPos(it.getPos()-1);
	while(it.getPos() >= 0 && !suits(it)) 
	    it.setPos(it.getPos() - 1);
	if(it.getPos()<0)
	{
	    it.setPos(origState);
	    return false;
	}
	return true;
    }
}
