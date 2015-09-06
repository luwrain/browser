
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
	final String path = args[0];
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
	final int count = doc.getLineCount();
	for(int i = 0;i < count;++i)
	    System.out.println(doc.getLine(i));
    }
}
