/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.dakror.liturfaliarcest.editor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.dakror.gamesetup.util.swing.WrapLayout;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.settings.CFG;

/**
 * @author Dakror
 */
public class FloorEditor extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static FloorEditor currentFloorEditor;
	
	int width, height;
	
	JPanel map;
	public String selectedTile;
	boolean border;
	boolean dragDelete;
	Point dragStart, dragEnd;
	
	public FloorEditor() {
		super("Liturfaliar Cest Editor: Boden");
		
		currentFloorEditor = this;
		
		setSize(1280, 720);
		setLocationRelativeTo(Editor.currentEditor);
		setResizable(false);
		setIconImage(Game.getImage("system/editor.png"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		initComponents();
		
		setVisible(true);
	}
	
	public void initComponents() {
		JSplitPane cp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		cp.setEnabled(false);
		cp.setDividerLocation(200);
		JPanel tilesets = new JPanel(new WrapLayout(WrapLayout.LEFT));
		
		for (String tileset : CFG.AUTOTILES)
			tilesets.add(new Autotile(0, 0, tileset, false));
		
		tilesets.setSize(200, 720);
		
		JScrollPane jsp = new JScrollPane(tilesets, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cp.add(jsp);
		
		map = new JPanel(null) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				if (dragEnd != null && dragStart != null) {
					Graphics2D g2 = (Graphics2D) g;
					Composite c = g2.getComposite();
					Color color = g2.getColor();
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
					g2.setColor(dragDelete ? Color.red : Color.green);
					int x = Math.min(dragStart.x, dragEnd.x);
					int y = Math.min(dragStart.y, dragEnd.y);
					int xM = Math.max(dragStart.x, dragEnd.x);
					int yM = Math.max(dragStart.y, dragEnd.y);
					g2.fillRect(x, y, xM - x + 32, yM - y + 32);
					g2.setComposite(c);
					g2.setColor(color);
				}
			}
		};
		KeyStroke keyStroke = KeyStroke.getKeyStroke("A");
		map.getActionMap().put("a-add", new AbstractAction("a-add") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Component c : map.getComponents())
					c.setBounds(c.getX() + 32, c.getY(), 32, 32);
				
				for (int i = 0; i < height; i++)
					map.add(new Autotile(0, i, "", true));
				
				width++;
				
				map.setPreferredSize(new Dimension(width * 32, height * 32));
				map.setSize(new Dimension(width * 32, height * 32));
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "a-add");
		keyStroke = KeyStroke.getKeyStroke("W");
		map.getActionMap().put("w-add", new AbstractAction("w-add") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Component c : map.getComponents())
					c.setBounds(c.getX(), c.getY() + 32, 32, 32);
				
				for (int i = 0; i < width; i++)
					map.add(new Autotile(i, 0, "", true));
				
				height++;
				
				map.setPreferredSize(new Dimension(width * 32, height * 32));
				map.setSize(new Dimension(width * 32, height * 32));
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "w-add");
		keyStroke = KeyStroke.getKeyStroke("S");
		map.getActionMap().put("s-add", new AbstractAction("s-add") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < width; i++)
					map.add(new Autotile(i, height, "", true));
				
				height++;
				
				map.setPreferredSize(new Dimension(width * 32, height * 32));
				map.setSize(new Dimension(width * 32, height * 32));
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "s-add");
		keyStroke = KeyStroke.getKeyStroke("D");
		map.getActionMap().put("d-add", new AbstractAction("d-add") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < height; i++)
					map.add(new Autotile(width, i, "", true));
				
				width++;
				
				map.setPreferredSize(new Dimension(width * 32, height * 32));
				map.setSize(new Dimension(width * 32, height * 32));
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "d-add");
		keyStroke = KeyStroke.getKeyStroke("control S");
		map.getActionMap().put("save", new AbstractAction("save") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(new File(System.getProperty("user.dir")));
				jfc.setMultiSelectionEnabled(false);
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setFileFilter(new FileNameExtensionFilter("PNG-Bild (*.png)", "png"));
				if (jfc.showSaveDialog(FloorEditor.this) == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					if (!f.getName().endsWith(".png")) f = new File(f.getPath() + ".png");
					BufferedImage bi = new BufferedImage(width * 32, height * 32, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = (Graphics2D) bi.getGraphics();
					for (Component c : map.getComponents())
						if (c instanceof Autotile) g.drawImage(((ImageIcon) ((Autotile) c).getIcon()).getImage(), c.getX(), c.getY(), null);
					
					try {
						ImageIO.write(bi, "PNG", f);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "save");
		keyStroke = KeyStroke.getKeyStroke("B");
		map.getActionMap().put("border", new AbstractAction("border") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				border = !border;
				for (Component c : map.getComponents())
					((Autotile) c).gridBorder = border;
				
				map.repaint();
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "border");
		
		map.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				new Thread() {
					@Override
					public void run() {
						if (dragStart != null && dragEnd != null) {
							int x = Math.min(dragStart.x, dragEnd.x);
							int y = Math.min(dragStart.y, dragEnd.y);
							int xM = Math.max(dragStart.x, dragEnd.x) + 32;
							int yM = Math.max(dragStart.y, dragEnd.y) + 32;
							
							
							for (Component c : map.getComponents()) {
								if (c instanceof Autotile && c.getX() >= x && c.getX() < xM && c.getY() >= y && c.getY() < yM) {
									if (dragDelete) ((Autotile) c).tileset = "";
									else ((Autotile) c).tileset = selectedTile;
									
									((Autotile) c).updateNeighbors();
								}
							}
						}
						
						dragEnd = dragStart = null;
						dragDelete = false;
						map.repaint();
					}
				}.start();
			}
		});
		map.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (selectedTile == null) return;
				dragDelete = e.getModifiers() == 4;
				
				if (dragStart == null) dragStart = new Point((int) (e.getX() / 32f) * 32, (int) (e.getY() / 32f) * 32);
				else dragEnd = new Point((int) (e.getX() / 32f) * 32, (int) (e.getY() / 32f) * 32);
				map.repaint();
			}
		});
		
		width = 10;
		height = 10;
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				map.add(new Autotile(i, j, "", true));
		
		JScrollPane jsp2 = new JScrollPane(map, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp2.getVerticalScrollBar().setUnitIncrement(32);
		jsp2.getHorizontalScrollBar().setUnitIncrement(32);
		jsp2.setSize(new Dimension(1080, 720));
		cp.add(jsp2);
		
		setContentPane(cp);
	}
}
