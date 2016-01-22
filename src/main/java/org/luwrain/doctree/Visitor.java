
package org.luwrain.doctree;

import org.luwrain.core.NullCheck;

public interface Visitor
{
    void visitNode(NodeImpl node);
    void visit(ListItem node);
    void visit(ParagraphImpl node);
    void visit(Section node);
    void visit(TableCell node);
    void visit(Table node);
    void visit(TableRow node);

    static public void walk(NodeImpl node, Visitor visitor)
    {
	NullCheck.notNull(node, "node");
	NullCheck.notNull(visitor, "visitor");
	if (node instanceof ListItem)
	    visitor.visit((ListItem)node); else
	if (node instanceof Section)
	    visitor.visit((Section)node); else
	if (node instanceof Table)
	    visitor.visit((Table)node); else
	if (node instanceof TableRow)
	    visitor.visit((TableRow)node); else
	if (node instanceof TableCell)
	    visitor.visit((TableCell)node); else
	if (node instanceof ParagraphImpl)
	    visitor.visit((ParagraphImpl)node); else
	    visitor.visitNode(node);
	if (node.subnodes != null)
	for(NodeImpl n: node.subnodes)
	    walk(n, visitor);
    }
}
