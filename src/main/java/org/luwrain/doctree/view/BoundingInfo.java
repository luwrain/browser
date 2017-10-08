
package org.luwrain.doctree.view;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

class BoundingInfo
{
    interface Acceptor
    {
	void accept(Run run, int posFrom, int posTo);
    }

    final Run runFrom;
    final Run runTo;
    final int posFrom;
    final int posTo;

    BoundingInfo(Run runFrom, int posFrom, Run runTo, int posTo)
    {
	if (runFrom == null && runTo == null)
	    throw new IllegalArgumentException("runFrom and runTo may not be null simultainously");
	if (runFrom != null && posFrom < 0)
	    throw new IllegalArgumentException("posFrom may not be negative");
	if (runTo != null && posTo < 0)
	    throw new IllegalArgumentException("posTo may not be negative");
	this.runFrom = runFrom;
	this.posFrom = posFrom;
	this.runTo = runTo;
	this.posTo = posTo;
	    }

    void filter(Run[] runs, Acceptor acceptor)
    {
	NullCheck.notNullItems(runs, "runs");
	NullCheck.notNull(acceptor, "acceptor");
	boolean accepting = runFrom != null;
	for(Run r: runs)
	{
	    if (accepting)
	    {
		if (r == runTo)
		{
		    acceptor.accept(run, 0, Math.min(r.getText().length(), posTo));
		    return;
		    		}
	    } else
	    {
		//not accepting
		if (r == runFrom)
		{
		    if (r == runTo)
		    {
			//runFrom == runTo, nothing strange
			acceptor.accept(r, Math.min(r.getText().length(), posFrom), Math.min(r.getText().length(), posTo));
			return;
		    }
		    acceptor.accept(r, Math.min(r.getText().length(), posFrom), r.getText().length());
		    accepting = true;
		    continue;
		}
		if (r == runTo)//runTo met before we accepted anything, as you wish...
		    return;
	    }
	    //r != runFrom and r != runTo
	    if (accepting)
		acceptor.acceptor(r, 0, r.getText().length());
	}
    }
}
