package de.dakror.liturfaliarcest.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.world.World;

/**
 * @author Dakror
 */
public class MapPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	
	BufferedImage ground, above;
	
	Point mouse;
	Point drag;
	Point dragPos;
	boolean loadingDone;
	
	public MapPanel() {
		addMouseListener(this);
		addMouseMotionListener(this);
		
		KeyStroke keyStroke = KeyStroke.getKeyStroke("F5");
		getActionMap().put("refresh", new AbstractAction("refresh") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Editor.currentEditor.map != null) {
					try {
						File p = Editor.currentEditor.map.getParentFile();
						ground = ImageIO.read(new File(p, p.getName() + "-0.png"));
						if (new File(p, p.getName() + "-1.png").exists()) above = ImageIO.read(new File(p, p.getName() + "-1.png"));
						else above = null;
						
						repaint();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "refresh");
		
		KeyStroke keyStroke1 = KeyStroke.getKeyStroke("control C");
		getActionMap().put("copy", new AbstractAction("copy") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Editor.currentEditor.map != null) {
					try {
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("/* " + (mouse.x * World.TILE_SIZE / 32) + " (" + (int) Math.floor(mouse.x / 32f) + ") x " + (mouse.y * World.TILE_SIZE / 32) + " (" + (int) Math.floor(mouse.y / 32f) + ") */"), null);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke1, "copy");
		
		setLayout(null);
	}
	
	public void openMap() {
		loadingDone = false;
		try {
			setLayout(null);
			removeAll();
			revalidate();
			
			File p = Editor.currentEditor.map.getParentFile();
			ground = ImageIO.read(new File(p, p.getName() + "-0.png"));
			if (new File(p, p.getName() + "-1.png").exists()) above = ImageIO.read(new File(p, p.getName() + "-1.png"));
			else above = null;
			
			JSONArray e = new JSONArray(Helper.getFileContent(Editor.currentEditor.map));
			for (int i = 0; i < e.length(); i++) {
				JSONObject en = e.getJSONObject(i);
				JSONObject o = Editor.currentEditor.entities.getJSONObject(en.getInt("i"));
				
				BufferedImage img = (!o.getString("t").equals("black")) ? Game.getImage("tiles/" + o.getString("t")).getSubimage(o.getInt("x"), o.getInt("y"), o.getInt("w"), o.getInt("h")) : new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
				if (o.getString("t").equals("black")) {
					Graphics2D g = (Graphics2D) img.getGraphics();
					g.setColor(Color.black);
					Helper.drawHorizontallyCenteredString("E", 32, 32, g, 44);
				}
				Entity l = new Entity(new ImageIcon(img));
				l.e = en.has("e") ? en.getJSONObject("e") : new JSONObject();
				l.setName(en.getInt("i") + "");
				l.setPreferredSize(new Dimension(o.getInt("w"), o.getInt("h")));
				l.setBounds(en.getInt("x"), en.getInt("y"), o.getInt("w"), o.getInt("h"));
				l.setM(en.has("m") ? en.getJSONObject("m") : new JSONObject());
				l.uid = en.has("uid") ? en.getInt("uid") : createUID();
				
				addEntity(l);
			}
			
			setPreferredSize(new Dimension(ground.getWidth(), ground.getHeight()));
			getParent().getParent().revalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintChildren(Graphics g) {
		g.drawImage(ground, 0, 0, null);
		
		Component[] c = getComponents();
		Arrays.sort(c, new Comparator<Component>() {
			@Override
			public int compare(Component o1, Component o2) {
				return Integer.compare(o2.getY(), o1.getY());
			}
		});
		
		for (int i = 0; i < c.length; i++)
			setComponentZOrder(c[i], i);
		
		super.paintChildren(g);
		
		if (mouse != null && Editor.currentEditor.selectedEntity != null && Editor.currentEditor.map != null) {
			Image i = ((ImageIcon) Editor.currentEditor.selectedEntity.getIcon()).getImage();
			g.drawImage(i, mouse.x - i.getWidth(null) / 2, mouse.y - i.getHeight(null) / 2, null);
		}
		
		if (above != null) g.drawImage(above, 0, 0, null);
		
		if (mouse != null && Editor.currentEditor.map != null) Helper.drawString((mouse.x * World.TILE_SIZE / 32) + " (" + (int) Math.floor(mouse.x / 32f) + ") x " + (mouse.y * World.TILE_SIZE / 32) + " (" + (int) Math.floor(mouse.y / 32f) + ")", -getX(), 16 - getY(), (Graphics2D) g, 15);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();
		if (e.isControlDown() && Editor.currentEditor.selectedEntity != null) {
			Image i = ((ImageIcon) Editor.currentEditor.selectedEntity.getIcon()).getImage();
			
			mouse.x = Helper.round(mouse.x - i.getWidth(null) / 2, 32) + i.getWidth(null) / 2;
			mouse.y = Helper.round(mouse.y - i.getHeight(null) / 2, 32) + i.getHeight(null) / 2;
		}
		repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (Editor.currentEditor.selectedEntity != null && e.getButton() == MouseEvent.BUTTON3) {
			Editor.currentEditor.selectedEntity = null;
			Editor.currentEditor.selectedEntityOriginal.setBorder(null);
			mouse = null;
			return;
		}
		
		if (mouse != null && Editor.currentEditor.map != null) {
			if (e.getButton() == MouseEvent.BUTTON1 && Editor.currentEditor.selectedEntity != null) {
				Editor.currentEditor.selectedEntity.uid = createUID();
				addEntity(Editor.currentEditor.selectedEntity);
				
				Editor.currentEditor.selectedEntity = null;
				Editor.currentEditor.selectedEntityOriginal.setBorder(null);
			}
		}
	}
	
	public void addEntity(final Entity l) {
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) editEntityEvents(l);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				boolean d = drag == null;
				drag = null;
				dragPos = null;
				
				if (!d) return;
				
				if ((l.getBorder() == null || !((LineBorder) l.getBorder()).getLineColor().equals(Color.red)) && e.getButton() == MouseEvent.BUTTON1) {
					for (Component c : MapPanel.this.getComponents())
						((JLabel) c).setBorder(null);
					
					l.setBorder(BorderFactory.createLineBorder(Color.red));
				} else {
					if (e.getButton() == MouseEvent.BUTTON3) {
						MapPanel.this.remove(l);
						repaint();
					} else if (e.getButton() == MouseEvent.BUTTON2) {
						l.showPopupMenu(e);
					} else l.setBorder(BorderFactory.createLineBorder(Color.black));
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if (l.getBorder() == null || !((LineBorder) l.getBorder()).getLineColor().equals(Color.red)) l.setBorder(BorderFactory.createLineBorder(Color.black));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				if (l.getBorder() == null || !((LineBorder) l.getBorder()).getLineColor().equals(Color.red)) l.setBorder(null);
			}
		});
		l.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouse = new Point(e.getX() + l.getX(), e.getY() + l.getY());
				repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (e.getModifiers() != 16 && e.getModifiers() != 18) return;
				
				if (drag == null) {
					dragPos = l.getLocation();
					drag = e.getPoint();
				}
				
				if (e.isControlDown()) l.setLocation(Helper.round(l.getX() + e.getX() - drag.x, 32), Helper.round(l.getY() + e.getY() - drag.y, 32));
				else l.setLocation(l.getX() + e.getX() - drag.x, l.getY() + e.getY() - drag.y);
				
				l.setToolTipText("X: " + (l.getX() * World.TILE_SIZE / 32) + ", Y: " + (l.getY() * World.TILE_SIZE / 32));
			}
		});
		if (mouse != null && loadingDone) l.setBounds(mouse.x - l.getPreferredSize().width / 2, mouse.y - l.getPreferredSize().height / 2, l.getPreferredSize().width, l.getPreferredSize().height);
		l.setToolTipText("X: " + (l.getX() * World.TILE_SIZE / 32) + ", Y: " + (l.getY() * World.TILE_SIZE / 32));
		add(l);
	}
	
	public int createUID() {
		int uid = (int) (Math.random() * (Integer.MAX_VALUE - 1)) + 1;
		for (Component c : getComponents()) {
			if (((Entity) c).uid == uid) {
				return createUID();
			}
		}
		
		return uid;
	}
	
	public void editEntityEvents(final Entity l) {
		try {
			new EntityEditor(l);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		loadingDone = true;
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {
		mouse = null;
		repaint();
	}
}
