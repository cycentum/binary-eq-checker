/*
 * Copyright (c) 2016 Takuya KOUMURA.
 * http://www.cycentum.com/
 * 
 * This file is part of Binary Eq Checker.
 * 
 * Binary Eq Checker is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package main;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;

/**
 * Text area.
 * @author koumura
 *
 */
class PathText extends JTextArea
{
	private Path path;
	private Checker checker;
	private int index;
	
	PathText(Checker checker, int index)
	{
		super("Drop a file or double click.");
		this.checker=checker;		
		this.index=index;
		
		setLineWrap(true);
		setTransferHandler(new DropHandler(this));
		addMouseListener(new MouseList(this));
	}
	
	/**
	 * Sets text to the path.
	 * @param path
	 */
	void fileDropped(String path)
	{
//		if(Files.isDirectory(path)) return;
//		setText(path.toAbsolutePath().toString());
//		this.path=path;
		setText(path);
		setForeground(Color.black);
		checker.fileDropped(index);
	}

	/**
	 * Converts text to a path.
	 */
	void updatePath()
	{
		path=Paths.get(getText());
	}
	
	Path getPath() {
		return path;
	}
	
	/**
	 * Sets forefround color to red.
	 */
	void red()
	{
		setForeground(Color.red);
	}
	
	/**
	 * Detects double click and open a file chooser.
	 * @author koumura
	 *
	 */
	private static class MouseList extends MouseAdapter
	{
		private PathText pathText;
		
		private MouseList(PathText pathText) {
			this.pathText = pathText;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount()==2)
			{
				JFileChooser chooser;
				if(pathText.path!=null)
				{
					Path dir=pathText.path.getParent();
					chooser=new JFileChooser(dir.toFile());
				}
				else chooser=new JFileChooser();
				int ch=chooser.showOpenDialog(pathText);
				if(ch==JFileChooser.APPROVE_OPTION)
				{
					Path path=chooser.getSelectedFile().toPath();
					pathText.fileDropped(path.toAbsolutePath().toString());
				}
			}
		}
	}
}
