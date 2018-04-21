
package org.luwrain.app.browser;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

class ActionLists
{
    private final Luwrain luwrain;
    private final Strings strings;

    ActionLists(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
    }

    Action[] getBrowserActions()
    {
	return new Action[]{
	    new Action("open-url", strings.actionOpenUrl(), new KeyboardEvent(KeyboardEvent.Special.F6)),
	    new Action("refresh", strings.actionRefresh(), new KeyboardEvent(KeyboardEvent.Special.F5)),
	    new Action("stop", strings.actionStop(), new KeyboardEvent(KeyboardEvent.Special.ESCAPE)),
	    new Action("history-prev", strings.actionHistoryPrev(), new KeyboardEvent(KeyboardEvent.Special.BACKSPACE)),
	    new Action("show-graphical", strings.actionShowGraphical(), new KeyboardEvent(KeyboardEvent.Special.F10)),
	    	    new Action("copy-url", strings.actionCopyUrl(), new KeyboardEvent(KeyboardEvent.Special.F7)),
	    new Action("copy-ref", strings.actionCopyRef(), new KeyboardEvent(KeyboardEvent.Special.F8)),
	};
    }
}
