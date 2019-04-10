
package org.luwrain.controls.web;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.block.*;
import org.luwrain.browser.*;

public class DefaultAppearance implements WebArea.Appearance
{
    protected ControlEnvironment context;

    public DefaultAppearance(ControlEnvironment context)
    {
	NullCheck.notNull(context, "context");
	this.context = context;
    }

    @Override public void announceFirstRow(Block block, BlockRowFragment[] objs)
    {
	/*
	NullCheck.notNull(type, "type");
	NullCheck.notNullItems(objs, "objs");
	final Sounds sound;
	switch(type)
	{
	case PARA:
	    sound = Sounds.PARAGRAPH;
	    break;
	case LIST_ITEM:
	    sound = Sounds.LIST_ITEM;
	    break;
	case HEADING:
	    sound = Sounds.DOC_SECTION;
	    break;
	default:
	    sound = null;
	}
	context.setEventResponse(DefaultEventResponse.text(sound, makeResponseText(objs)));
	*/
    }

    @Override public void announceRow(Block block, BlockRowFragment[] objs)
    {
	NullCheck.notNull(block, "block");
	NullCheck.notNullItems(objs, "objs");
	context.setEventResponse(DefaultEventResponse.text(makeResponseText(objs)));
    }

    @Override public String getRowTextAppearance(BlockRowFragment[] objs)
    {
	NullCheck.notNullItems(objs, "objs");
	final StringBuilder b = new StringBuilder();
	for(BlockRowFragment o: objs)
	{
	    /*
	    if (o instanceof WebText)
	    {
		final WebText webText = (WebText)o;
		b.append(webText.getText());
		continue;
	    }
	    if (o instanceof WebTextInput)
	    {
		final WebTextInput webTextInput = (WebTextInput)o;
		//FIXME:width
		b.append("[").append(webTextInput.getText()).append("]");
		continue;
	    }
	    if (o instanceof WebButton)
	    {
		final WebButton webButton = (WebButton)o;
		final String text;
		if (webButton.getTitle().length() + 2 >= webButton.getWidth())
		    text = webButton.getTitle() ; else
		    text = webButton.getTitle().substring(0, webButton.getWidth() - 5) + "...";
		b.append("[").append(text).append("]");
		continue;
	    }
	    if (o instanceof WebImage)
	    {
		final WebImage webImage = (WebImage)o;
		//FIXME:
		b.append("[").append(webImage.getComment()).append("]");
		continue;
	    }
	    */
	}
	return new String(b);
    }

    protected String makeResponseText(BlockRowFragment[] objs)
    {
	NullCheck.notNullItems(objs, "objs");
	final StringBuilder b = new StringBuilder();
	/*
	for(WebObject o: objs)
	{
	    if (o instanceof WebText)
	    {
		final WebText webText = (WebText)o;
		if (webText.hasHref())
		    b.append (" ссылка ");
		b.append(webText.getText());
		continue;
	    }
	    if (o instanceof WebTextInput)
	    {
		final WebTextInput webTextInput = (WebTextInput)o;
		b.append(" поле для ввода текста ").append(webTextInput.getText()).append(" ");
		continue;
	    }
	    if (o instanceof WebButton)
	    {
		final WebButton webButton = (WebButton)o;
		b.append(" кнопка ").append(webButton.getTitle()).append(" ");
		continue;
	    }
	    if (o instanceof WebImage)
	    {
		final WebImage webImage = (WebImage)o;
		b.append(" изображение ").append(webImage.getComment()).append(" ");
		continue;
	    }
	}
	return new String(b);
    }
	*/
	return "";
    }
}
