/*
 * Copyright (c) 2016 Takuya KOUMURA.
 * http://cycentum.com/software/binaryeqchecker/
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.TransferHandler;

@SuppressWarnings("serial")
class DropHandler extends TransferHandler
{
	private PathText panel;
	
	DropHandler(PathText panel)
	{
		this.panel = panel;
	}

	@Override
	public boolean canImport(TransferSupport support)
	{
		if (!support.isDrop()) return false;
		if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return false;
		return true;
	}

	@Override
	public boolean importData(TransferSupport support) {
		// 受け取っていいものか確認する
		if (!canImport(support)) {
			return false;
		}

		// ドロップ処理
		Transferable t = support.getTransferable();
		try {
			// ファイルを受け取る
			@SuppressWarnings("unchecked")
			List<File> files = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
			if(files.size()==0) return false;
			if(files.size()>1) return false;
			panel.fileDropped(files.get(0).toPath());
		}
		catch (UnsupportedFlavorException | IOException e) {e.printStackTrace();}
		return true;
	}
}
