
package org.luwrain.web;

import java.util.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

//import org.luwrain.controls.block.*;

import static org.luwrain.web.WebKitBlockBase.*;

public class WebKitBlockBaseTest 
{
    @Test public void singleLongRun()
    {
	final var b = new WebKitBlockBase();
	b.runs.add(new Run("Какие вкусные пирожки были сегодня к обеду, но особенно интересно, что будет завтра."));
	b.left = 0;
	b.right = 15;
	b.buildLines();
	assertEquals(7, b.lines.size());
	for(final var l: b.lines)
	    assertTrue(l.text.length() <= b.right - b.left);
	assertEquals("Какие вкусные ", b.lines.get(0).text);
	assertEquals("пирожки были ", b.lines.get(1).text);
	assertEquals("сегодня к ", b.lines.get(2).text);
	assertEquals("обеду, но ", b.lines.get(3).text);
	assertEquals("особенно ", b.lines.get(4).text);
	assertEquals("будет завтра.", b.lines.get(6).text);
    }
}
