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
    static public final int IMPORTANCE_REGULAR = 50;
    protected static final int MIN_TABLE_CELL_WIDTH = 8;

    public Type type;
    public ExtraInfo extraInfo = null;
    public int importance = IMPORTANCE_REGULAR;
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

	public String id=null;
    @Override public String getId()
	{
		return id;
	}
    public void setId(String id)
    {
    	this.id=id;
    }

    public String smil=null;
    public String getSmil()
	{
		return smil;
	}
	public void setSmil(String smil)
	{
		this.smil=smil;
	}

	NodeImpl(Type type)
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
    void calcWidth(int recommended)
    {
	switch (type)
	{
	case ROOT:
	case SECTION:
	case UNORDERED_LIST:
	case ORDERED_LIST:
	    break;
	default:
	    throw new IllegalArgumentException("unknown node type " + type);
	}
	width = recommended;
	if (subnodes == null || subnodes.length < 1)
	    return;
	for(NodeImpl n: subnodes)
	{
	    n.calcWidth(recommended);
	    if (width < n.width)
		width = n.width;
	}
    }

    //Launched after RowPartsBuilder;
    void calcHeight()
    {
	switch (type)
	{
	case ROOT:
	case SECTION:
	case UNORDERED_LIST:
	case ORDERED_LIST:
	    break;
	default:
	    throw new IllegalArgumentException("unknown node type " + type);
	}
	height = 0;
	if (subnodes == null)
	    return;
	for(NodeImpl n: subnodes)
	    n.calcHeight();
	for(NodeImpl n: subnodes)
	    height += n.height;
    }

    //Launched after calcHeight;
    void calcPosition()
    {
	switch (type)
	{
	case ROOT:
	    x = 0;
	    y = 0;
	case SECTION:
	case UNORDERED_LIST:
	case ORDERED_LIST:
	    break;
	default:
	    throw new IllegalArgumentException("unknown node type " + type);
	}
	int offset = 0;
	for(NodeImpl n: subnodes)
	{
	    n.x = x;
	    n.y = y + offset;
	    offset += n.height;
	    if (type == Type.ROOT)
		++offset;
	    n.calcPosition();
	}
    }

    void commit()
    {
	if (type == Type.ROOT)
	    parentNode = null;
	if (subnodes == null)
	    subnodes = new NodeImpl[0];
	if (type == Type.ORDERED_LIST || type == Type.UNORDERED_LIST)
	{
	    for(int i = 0;i < subnodes.length;++i)
	    {
		if (subnodes[i].type != Node.Type.LIST_ITEM)
		{
		    final NodeImpl n = NodeFactory.newNode(Type.LIST_ITEM);
		    n.subnodes = new NodeImpl[]{subnodes[i]};
		    subnodes[i] = n;
		}
	    }
	}
	for(NodeImpl n: subnodes)
	{
	    n.parentNode = this;
	    n.commit();
	}
    }

    void setEmptyMark()
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

    void removeEmpty()
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

    void saveStatistics(Statistics stat)
    {
	++stat.numNodes;
	if (subnodes != null)
	    for(NodeImpl n: subnodes)
		n.saveStatistics(stat);
    }

    /** 
     * @return -1 if there is no a parent node or there is a consistency error
     */
    Node.Type getParentType()
    {
	return parentNode != null && parentNode.subnodes != null?parentNode.type:null;
    }

    /** @return -1 if there is no a parent node or there is a consistency error*/
    int getParentSubnodeCount()
    {
	return parentNode != null && parentNode.subnodes != null?parentNode.subnodes.length:-1;
    }

    /** @return -1 if it is impossible to understand;*/
    int getIndexInParentSubnodes()
    {
	if (parentNode == null || parentNode.subnodes == null)
	    return -1;
	for(int i = 0;i < parentNode.subnodes.length;++i)
	    if (parentNode.subnodes[i] == this)
		return i;
	return -1;
    }
}
