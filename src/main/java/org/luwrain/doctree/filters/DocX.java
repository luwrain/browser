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
import java.math.BigInteger;
import java.nio.file.*;

import org.luwrain.doctree.*;
import org.apache.poi.xwpf.usermodel.*;

import org.luwrain.core.NullCheck;

public class DocX
{
    private Path path;
    private String wholeText;
    private HashMap<BigInteger, HashMap<Integer, Integer>> listInfo = new HashMap<BigInteger, HashMap<Integer, Integer>>();
    private int lastLvl = -1;

    public DocX(Path path)
    {
	NullCheck.notNull(path, "path");
	this.path = path;
    }

    public org.luwrain.doctree.Document constructDocument()
    {
    	try
    	{
	    final InputStream s = Files.newInputStream(path);
	    XWPFDocument doc = new XWPFDocument(s);
	    final org.luwrain .doctree.Document res = transform(doc);
	    res.setFormat("DOCX");
	    res.setUrl(path.toUri().toURL());
	    s.close();
	    return res;
    	} catch (IOException e)
    	{
	    e.printStackTrace();
	    return null;
	}
    }

    private org.luwrain.doctree.Document transform(XWPFDocument doc)
    {
	wholeText = ""; // упрощенное текстовое представление будет заполнено в процессе разбора
	final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
	anyRangeAsParagraph(subnodes,doc.getBodyElements(),0);
	final NodeImpl root = NodeFactory.newNode(Node.Type.ROOT);
	root.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
	return new org.luwrain.doctree.Document(root);
    }

    /* рекурсивный метод, вызывается для любого места в документе, способного содержать несколько элементов, представляя их как список параграфов
     * @param subnodes The list of nodes to get all new on current level
     * @param range The range to look through
     * @param lvl Current recurse level (must be zero for the root)
     */
    private void anyRangeAsParagraph(LinkedList<NodeImpl> subnodes,
				     List<IBodyElement> range, int lvl)
    {
	int i = 0;
	for (IBodyElement paragraph : range)
	{
	    if (paragraph.getClass() == XWPFTable.class)
	    {
		// We do this processing for the first cell only, skipping all others
		final NodeImpl table_node = NodeFactory.newNode(Node.Type.TABLE);
		subnodes.add(table_node);
		final LinkedList<NodeImpl> rows_subnodes = new LinkedList<NodeImpl>();
		final XWPFTable table = (XWPFTable) paragraph;
		wholeText+=table.getText();
		int r = 0;
		for (final XWPFTableRow trow : table.getRows())
		{ // для каждой строки таблицы
					r++;
					// создаем элементы структуры Node и добавляем текущую ноду
					// в список потомка
					final NodeImpl rowtable_node = NodeFactory.newNode(Node.Type.TABLE_ROW);
					rows_subnodes.add(rowtable_node);
					final LinkedList<NodeImpl> cels_subnodes = new LinkedList<NodeImpl>();
					int c = 0;
					for (final XWPFTableCell cell : trow.getTableCells())
					{ // для каждой ячейки таблицы
					    c++;
					    // Creating a node for table cell
					    final NodeImpl celltable_node = NodeFactory.newNode(Node.Type.TABLE_CELL);
					    final LinkedList<NodeImpl> incell_subnodes = new LinkedList<NodeImpl>();
					    cels_subnodes.add(celltable_node);
					    anyRangeAsParagraph(incell_subnodes, cell.getBodyElements(), lvl + 1);
					    celltable_node.subnodes = incell_subnodes.toArray(new NodeImpl[incell_subnodes.size()]);
					    checkNodesNotNull(celltable_node.subnodes);
					} // for(cells);
					rowtable_node.subnodes = cels_subnodes.toArray(new NodeImpl[cels_subnodes.size()]);
					checkNodesNotNull(rowtable_node.subnodes);
		} // for(trows);
		table_node.subnodes = rows_subnodes.toArray(new NodeImpl[rows_subnodes.size()]);
		checkNodesNotNull(table_node.subnodes);
	    } else
	    {
		parseParagraph(subnodes, paragraph);
	    }
	    i++;
	} // for(body elements)
    }

    /*
     * Анализирует тип параграфа и выделяет в соответствии с ним данные
     * @param subnodes список нод на текущем уровне собираемой структуры, в этот список будут добавлены новые элементы
     * @param paragraph элемент документа (параграф или элемент списка) или ячейка таблицы
     */
    void parseParagraph(LinkedList<NodeImpl> subnodes, IBodyElement element)
    {
	String className = element.getClass().getSimpleName();
	String paraText = "";
	if (element.getClass() == XWPFParagraph.class)
	{ // все есть параграф
	    final XWPFParagraph paragraph = (XWPFParagraph) element;
	    wholeText+=paragraph.getText();
	    if (paragraph.getNumIlvl() != null)
	    { // параграф с установленным уровнем - элемент списка
		// создаем элементы структуры Node и добавляем текущую ноду в
		// список потомка
		final NodeImpl node = NodeFactory.newNode(Node.Type.LIST_ITEM);
		subnodes.add(node);
		//
		BigInteger listId = paragraph.getNumID();
		int listLvl = paragraph.getNumIlvl().intValue();
		// если это новый список, то добавим пустой подсписок его
		// счетчиков
		if (!listInfo.containsKey(listId))
		    listInfo.put(listId, new HashMap<Integer, Integer>());
		// если уровень списка уменьшился, то очищаем счетчики выше
		// уровнем
		if (lastLvl > listLvl)
		{
		    for (Entry<Integer, Integer> lvls : listInfo.get(listId).entrySet())
			if (lvls.getKey() > listLvl)
			    listInfo.get(listId).put(lvls.getKey(), 1);
		}
		lastLvl = listLvl;
		// если в списке счетчиков значения нет, то иннициализируем его
		// 0 (позже он будет обязательно увеличен на 1)
		if (!listInfo.get(listId).containsKey(listLvl))listInfo.get(listId).put(listLvl, 0);
		// так как это очередной элемент списка, то увеличиваем его
		// счетчик на 1
		listInfo.get(listId).put(listLvl,listInfo.get(listId).get(listLvl) + 1);
		// формируем строку-номер
		String numstr = "";
		for (int lvl = 0; lvl <= listLvl; lvl++) numstr += listInfo.get(listId).get(lvl) + ".";
		paraText = paragraph.getText().trim();
		LinkedList<NodeImpl> item_subnodes = new LinkedList<NodeImpl>();
		item_subnodes.add(NodeFactory.newPara(paraText));
		node.subnodes = item_subnodes.toArray(new NodeImpl[item_subnodes.size()]);
	    } else
	    {
		paraText = paragraph.getText().trim();
		subnodes.add(NodeFactory.newPara(paraText));
	    }
	} else
	{
	    subnodes.add(NodeFactory.newPara(paraText));
	}
    }

    private void checkNodesNotNull(NodeImpl[] nodes)
    {
	if (nodes == null)
	    throw new NullPointerException("nodes is null");
	for (int i = 0; i < nodes.length; ++i)
	    if (nodes[i] == null)
		throw new NullPointerException("nodes[" + i + "] is null");
    }
}
