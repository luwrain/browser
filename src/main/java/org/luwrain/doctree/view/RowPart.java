
package org.luwrain.doctree.view;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

class RowPart
{
    private Run run;

    /** Starting position in the text of the corresponding run*/
    private int posFrom = 0;

    /** Ending position in the text of the corresponding run*/
    private int posTo = 0;

    /** Absolute row index in a document*/
    int absRowNum = 0;

    /** Index in the corresponding paragraph*/
    private int relRowNum = 0;

    RowPart(Run run, 
	    int posFrom, int posTo,
int relRowNum)
    {
	NullCheck.notNull(run, "run");
	this.run = run;
	this.posFrom = posFrom;
	this.posTo = posTo;
	this.relRowNum = relRowNum;
    }

    String text()
    {
	if (run == null)
	    throw new NullPointerException("run may not be null");
	return run.text().substring(posFrom, posTo);
    }

    //Checks relRowNum and parents of runs
    boolean onTheSameRow(RowPart rowPart)
    {
	NullCheck.notNull(rowPart, "rowPart");
	return run.getParentNode() == rowPart.run.getParentNode() && relRowNum == rowPart.relRowNum;
    }

    Run run() {return run;}
    int posFrom() {return posFrom;}
    int posTo() {return posTo;}
    int relRowNum() {return relRowNum;}
}
