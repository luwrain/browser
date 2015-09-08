
package org.luwrain.doctree.filters;

import java.util.*;
import org.luwrain.util.*;

class HtmlConfig implements MlReaderConfig
{
    private final String[] validTags = new String[]{
	"a",
	"abbr",
	"acronym",
	"address",
	"applet",
	"area",
	"article",
	"aside",
	"audio",
	"b",
	"base",
	"basefont",
	"bdi",
	"bdo",
	"big",
	"blockquote",
	"body",
	"br",
	"button",
	"canvas",
	"caption",
	"center",
	"cite",
	"code",
	"col",
	"colgroup",
	"datalist",
	"dd",
	"del",
	"details",
	"dfn",
	"dialog",
	"dir",
	"div",
	"dl",
	"dt",
	"em",
	"embed",
	"fieldset",
	"figcaption",
	"figure",
	"font",
	"footer",
	"form",
	"frame",
	"frameset",
	"h1",
	"h6",
	"head",
	"header",
	"hr",
	"html",
	"i",
	"iframe",
	"img",
	"input",
	"ins",
	"kbd",
	"keygen",
	"label",
	"legend",
	"li",
	"link",
	"main",
	"map",
	"mark",
	"menu",
	"menuitem",
	"meta",
	"meter",
	"nav",
	"nobr",
	"noframes",
	"noscript",
	"object",
	"ol",
	"optgroup",
	"option",
	"output",
	"p",
	"param",
	"pre",
	"progress",
	"q",
	"rp",
	"rt",
	"ruby",
	"s",
	"samp",
	"script",
	"section",
	"select",
	"small",
	"source",
	"span",
	"strike",
	"strong",
	"style",
	"sub",
	"summary",
	"sup",
	"table",
	"tbody",
	"td",
	"textarea",
	"tfoot",
	"th",
	"thead",
	"time",
	"title",
	"tr",
	"track",
	"tt",
	"u",
	"ul",
	"var",
	"video",
	"wbr"};

    final String[] nonClosingTags = new String[]{
	"!doctype",
	"input",
	"br",
	"link",
	"img",
	"meta"
    }; 

    @Override public boolean mlTagMustBeClosed(String tag)
    {
	final String adjusted = tag.toLowerCase().trim();
	for(String s: nonClosingTags)
	    if (s.equals(adjusted))
		return false;
	return true;
    }

    @Override public boolean mlAdmissibleTag(String tagName, LinkedList<String> tagsStack)
    {
	//May not open a tag inside of a script;
	if (!tagsStack.isEmpty())
	    if (tagsStack.getLast().toLowerCase().trim().equals("script"))
		return false;
	final String adjusted = tagName.toLowerCase().trim();
	for(String s: validTags)
	    if (s.equals(adjusted))
		return true;
	return false;
    }
}
