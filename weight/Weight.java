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

package org.luwrain.browser.weight;

import java.awt.Rectangle;

import org.luwrain.browser.*;

class Weight
{
	public interface Calculator
	{
		long calcWeightFor(WebElement element);
	}
	
	/** calculate weight by count, each leaf have weight 1 */
	public static class ByCount implements Calculator
	{
		@Override public long calcWeightFor(WebElement element)
		{
			return 1;
		}
	}
	
	/** calculate weight by rectangle square */
	public static class BySquare implements Calculator
	{
		@Override public long calcWeightFor(WebElement element)
		{
			final Rectangle r = element.getNode().getRect();
			return r.width * r.height;
		}
	}

	/** calculate weight by rectangle web text length */
	public static class ByTextLen implements Calculator
	{
		@Override public long calcWeightFor(WebElement element)
		{
			BrowserIterator e=element.getNode();
			long len=e.getText().length();
			// if it link, add href length too
			String href=e.getAttribute("href");
			if(href!=null)
				len+=href.length();
			/*
			switch(element.getType())
			{
				case Button:
				case Checkbox:
				case Edit:
				case List:
				case ListElement:
				case Radio:
				case Select:
				case Table:
				case TableCell:
				case TableRow:
				case Text:
				default:
					break;
			}
			*/
			return len;
		}
	}

}
