package org.luwrain.doctree.dtbook;

import org.luwrain.doctree.Document;

public class DTBookFile
{
	public String id;
	// relative file name
	public String name;
	// mime media-type of file
	public String type;
	
	public DTBookFile(String id,String name,String type)
	{
		this.id=id;
		this.name=name;
		this.type=type;
	}

	// * files was loaded
	// application/smil
	// application/x-dtbncx+xml
	// text/xml
	// application/x-dtbook+xml
	// * was not loaded
	// application/x-dtbresource+xml
	// audio/mpeg
	// text/css
	// ..
	// parsed version of document, if it needed for DAISY reading
	public Document document=null;
}
