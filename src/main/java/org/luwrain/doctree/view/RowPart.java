
package org.luwrain.doctree.view;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

class RowPart
{
    /** The run this part is associated with*/
    final Run run;

    /** Starting position in the text of the corresponding run*/
    final int posFrom;

    /** Ending position in the text of the corresponding run*/
    final int posTo;

    /** Index in the corresponding paragraph*/
    final int relRowNum;

    /** Absolute row index in the document*/
    int absRowNum = 0;

    RowPart(Run run, int posFrom, int posTo,
	    int relRowNum)
    {
	NullCheck.notNull(run, "run");
	this.run = run;
	this.posFrom = posFrom;
	this.posTo = posTo;
	this.relRowNum = relRowNum;
    }

    String getText()
    {
	return run.text().substring(posFrom, posTo);
    }

    //Checks relRowNum and parents of runs
    boolean onTheSameRow(RowPart rowPart)
    {
	NullCheck.notNull(rowPart, "rowPart");
	return run.getParentNode() == rowPart.run.getParentNode() && relRowNum == rowPart.relRowNum;
    }
}
