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

package org.luwrain.browser.docbuilder;

import java.util.*;

import org.luwrain.core.*;

class Cleaning
{
    //FIXME: make it more wisely, do not remove interactive elements with onclick and soon 
    static int clean(Prenode node)
    {
	int count=0;
	if(node.children.isEmpty())
	{
	    String text=node.browserIt.getText();
	    if(text.isEmpty())
	    { // remove empty text element without child
		String tagName = node.browserIt.getHtmlTagName().toLowerCase();
		switch(tagName)
		{
		    /*
		    // do not remove
		    case "img":
		    case "option": // html SELECT option's nodes
		    case "td": // table cells
		    case "tr": // table rows
		    case "th": // table cells
		    case "li": // element list
		    case "a":
		    case "input":
		    case "button":
		    return 0;
		    */
		    // remove
		case "span":
		case "div":
		case "li":
		    // remove it from parent
		    if(node.parent!=null)
		    {
			node.toDelete=true;
			count++;
		    }
		break;
		}
	    }
	} else
	    for(Prenode child:node.children)
		count += clean(child);
	Iterator<Prenode> i=node.children.iterator();
	while (i.hasNext())
	{
	    Prenode child=i.next();
	    if(child.toDelete)
		i.remove();
	}
	return count;
    }
    }
