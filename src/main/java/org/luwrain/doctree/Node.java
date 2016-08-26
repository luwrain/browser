/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

import java.util.*;

import org.luwrain.core.*;

public class Node
{
    public enum Type {
	ROOT, SECTION, PARAGRAPH,
	TABLE, TABLE_ROW,TABLE_CELL, 
	UNORDERED_LIST, ORDERED_LIST, LIST_ITEM,
	SMIL_PARAGRAPH // daisy dtbook: Parallel time grouping in which multiple elements (e.g., text, audio, and image) play back simultaneously
    };

    static public final int IMPORTANCE_REGULAR = 50;
    protected static final int MIN_TABLE_CELL_WIDTH = 8;

    protected Type type;
    public ExtraInfo extraInfo = null;
    public int importance = IMPORTANCE_REGULAR;
Node[] subnodes = new Node[0];
    Node parentNode;
    protected final TitleRun titleRun = new TitleRun(this.getClass().getName());

    /** The exact meaning of a level depends on the node type*/
    int level = 0;

    /**Absolute horizontal position in the area*/
    int x = -1;

    /**Absolute vertical position in the area*/
    int y = -1;
    int width = 0;
    int height = 0;

    boolean empty = false;

	Node(Type type)
    {
	this.type = type;
    }

    public Node[] getSubnodes()
    {
	return subnodes != null?subnodes:new Node[0];
    }

    public int getSubnodeCount()
    {
	return subnodes != null?subnodes.length:0;
    }

    public int getNodeX()
    {
	return x;
    }

public int getNodeY()
    {
	return y;
    }

public int getNodeWidth()
    {
	return width;
    }

public int getNodeHeight()
    {
	return height;
    }

    public boolean noSubnodes()
    {
	return subnodes == null || subnodes.length < 1;
    }

    void commit()
    {
	if (type == Type.ROOT)
	    parentNode = null;
	if (titleRun != null)
	    titleRun.setParentNode(this);
	if (subnodes == null)
	    subnodes = new Node[0];
	for(Node n: subnodes)
	{
	    n.parentNode = this;
	    n.commit();
	}
	if (type == Type.ORDERED_LIST || type == Type.UNORDERED_LIST)
	    for(int i = 0;i < subnodes.length;++i)
		if (subnodes[i].type != Node.Type.LIST_ITEM)
		{
		    final Node n = NodeFactory.newNode(Type.LIST_ITEM);
		    n.subnodes = new Node[]{subnodes[i]};
		    n.parentNode = this;
		    n.commit();
		    subnodes[i] = n;
		}
    }

void setEmptyMark()
    {
	empty = true;
	if (subnodes == null || subnodes.length < 1)
	    return;
	for(Node n:subnodes)
	{
	    n.setEmptyMark();
	    if (!n.empty)
		empty = false;
	}
    }

    //Must return the number of deleted subnodes
    int prune()
    {
	if (subnodes == null)
	    return 0;
	int k = 0;
	for(int i = 0;i < subnodes.length;++i)
	    if (subnodes[i].empty)
		++k; else
		subnodes[i - k] = subnodes[i];
	if (k > 0)
	    subnodes = Arrays.copyOf(subnodes, subnodes.length - k);
	for(Node n: subnodes)
	    k += n.prune();
	return k;
    }

    @Override public String toString()
    {
	if (subnodes == null)
	    return "";
	String s = "";
	for(Node n: subnodes)
	{
	    if (!s.isEmpty())
		s += " ";
	    s += n.toString();
	}
	return s;
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

    /*
    boolean isLastInParentSubnodes()
    {
	final int count = getParentSubnodeCount();
	final int index = getIndexInParentSubnodes();
	if (index < 0 || count < 0)
	    return false;
	return index + 1 == count;
    }
    */

    public TitleRun getTitleRun()
    {
	return titleRun;
    }

    public Type getType()
    {
	return type;
    }

    public void setSubnodes(Node[] subnodes)
    {
	NullCheck.notNullItems(subnodes, "subnodes");
	this.subnodes = subnodes;
    }
}
