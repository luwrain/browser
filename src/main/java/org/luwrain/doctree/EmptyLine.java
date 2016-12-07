
package org.luwrain.doctree;

import java.util.*;

public class EmptyLine extends Paragraph
{
    EmptyLine()
    {
	runs = new Run[]{new TextRun("")};
    }

    @Override void setEmptyMark()
    {
	empty = false;
    }

    @Override int prune()
    {
	    return 0;
    }
}
