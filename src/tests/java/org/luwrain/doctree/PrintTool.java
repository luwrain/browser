
package org.luwrain.doctree;

public class PrintTool
{
    static public void main(String[] args)
    {
	if (args.length < 1)
	{
	    System.err.println("No file to read");
	    return;
	}
	String path;
	boolean rows;
	if (args[0].equals("--rows"))
	{
	    if (args.length < 2)
	    {
		System.err.println("No file to read");
		return;
	    }
	    path = args[1];
	    rows = true;
	} else
	{
	    path = args[0];
	    rows = false;
	}
	final int format = Factory.suggestFormat(path);
	if (format == Factory.UNRECOGNIZED)
	{
	    System.err.println("Error choosing a filter to read the file " + path);
	    return;
	}
	final Document doc = Factory.loadFromFile(format, path);
	if (doc == null)
	{
	    System.err.println("Error reading " + path);
	    return;
	}
	if (rows)
	{
	    Iterator it = doc.getIterator();
	    do {
		printItState(it);
	    } while (it.moveNext());
	} else
	{
	    final int count = doc.getLineCount();
	    for(int i = 0;i < count;++i)
		System.out.println(doc.getLine(i));
	}
    }

    static private void printItState(Iterator it)
    {
	if (it.isCurrentRowEmpty())
	{

	final Row row = it.getCurrentRow();
	System.out.println("<<<EMPTY ROW " + row.getRowX() + "," + row.getRowY() + ">>>");
	    return;
	}
	if (it.isCurrentRowFirst() && it.isCurrentParaFirst())
	{
	    System.out.println();
	    if (it.isCurrentParaContainerTableCell())
	    {
		final TableCell cell = it.getTableCell();
		System.out.println("*** Table cell (" + cell.getColIndex() + "," + cell.getRowIndex() + ")");
	    }
	}
	final Row row = it.getCurrentRow();
	final String paraSign = it.isCurrentRowFirst()?"[PARA] ":"";
	System.out.println(paraSign + "(" + row.getRowX() + "," + row.getRowY() + ") " + it.getCurrentText());
    }
}
