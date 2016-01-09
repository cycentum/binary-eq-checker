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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.SwingUtilities;

public class Loader
{
	private Checker checker;
	private Run run;
	private byte[][] data;
	private Path[] path;
	private int singleSize;
	private boolean running, error, incomplete;
	
	/**
	 * @param singleSize byte
	 */
	Loader(Checker checker, int singleSize)
	{
		this.checker=checker;
		this.singleSize = singleSize;
		data=new byte[2][];
		path=new Path[2];
		run=new Run(this);
	}

	byte[][] getData() {
		return data;
	}

	void load()
	{
		Thread thread=new Thread(run);
		running=true;
		thread.start();
	}

	public Path[] getPath() {
		return path;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isError() {
		return error;
	}

	public boolean isIncomplete() {
		return incomplete;
	}

	private static class Run implements Runnable
	{
		private Loader loader;
		
		private Run(Loader loader) {
			this.loader = loader;
		}

		@Override
		public void run()
		{
			loader.error=false;
			loader.incomplete=false;
			for(int i=0; i<loader.path.length; ++i) if(loader.running)
			{
				int size=(int)loader.path[i].toFile().length();
				try(BufferedInputStream in=new BufferedInputStream(new FileInputStream(loader.path[i].toFile()), loader.singleSize*2))
				{
					loader.data[i]=new byte[size];
					int index=0;
					int len;
					loader.checker.getProgressBar()[i].setMaximum(size);
					while(loader.running&&(len=in.read(loader.data[i], index, Math.min(size-index, loader.singleSize)))>=0)
					{
						index+=len;
						loader.checker.getProgressBar()[i].setValue(index);
						if(index==size) break;
					}
					if(index<size) loader.incomplete=true;
				}
				catch(IOException e)
				{
					loader.error=true;
					break;
				}
			}
			SwingUtilities.invokeLater(()->loader.checker.loadFinished());
		}
	}
}
