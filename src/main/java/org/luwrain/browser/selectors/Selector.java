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

import org.luwrain.core.*;
import org.luwrain.browser.*;

public abstract class Selector
{
abstract public boolean suits(BrowserIterator list);

    /** Moves the iterator to the first element, approved by the {@code suits()} 
     * method. If there is no such element, the method restored 
     * the original state of the iterator.
     *
     * @param list The iterator to move
     * @return True if the iterator gets necessary position, false otherwise
     */
    //    boolean moveFirst(ElementIterator list);

    /** Moves the iterator to the next element, approved by the {@code suits()} 
     * method. If there is no such element, the method restored 
     * the original state of the iterator.
     *
     * @param list The iterator to move
     * @return True if the iterator gets necessary position, false otherwise
     */
    //    boolean moveNext(ElementIterator list);

    /** Moves the iterator to the previous element, approved by the {@code suits()} 
     * method. If there is no such element, the method restored 
     * the original state of the iterator.
     *
     * @param list The iterator to move
     * @return True if the iterator gets necessary position, false otherwise
     */
    //    boolean movePrev(ElementIterator list);

    //    boolean moveToPos(ElementIterator list, int pos);



    /** Moves the iterator to the first element, approved by the {@code suits()} 
     * method. If there is no such element, the method restored 
     * the original state of the iterator.
     *
     * @param it The iterator to move
     * @return True if the iterator gets necessary position, false otherwise
     */
public boolean moveFirst(BrowserIterator it)
    {
	final int origState = it.getPos();
	final int count = it.getBrowser().numElements();
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
     * method. If there is no such element, the method restored 
     * the original state of the iterator.
     *
     * @param it The iterator to move
     * @return True if the iterator gets necessary position, false otherwise
     */
public boolean moveNext(BrowserIterator it)
    {
	final int origState = it.getPos();
	final int count = it.getBrowser().numElements();
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
     * method. If there is no such element, the method restored 
     * the original state of the iterator.
     *
     * @param it The iterator to move
     * @return True if the iterator gets necessary position, false otherwise
     */
public boolean movePrev(BrowserIterator it)
    {
	final int origState = it.getPos();
	it.setPos(it.getPos()-1);
	while(it.getPos() >= 0 && !suits(it)) 
		it.setPos(it.getPos()-1);
	if(it.getPos()<0)
	{
		it.setPos(origState);
	    return false;
	}
	return true;
    }

public boolean moveToPos(BrowserIterator it, int pos)
    {
    	if(it.getPos() == pos)
    		return true;
    	else if(it.getPos() < pos)
	    {
		while(moveNext(it)) 
		    if(pos == it.getPos()) 
			return true;
		return false;
	    } else
	    {
		while(movePrev(it)) 
		    if(pos == it.getPos()) 
			return true;
		return false;
	    }
    }
}
