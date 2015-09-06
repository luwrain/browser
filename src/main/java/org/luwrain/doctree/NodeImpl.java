/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.doctree;

public class NodeImpl implements Node
{
    private static final int MIN_TABLE_CELL_WIDTH = 8;

    public int type;
    public NodeImpl[] subnodes = new NodeImpl[0];
    NodeImpl parentNode;

    /** The exact meaning of a level depends on the node type*/
    int level = 0;

    /**Absolute horizontal position in the area*/
    int x = -1;

    /**Absolute vertical position in the area*/
    int y = -1;
    int width = 0;
    int height = 0;

    boolean empty = false;

    NodeImpl(int type)
    {
	this.type = type;
    }

    @Override public int getNodeX()
    {
	return x;
    }

    @Override public int getNodeY()
    {
	return y;
    }

    @Override public int getNodeWidth()
    {
	return width;
    }

    @Override public int getNodeHeight()
    {
	return height;
    }

    //Launched before everything, RowPartsBuilder goes next
    public void calcWidth(int recommended)
    {
	width = 0;
	switch (type)
	{
	case Node.TABLE_CELL:
	    if (subnodes == null || subnodes.length < 1)
	    {
		width = recommended >= MIN_TABLE_CELL_WIDTH?recommended:MIN_TABLE_CELL_WIDTH;
		break;
	    }
	    for(NodeImpl n: subnodes)
	    {
		n.calcWidth(recommended >= MIN_TABLE_CELL_WIDTH?recommended:MIN_TABLE_CELL_WIDTH);
		if (width < n.width)
		    width = n.width;
	    }
	    break;
	case Node.TABLE_ROW:
	    for(NodeImpl n: subnodes)
		n.calcWidth((recommended - subnodes.length + 1) >= subnodes.length?(recommended - subnodes.length + 1) / subnodes.length:1);
	    for(NodeImpl n: subnodes)
		width += n.width;
	    width += (subnodes.length - 1);//One additional empty column after each cell;
	    if (width < recommended)
		width = recommended;
	    break;
	case Node.PARAGRAPH:
	    width = recommended;
	    break;
	case Node.ROOT:
	case Node.SECTION:
	case Node.UNORDERED_LIST:
	case Node.ORDERED_LIST:
	case Node.LIST_ITEM:
	case Node.TABLE:
	    if (subnodes == null || subnodes.length < 1)
	    {
		width = recommended;
		break;
	    }
	    for(NodeImpl n: subnodes)
	    {
		n.calcWidth(recommended);
		if (width < n.width)
		    width = n.width;
	    }
	    break;
	default:
	    throw new IllegalArgumentException("unknown node type " + type);
	}
    }

    //Launched after RowPartsBuilder;
    public void calcHeight()
    {
	for(NodeImpl n: subnodes)
	    n.calcHeight();
	height = 0;
	switch (type)
	{
	case Node.TABLE_ROW:
	    for(NodeImpl n: subnodes)
		if (height < n.height)
		    height = n.height;
	    break;
	case Node.ROOT:
	case Node.SECTION:
	case Node.TABLE:
	case Node.TABLE_CELL:
	case Node.UNORDERED_LIST:
	case Node.ORDERED_LIST:
	case Node.LIST_ITEM:
	    for(NodeImpl n: subnodes)
		height += n.height;
	    break;
	default:
	    throw new IllegalArgumentException("unknown node type " + type);
	}
    }

    //Launched after calcHeight;
    public void calcPosition()
    {
	int offset = 0;
	switch (type)
	{
	case Node.TABLE_ROW:
	    offset = 0;
	    for(NodeImpl n: subnodes)
	    {
		n.x = x + offset;
		offset += (n.width + 1);
		n.y = y;
		n.calcPosition();
	    }
	    break;
	case Node.PARAGRAPH:
	    break;
	case Node.ROOT:
	    x = 0;
	    y = 0;
	case Node.SECTION:
	case Node.TABLE:
	case Node.TABLE_CELL:
	case Node.UNORDERED_LIST:
	case Node.ORDERED_LIST:
	case Node.LIST_ITEM:
	    for(NodeImpl n: subnodes)
	    {
		n.x = x;
		n.y = y + offset;
		offset += (n.height + (n.shouldHaveExtraLine()?1:0));
		n.calcPosition();
	    }
	    break;
	default:
	    throw new IllegalArgumentException("unknown node type " + type);
	}
    }

    public void commit()
    {
	if (type == Node.ROOT)
	    parentNode = null;
	if (subnodes == null)
	    return;
	for(NodeImpl n: subnodes)
	{
	    n.parentNode = this;
	    n.commit();
	}
    }

    public void setEmptyMark()
    {
	empty = true;
	if (subnodes == null || subnodes.length < 1)
	    return;
	for(NodeImpl n:subnodes)
	{
	    n.setEmptyMark();
	    if (!n.empty)
		empty = false;
	}
    }

    public void removeEmpty()
    {
	if (subnodes == null)
	    return;
	int k = 0;
	for(int i = 0;i < subnodes.length;++i)
	    if (subnodes[i].empty)
		++k; else
		subnodes[i - k] = subnodes[i];
	if (k > 0)
	{
	    final int count = subnodes.length - k;
	    NodeImpl[] newNodes = new NodeImpl[count];
	    for(int i = 0;i < count;++i)
		newNodes[i] = subnodes[i];
	    subnodes = newNodes;
	}
	for(NodeImpl n: subnodes)
	    n.removeEmpty();
    }

    @Override public String toString()
    {
	if (subnodes == null)
	    return "";
	String s = "";
	for(NodeImpl n: subnodes)
	{
	    if (!s.isEmpty())
		s += " ";
	    s += n.toString();
	}
	return s;
    }

    public void saveStatistics(Statistics stat)
    {
	++stat.numNodes;
	if (subnodes != null)
	    for(NodeImpl n: subnodes)
		n.saveStatistics(stat);
    }

    /** @return -1 if there is no a parent node or there is a consistency error*/
    public int getParentType()
    {
	return parentNode != null && parentNode.subnodes != null?parentNode.type:-1;
    }


    /** @return -1 if there is no a parent node or there is a consistency error*/
    public int getParentSubnodeCount()
    {
	return parentNode != null && parentNode.subnodes != null?parentNode.subnodes.length:-1;
    }

    /** @return -1 if it is impossible to understand;*/
    public int getIndexInParentSubnodes()
    {
	if (parentNode == null || parentNode.subnodes == null)
	    return -1;
	for(int i = 0;i < parentNode.subnodes.length;++i)
	    if (parentNode.subnodes[i] == this)
		return i;
	return -1;
    }

    public boolean shouldHaveExtraLine()
    {
	//Meaningful only for paragraphs;
	return false;
    }

}
