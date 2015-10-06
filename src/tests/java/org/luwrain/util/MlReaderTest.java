
package org.luwrain.util;

import org.junit.*;

public class MlReaderTest extends Assert
{
    @Test public void trivial()
    {
	TestingMlReader r = new TestingMlReader();
	r.run("abc");
	assertTrue(r.result().equals("abc"));
    }

    @Test public void cdata()
    {
	final TestingMlReader r = new TestingMlReader();
	r.run("abc<![cdata[123]]>cba");
	write("result=" + r.result());
	assertTrue(r.result().equals("abc123cba"));
	r.run("abc<![cdata[ 123 ]]>cba");
	write("result=" + r.result());
	assertTrue(r.result().equals("abc123cbaabc 123 cba"));
    }

    @Test public void cdataRecurse()
    {
	final TestingMlReader r = new TestingMlReader();
	r.run("<![cdata[<![cdata[]]>");
	write("result=" + r.result());
	assertTrue(r.result().equals("<![cdata["));
    }

    @Test public void entities()
    {
	final TestingMlReader r = new TestingMlReader();
	r.run("&lt;");
	write("result=" + r.result());
	assertTrue(r.result().equals("<"));
	r.run("&gt;");
	write("result=" + r.result());
	assertTrue(r.result().equals("<>"));
	r.run("&amp;");
	write("result=" + r.result());
	assertTrue(r.result().equals("<>&"));
    }

    @Test public void decEntities()
    {
	final TestingMlReader r = new TestingMlReader();
	r.run("&#60;");
	write("result=" + r.result());
	assertTrue(r.result().equals("<"));
	r.run("&#62;");
	write("result=" + r.result());
	assertTrue(r.result().equals("<>"));
    }

    private void write(String s)
    {
	//	System.out.println(s);
    }
}
