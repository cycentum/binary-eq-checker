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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.miginfocom.swing.MigLayout;

/**
 * Main class.
 * @author koumura
 *
 */
public class Checker
{
	private JFrame mainFrame;
	private JPanel mainPanel;
	private PathText[] filePanel;
	private JProgressBar[] progressBar;
	private JButton checkButton;
	private JLabel eqLabel;
	private Loader loader;
	
	private int panelWidth=200, panelHeight=100;
	private int frameWidth=panelWidth*2+40, frameHeight=panelHeight*2+95;
	
	private Checker()
	{
		mainFrame=new JFrame("Binary Eq Checker");
		mainPanel=new JPanel();
		mainFrame.add(mainPanel);
		mainPanel.setLayout(new MigLayout());
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(frameWidth, frameHeight);
		
		filePanel=new PathText[2];
		for(int i=0; i<filePanel.length; ++i)
		{
			filePanel[i]=new PathText(this, i);
			if(i<filePanel.length-1) mainPanel.add(filePanel[i], "w "+panelWidth+"!,h "+panelHeight+"!");
			else mainPanel.add(filePanel[i], "wrap,w "+panelWidth+"!,h "+panelHeight+"!");
		}
		
		progressBar=new JProgressBar[filePanel.length];
		for(int i=0; i<progressBar.length; ++i)
		{
			progressBar[i]=new JProgressBar();
			if(i<progressBar.length-1) mainPanel.add(progressBar[i], "w "+panelWidth+"!");
			else mainPanel.add(progressBar[i], "wrap,w "+panelWidth+"!");
		}
		
		checkButton=new JButton("Check");
		mainPanel.add(checkButton, "grow,w "+panelWidth+"!,h "+panelHeight+"!");
		checkButton.addActionListener(new ButtonList(this));
		checkButton.setFont(checkButton.getFont().deriveFont((float)(checkButton.getFont().getSize()*3)));
		
		eqLabel=new JLabel("?");
		mainPanel.add(eqLabel, "wrap,center,h "+panelHeight+"!");
		eqLabel.setFont(eqLabel.getFont().deriveFont((float)eqLabel.getFont().getSize()*6));
		
		JLabel urlLabel=new JLabel("www.cycentum.com");
		mainPanel.add(urlLabel, "span,right");
		urlLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event)
			{
				try
				{
					Desktop.getDesktop().browse(new URI("www.cycentum.com"));
				}
				catch (Exception ex) {}
			}
		});
		urlLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		loader=new Loader(this, 1024*1024);
	}
	
	/**
	 * Resets foreground color and the progress bar.
	 * @param index
	 */
	void fileDropped(int index)
	{
		eqLabel.setText("?");
		eqLabel.setForeground(Color.black);
		progressBar[index].setValue(0);
	}
	
	public JProgressBar[] getProgressBar() {
		return progressBar;
	}

	private void check()
	{
		boolean ok=true;
		for(PathText fp: filePanel)
		{
			fp.updatePath();
			if(fp.getPath()==null||!Files.exists(fp.getPath())||Files.isDirectory(fp.getPath()))
			{
				ok=false;
				fp.red();
			}
		}
		if(!ok)
		{
			eqLabel.setText("?");
			eqLabel.setForeground(Color.red);
			return;
		}
		
		checkButton.setText("Cancel");
		
		eqLabel.setForeground(Color.black);
		eqLabel.setText("...");
		for(int i=0; i<filePanel.length; ++i)
		{
			loader.getPath()[i]=filePanel[i].getPath();
		}
		loader.load();
	}
	
	void loadFinished()
	{
		checkButton.setText("Check");
		if(loader.isIncomplete()) return;
		if(loader.isError())
		{
			eqLabel.setForeground(Color.red);
			return;
		}
		if(Arrays.equals(loader.getData()[0], loader.getData()[1]))
		{
			eqLabel.setText("=");
		}
		else
		{
			eqLabel.setText("\u2260");
		}
	}
	
	private void cancel()
	{
		loader.setRunning(false);
		eqLabel.setText("?");
		eqLabel.setForeground(Color.black);
	}
	
	/**
	 * Action listener of the check button.
	 * @author koumura
	 *
	 */
	private static class ButtonList implements ActionListener
	{
		private Checker checker;
		
		private ButtonList(Checker checker) {
			this.checker = checker;
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
			String buttonText=checker.checkButton.getText();
			if(buttonText.equals("Check")) checker.check();
			else if(buttonText.equals("Cancel")) checker.cancel();
		}
		
	}
	
	/**
	 * Entry point.
	 * Creates main class and show the main frame.
	 * @param arg
	 */
	public static void main(String... arg)
	{
		Checker checker=new Checker();
		
//		Path path=Paths.get("./res/favicon.ico");
		Path path=Paths.get("./res/iconAlpha.png");
		try
		{
			BufferedImage image=ImageIO.read(path.toFile());
			checker.mainFrame.setIconImage(image);
		}
		catch(IOException e){}
		
		checker.mainFrame.setVisible(true);
	}
}
