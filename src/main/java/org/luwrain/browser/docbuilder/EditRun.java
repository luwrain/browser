
package org.luwrain.browser.docbuilder;

import org.luwrain.core.*;
import org.luwrain.doctree.*;
import org.luwrain.browser.*;

public class EditRun extends WebRun
{
    protected final String text;

    EditRun(BrowserIterator it)
    {
	super(it);
	NullCheck.notNull(it, "it");
	final String s = it.getText();
	this.text = s != null?s:"";
    }

    @Override public String text()
    {
	return "Ввод текста " + text;
    }

    //IOnly in browser thread
    public String getText()
    {
	return it.getText();
    }

    //Only in browser thread
    public void setText(String value)
    {
	NullCheck.notNull(value, "value");
	it.setText(value);
    }
}
