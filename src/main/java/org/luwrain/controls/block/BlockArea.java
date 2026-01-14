// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.controls.block;

import java.io.*;
import java.net.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

import static org.luwrain.core.DefaultEventResponse.*;
import static org.luwrain.core.NullCheck.*;
import static org.luwrain.core.Log.*;

public class BlockArea implements Area
{
    static final String
	LOG_COMPONENT = "blocks";
    static private final int
	MIN_VISIBLE_WIDTH = 20;

    public interface Appearance
    {
	void announceFirstBlockLine(Block block, BlockLine blockLine);
	void announceBlockLine(Block block, BlockLine blockLine);
	String getBlockLineTextAppearance(Block block, BlockLine blockLine);
    }

    public interface ClickHandler
    {
	boolean onClick(BlockArea area, Block block, int lineIndex, BlockLine line);
    }

    static public class Params
    {
	public ControlContext context = null;
	public Appearance appearance;
	public ClickHandler clickHandler = null;
    }

    protected final ControlContext context;
    protected final Appearance appearance;
    protected ClickHandler clickHandler = null;
    protected View view = null;
    protected BlockIterator it = null;
    int hotPointX = 0;
    protected final List<Block> blocks = new ArrayList<>();

    public BlockArea(Params params)
    {
	notNull(params, "params");
	notNull(params.context, "params.context");
	notNull(params.appearance, "params.appearance");
	this.context = params.context;
	this.appearance = params.appearance;
	this.clickHandler = params.clickHandler;
    }

    public void clear()
    {
	view = null;
	it = null;
    }

    public void setBlocks(Block[] blocks)
    {
	notNull(blocks, "blocks");
	this.blocks.clear();
	this.blocks.addAll(Arrays.asList(blocks));
	this.view = new View(appearance, this.blocks);
	this.it = new BlockIterator(this);
	this.hotPointX = 0;
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
	context.onAreaNewName(this);
	debug(LOG_COMPONENT, "Setting " + this.blocks.size() + " blocks");
    }

    public boolean isEmpty()
    {
	return view == null || it == null;
    }

    @Override public int getHotPointX()
    {
	if (isEmpty())
	    return 0;
	return it.getX() + hotPointX;
    }

    @Override public int getHotPointY()
    {
	if (isEmpty())
	    return 0;
	return it.getY();
    }

    @Override public int getLineCount()
    {
	if (isEmpty())
	    return 1;
	return view.getLineCount();
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	if (isEmpty())
	    return (index == 0)?noContentStr():"";
	return view.getLine(index);
    }

    @Override public String getAreaName()
    {
	return "FIXME";
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		return onClick();
	    case ARROW_RIGHT:
		return onMoveRight(event);
	    case ARROW_LEFT:
		return onMoveLeft(event);
	    case ARROW_DOWN:
		return onMoveDown(event);
	    case ARROW_UP:
		return onMoveUp(event);
	    }
	return false;
    }

    protected boolean onClick()
    {
	if (isEmpty() || clickHandler == null)
	    return false;
	return clickHandler.onClick(this, it.getBlock(), it.lineIndex, it.getBlock().getLine(it.lineIndex));
    }

    protected boolean onMoveRight(InputEvent event)
    {
	notNull(event, "event");
	if (noContent())
	    return true;
	final String text = it.getLineText(appearance);
	if (hotPointX >= text.length())
	{
	    context.setEventResponse(hint(Hint.END_OF_LINE));
	    return true;
	}
	++hotPointX;
	if (hotPointX >= text.length())
	{
	    context.setEventResponse(hint(Hint.END_OF_LINE));
	    return true;
	}
	context.onAreaNewHotPoint(this);
	context.setEventResponse(letter(text.charAt(hotPointX)));
	return true;
    }

    protected boolean onMoveLeft(InputEvent event)
    {
	notNull(event, "event");
	if (noContent())
	    return true;
	final String text = it.getLineText(appearance);
	if (hotPointX == 0)
	{
	    context.setEventResponse(hint(Hint.BEGIN_OF_LINE));
	    return true;
	}
	--hotPointX;
	if (hotPointX >= text.length())
	{
	    context.setEventResponse(hint(Hint.END_OF_LINE));
	    return true;
	}
	context.onAreaNewHotPoint(this);
	context.setEventResponse(letter(text.charAt(hotPointX)));
	return true;
    }

    protected boolean onMoveUp(InputEvent event)
    {
	notNull(event, "event");
	if (noContent())
	    return true;
	if (!it.movePrev())
	{
	    context.setEventResponse(hint(Hint.NO_ITEMS_ABOVE));
	    return true;
	}
	hotPointX = 0;
	context.onAreaNewHotPoint(this);
	announceRow();
	return true;
    }

    protected boolean onMoveDown(InputEvent event)
    {
	notNull(event, "event");
	if (noContent())
	    return true;
	if (!it.moveNext())
	{
	    context.setEventResponse(hint(Hint.NO_ITEMS_BELOW));
	    return true;
	}
	hotPointX = 0;
	context.onAreaNewHotPoint(this);
	announceRow();
	return true;
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	notNull(event, "event");
	return false;
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	notNull(query, "query");
	return false;
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    public void announceRow()
    {
	if (isEmpty())
	    return;
	if (it.lineIndex == 0)
	    appearance.announceFirstBlockLine(it.getBlock(), it.getLine()); else
	    appearance.announceBlockLine(it.getBlock(), it.getLine());
    }

    protected String noContentStr()
    {
	return context.getStaticStr("NoContent");
    }

    protected void noContentMsg()
    {
	context.setEventResponse(hint(Hint.NO_CONTENT));
    }

    protected boolean noContent()
    {
	if (isEmpty())
	{
	    noContentMsg();
	    return true;
	}
	return false;
    }
}
