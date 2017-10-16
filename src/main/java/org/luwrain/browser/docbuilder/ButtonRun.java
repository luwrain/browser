
package org.luwrain.browser.docbuilder;

import org.luwrain.core.*;
import org.luwrain.doctree.*;
import org.luwrain.browser.*;

public class ButtonRun extends WebRun
{
    protected final String text;

    ButtonRun(BrowserIterator it)
    {
	super(it);
	NullCheck.notNull(it, "it");
	final String s = it.getText();
	this.text = s != null?s:"";
    }

    @Override public String text()
    {
	return "Кнопка " + text;
    }

    //In browser thread only
    public void submit()
    {
	it.emulateSubmit();
    }
}
