
package org.luwrain.controls.web;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.controls.web.WebArea.Callback.MessageType;

final class Model
{
    static private final String LOG_COMPONENT = WebArea.LOG_COMPONENT;
    
    final Container[] containers;

    Model(Container[] containers)
    {
	NullCheck.notNullItems(containers, "containers");
	this.containers = containers;
    }

    View buildView()
    {
	return new View(this);
    }

        public void printToLog()
    {
	for(int i = 0;i < containers.length;++i)
	    if (containers[i] != null)
	    {
		Log.debug(LOG_COMPONENT, containers[i].toString());
	    }
    }

}
