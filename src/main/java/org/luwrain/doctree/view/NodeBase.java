
package org.luwrain.doctree.view;

import org.luwrain.core.*;

public class NodeBase
{
    /**Absolute horizontal position in the area*/
    private int x = -1;

    /**Absolute vertical position in the area*/
    private int y = -1;

    public int width = 0;
    private int height = 0;

    private RowPart[] rowParts = new RowPart[0];

    public int getNodeX()
    {
	return x;
    }

    public void setNodeX(int value)
    {
	x = value;
    }

    public int getNodeY()
    {
	return y;
    }

    public void setNodeY(int value)
    {
	y = value;
    }

    public int getNodeWidth()
    {
	return width;
    }

    public void setNodeWidth(int value)
    {
	width = value;
    }

    public int getNodeHeight()
    {
	return height;
    }

    public void setNodeHeight(int value)
    {
	height = value;
    }

    public void setRowParts(RowPart[] rowParts)
    {
	NullCheck.notNullItems(rowParts, "rowParts");
	this.rowParts = rowParts != null?rowParts:new RowPart[0];
    }

    public RowPart[] getRowParts()
    {
	return rowParts;
    }
}
