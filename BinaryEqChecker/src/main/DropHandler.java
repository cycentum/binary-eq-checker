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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * 
 * @author koumura
 *
 */
class DropHandler extends TransferHandler
{
	private PathText panel;
	
	DropHandler(PathText panel)
	{
		this.panel = panel;
	}

	@Override
	public boolean canImport(JComponent component, DataFlavor[] flavor)
	{
		if(component!=panel) return false;
		for(DataFlavor f: flavor)
		{
			if(f.isFlavorJavaFileListType()) return true;
		}
		return false;
	}

	@Override
	public boolean importData(JComponent component, Transferable transferable)
	{
		if(component!=panel) return false;
		if(transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
		{
			try
			{
				@SuppressWarnings("unchecked")
				List<File> files= (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				if(files.size()==0) return false;
				if(files.size()>1) return false;
				panel.fileDropped(files.get(0).toPath().toAbsolutePath().toString());
			}
			catch (UnsupportedFlavorException | IOException e)
			{
				e.printStackTrace();
				return false;
			}
			return true;
		}
		if(transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
		{
			try
			{
				String p=(String)transferable.getTransferData(DataFlavor.stringFlavor);
				panel.replaceSelection(p);
			}
			catch (UnsupportedFlavorException | IOException e)
			{
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void exportToClipboard(JComponent comp, Clipboard clip, int action)
	{
		if(comp!=panel) return;
		String string=panel.getSelectedText();
		if(string==null || string.length()==0) return;
		StringSelection ss=new StringSelection(string);
		clip.setContents(ss, ss);
		if(action==DropHandler.MOVE)
		{
			panel.replaceSelection("");
		}
	}
}
