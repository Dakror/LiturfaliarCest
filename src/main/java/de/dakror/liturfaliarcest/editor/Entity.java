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

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.json.JSONObject;

import sun.misc.BASE64Decoder;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.animation.Animation;
import de.dakror.liturfaliarcest.game.item.Item;

public class Entity extends JLabel {
	private static final long serialVersionUID = 1L;
	
	public JSONObject e; // events
	public JSONObject m; // meta
	
	public int uid;
	
	Icon defaultIcon;
	
	public Entity(Icon i) {
		super(i);
		e = new JSONObject();
		m = new JSONObject();
		defaultIcon = i;
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Editor.currentEditor.mapPanel.getMouseMotionListeners()[0].mouseMoved(e);
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Editor.currentEditor.mapPanel.getMouseListeners()[0].mousePressed(e);
			}
		});
	}
	
	public void showPopupMenu(MouseEvent e) {
		JPopupMenu jpm = new JPopupMenu();
		jpm.add(new JMenuItem(new AbstractAction("UID kopieren") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(uid + ""), null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}));
		jpm.add(new JMenuItem(new AbstractAction("GUID kopieren") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(Editor.currentEditor.map.getParentFile().getName() + "$" + uid + ""), null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}));
		if (this.e.has("onEnter")) {
			try {
				String function = new String(new BASE64Decoder().decodeBuffer(this.e.getString("onEnter")));
				if (function.contains("teleportMap")) {
					// jpm.add(new JMenuItem());
					String[] teleports = function.split("teleportMap");
					for (int i = 1; i < teleports.length; i++) {
						String t = teleports[i].trim();
						if (!t.startsWith("(")) continue;
						
						String map = t.substring(t.indexOf("\"") + 1);
						map = map.substring(0, map.indexOf("\"")).trim();
						
						final String map2 = map;
						
						if (new File(Editor.currentEditor.map.getParentFile().getParentFile(), map + "/" + map + ".json").exists()) {
							jpm.add(new JMenuItem(new AbstractAction("Gehe zu Karte: " + map) {
								private static final long serialVersionUID = 1L;
								
								@Override
								public void actionPerformed(ActionEvent e) {
									Editor.currentEditor.map = new File(Editor.currentEditor.map.getParentFile().getParentFile(), map2 + "/" + map2 + ".json");
									Editor.currentEditor.openMap();
								}
							}));
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		jpm.show(this, e.getX(), e.getY());
	}
	
	public void setM(JSONObject m) {
		this.m = m;
		
		if (m.length() == 0) return;
		
		if (m.has("texture")) {
			try {
				BufferedImage bi = Game.getImage(m.getString("texture"));
				if (bi != null) {
					setIcon(new ImageIcon(m.has("frame") || m.has("dir") ? bi.getSubimage((m.has("frame") ? m.getInt("frame") : 0) * bi.getWidth() / 4, (m.has("dir") ? m.getInt("dir") : 0) * bi.getHeight() / 4, bi.getWidth() / 4, bi.getHeight() / 4) : bi.getSubimage(0, 0, bi.getWidth() / 4, bi.getHeight() / 4)));
					setBounds(getX(), getY(), bi.getWidth() / 4, bi.getHeight() / 4);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (m.has("itemID")) {
			try {
				setIcon(Item.getItemForId(m.getInt("itemID")).getIcon(32));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (m.has("animID")) {
			try {
				Animation a = Animation.getAnimationForId(m.getInt("animID"));
				setBounds(getX(), getY(), (m.has("width") ? m.getInt("width") : a.getDefaultWidth()) / 2, (m.has("height") ? m.getInt("height") : a.getDefaultHeight()) / 2);
				setIcon(a.getIcon(getWidth(), getHeight()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			setIcon(defaultIcon);
			if (getName().equals("0")) setBounds(getX(), getY(), 32, 32);
		}
	}
}
