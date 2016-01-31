/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.doctree.filters;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;

import org.luwrain.doctree.Document;
import org.luwrain.doctree.Node;
import org.luwrain.doctree.NodeImpl;
import org.luwrain.doctree.NodeFactory;
import org.luwrain.doctree.ParagraphImpl;

import org.apache.poi.hwpf.*;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class Doc
{
    private String fileName = "";
    private String wholeText;

    public Doc(String fileName)
    {
	this.fileName = fileName;
	if (fileName == null)
	    throw new NullPointerException("fileName may not be null");
    }

    public Document constructDocument()
    {
	FileInputStream  s = null;
	try {
	    File docFile=new File(fileName);
	    s = new FileInputStream(docFile.getAbsolutePath());
	    HWPFDocument doc = new HWPFDocument(s);
	    Document res = transform(doc);
	    s.close(); //closing fileinputstream
	    return res;
	} catch (IOException e)
	{
	    e.printStackTrace();
	    try {
		if (s != null)
		    s.close();
	    }
	    catch (IOException ee) {}
	    return null;
	}
    }

    private Document transform(HWPFDocument doc)
    {
	wholeText = doc.getDocumentText();
	final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
	Range range = doc.getRange();
	anyRangeAsParagraph(subnodes,range,0);
	final NodeImpl root = NodeFactory.newNode(Node.Type.ROOT);
	root.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
	return new Document(root);
    }

    /* рекурсивный метод, вызывается для любого места в документе, способного содержать несколько элементов, представляя их как список параграфов
     * @param	subnodes	The list of nodes to get all new on current level
     * @param	range	The range to look through
     * @param	lvl	Current recurse level (must be zero for the root)
     */
    private void anyRangeAsParagraph(LinkedList<NodeImpl> subnodes,
				    Range range,
				    int lvl)
    {
	int i=0;
	final int num=range.numParagraphs();
	Boolean inTable=false;//Allows to silently skip all table cells except of first;
	while(i<num)
	{
	    final Paragraph paragraph = range.getParagraph(i);
	    if (paragraph.getTableLevel() > lvl)
	    {
		if(!inTable)
		{
		    //We do this processing for the first cell only, skipping all others
		    final NodeImpl table_node = NodeFactory.newNode(Node.Type.TABLE);
		    subnodes.add(table_node);
		    final LinkedList<NodeImpl> rows_subnodes = new LinkedList<NodeImpl>();
		    inTable=true;//We came to the table;
		    final org.apache.poi.hwpf.usermodel.Table table = range.getTable(paragraph);
		    //		    System.out.println(lvl+", is a table: "+table.numRows()+" rows");
		    final int rnum=table.numRows();
		    for(int r=0;r<rnum;r++)
		    { // для каждой строки таблицы
			// создаем элементы структуры Node и добавляем текущую ноду в список потомка
			final NodeImpl rowtable_node = NodeFactory.newNode(Node.Type.TABLE_ROW);
			rows_subnodes.add(rowtable_node);
			final LinkedList<NodeImpl> cels_subnodes = new LinkedList<NodeImpl>();
			final TableRow trow=table.getRow(r);
			final int cnum=trow.numCells();
			for(int c=0;c<cnum;c++)
			{ // для каждой ячейки таблицы
			    //Creating a node for table cell
			    final NodeImpl celltable_node = NodeFactory.newNode(Node.Type.TABLE_CELL);
			    final LinkedList<NodeImpl> incell_subnodes = new LinkedList<NodeImpl>();
			    cels_subnodes.add(celltable_node);
			    //			    System.out.print("* cell["+r+","+c+"]: ");
			    final TableCell cell=trow.getCell(c);
			    //Trying to figure out that we have just a text in the table cell
			    if(cell.numParagraphs()>1)
				anyRangeAsParagraph(incell_subnodes,cell,lvl+1); else
				parseParagraph(incell_subnodes,cell.getParagraph(0));
			    celltable_node.subnodes = incell_subnodes.toArray(new NodeImpl[incell_subnodes.size()]);
			    checkNodesNotNull(celltable_node.subnodes);
			} //for(cells);
			rowtable_node.subnodes = cels_subnodes.toArray(new NodeImpl[cels_subnodes.size()]);
			checkNodesNotNull(rowtable_node.subnodes);
		    } //for(rows);
		    table_node.subnodes = rows_subnodes.toArray(new NodeImpl[rows_subnodes.size()]);
		    checkNodesNotNull(table_node.subnodes);
		} // if(!inTable);
	    } else //if(paragraph.getTableLevel() > lvl);
	    {
		inTable=false;//We are not in table any more
		//		System.out.print(lvl+", not table: ");
		parseParagraph(subnodes,paragraph);
	    }
	    i++;
	} //while();
    }

    // listInfo[id of list][level]=counter;
    public HashMap<Integer,HashMap<Integer,Integer>> listInfo=new HashMap<Integer, HashMap<Integer,Integer>>();
    public int lastLvl=-1;

	/* Анализирует тип параграфа и выделяет в соответствии с ним данные
	 * @param	subnodes	список нод на текущем уровне собираемой структуры, в этот список будут добавлены новые элементы
	 * @param	paragraph	элемент документа (параграф или элемент списка) или ячейка таблицы
	 */
	public void parseParagraph(LinkedList<NodeImpl> subnodes,Paragraph paragraph)
	{
		String className=paragraph.getClass().getSimpleName();
		String paraText="";
		switch(className)
		{
			case "ListEntry":
				// создаем элементы структуры Node и добавляем текущую ноду в список потомка
				NodeImpl node = NodeFactory.newNode(Node.Type.LIST_ITEM);
				subnodes.add(node);
				//
				ListEntry elem=(ListEntry)paragraph;
				int sindex=elem.getStyleIndex();
				//StyleDescription style=that.doc.getStyleSheet().getStyleDescription(sindex);
				int listId=elem.getList().getLsid();
				int listLvl=elem.getIlvl();
				// если это новый список, то добавим пустой подсписок его счетчиков
				if(!listInfo.containsKey(listId)) listInfo.put(listId,new HashMap<Integer,Integer>());
				// если уровень списка уменьшился, то очищаем счетчики выше уровнем
				if(lastLvl>listLvl)
				{
					for(Entry<Integer,Integer> lvls:listInfo.get(listId).entrySet())
						if(lvls.getKey()>listLvl)
							listInfo.get(listId).put(lvls.getKey(), 1);
				}
				lastLvl=listLvl;
				// если в списке счетчиков значения нет, то иннициализируем его 0 (позже он будет обязательно увеличен на 1)
				if(!listInfo.get(listId).containsKey(listLvl)) listInfo.get(listId).put(listLvl, 0);
				// так как это очередной элемент списка, то увеличиваем его счетчик на 1
				listInfo.get(listId).put(listLvl, listInfo.get(listId).get(listLvl)+1);
				// формируем строку-номер
				String numstr="";
				for(int lvl=0;lvl<=listLvl;lvl++) numstr+=listInfo.get(listId).get(lvl)+".";
				paraText=paragraph.text().trim();
				//				System.out.println("LIST ENTRY:"+listLvl+", "+listId+", "+numstr+"["+paraText+"]");
				final LinkedList<NodeImpl> item_subnodes = new LinkedList<NodeImpl>();
				item_subnodes.add(NodeFactory.newPara(paraText));
				node.subnodes = item_subnodes.toArray(new NodeImpl[item_subnodes.size()]);
			break;
			case "Paragraph":
				paraText=paragraph.text().trim();
				//				System.out.println("PARAGRAPH:["+paraText+"]");
				subnodes.add(NodeFactory.newPara(paraText));
				/*
				// получение стилей текста
				int nrun=paragraph.numCharacterRuns();
				if(nrun>1) for(int r=0;r<nrun;r++)
				{
					CharacterRun run=paragraph.getCharacterRun(r);
//					System.out.println("RUN: ["+run.text()+"]");
				}
				*/
			break;
			default:
				paraText=paragraph.text().trim();
				//				System.out.println(className+"["+paraText+"]");
				subnodes.add(NodeFactory.newPara(paraText));
			break;
		}
	}

    private void checkNodesNotNull(NodeImpl[] nodes)
    {
	if (nodes == null)
	    throw new NullPointerException("nodes is null");
	for(int i = 0;i < nodes.length;++i)
	    if (nodes[i] == null)
		throw new NullPointerException("nodes[" + i + "] is null");
    }
}
