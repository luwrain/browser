
package org.luwrain.controls.web;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.controls.web.WebArea.Callback.MessageType;

final class View
{
    final Model model;

	View(Model model)
    {
	NullCheck.notNull(model, "model");
	this.model = model;
    }
}
