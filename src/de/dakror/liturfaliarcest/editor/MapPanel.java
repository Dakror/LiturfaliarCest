package de.dakror.liturfaliarcest.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JarManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.util.RhinoJavaScriptLanguageSupport;

/**
 * @author Dakror
 */
public class MapPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	
	BufferedImage ground, above;
	
	Point mouse;
	Point drag;
	Point dragPos;
	
	public MapPanel()
	{
		addMouseListener(this);
		addMouseMotionListener(this);
		setLayout(null);
	}
	
	public void openMap()
	{
		try
		{
			removeAll();
			
			File p = Editor.currentEditor.map.getParentFile();
			ground = ImageIO.read(new File(p, p.getName() + "-0.png"));
			if (new File(p, p.getName() + "-1.png").exists()) above = ImageIO.read(new File(p, p.getName() + "-1.png"));
			else above = null;
			
			JSONArray e = new JSONArray(Helper.getFileContent(Editor.currentEditor.map));
			for (int i = 0; i < e.length(); i++)
			{
				JSONObject en = e.getJSONObject(i);
				JSONObject o = Editor.currentEditor.entities.getJSONObject(en.getInt("i"));
				
				BufferedImage img = (!o.getString("t").equals("black")) ? Game.getImage("tiles/" + o.getString("t")).getSubimage(o.getInt("x"), o.getInt("y"), o.getInt("w"), o.getInt("h")) : new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
				if (o.getString("t").equals("black"))
				{
					Graphics2D g = (Graphics2D) img.getGraphics();
					g.setColor(Color.black);
					Helper.drawHorizontallyCenteredString("E", 32, 32, g, 44);
				}
				Entity l = new Entity(new ImageIcon(img));
				l.e = en.has("e") ? en.getJSONObject("e") : new JSONObject();
				l.m = en.has("m") ? en.getJSONObject("m") : new JSONObject();
				l.setPreferredSize(new Dimension(o.getInt("w"), o.getInt("h")));
				l.setName(en.getInt("i") + "");
				l.setBounds(en.getInt("x"), en.getInt("y"), o.getInt("w"), o.getInt("h"));
				addEntity(l);
			}
			
			setPreferredSize(new Dimension(ground.getWidth(), ground.getHeight()));
			getParent().getParent().revalidate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintChildren(Graphics g)
	{
		g.drawImage(ground, 0, 0, null);
		
		Component[] c = getComponents();
		Arrays.sort(c, new Comparator<Component>()
		{
			@Override
			public int compare(Component o1, Component o2)
			{
				return Integer.compare(o2.getY(), o1.getY());
			}
		});
		
		for (int i = 0; i < c.length; i++)
			setComponentZOrder(c[i], i);
		
		super.paintChildren(g);
		
		if (mouse != null && Editor.currentEditor.selectedEntity != null && Editor.currentEditor.map != null)
		{
			Image i = ((ImageIcon) Editor.currentEditor.selectedEntity.getIcon()).getImage();
			g.drawImage(i, mouse.x - i.getWidth(null) / 2, mouse.y - i.getHeight(null) / 2, null);
		}
		
		if (above != null) g.drawImage(above, 0, 0, null);
		
		if (mouse != null && Editor.currentEditor.map != null) Helper.drawString(mouse.x + " (" + (int) Math.floor(mouse.x / 32f) + ") x " + mouse.y + " (" + (int) Math.floor(mouse.y / 32f) + ")", getX(), 16 - getY(), (Graphics2D) g, 15);
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		mouse = e.getPoint();
		if (e.isControlDown() && Editor.currentEditor.selectedEntity != null)
		{
			Image i = ((ImageIcon) Editor.currentEditor.selectedEntity.getIcon()).getImage();
			
			mouse.x = Helper.round(mouse.x - i.getWidth(null) / 2, 32) + i.getWidth(null) / 2;
			mouse.y = Helper.round(mouse.y - i.getHeight(null) / 2, 32) + i.getHeight(null) / 2;
		}
		repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (Editor.currentEditor.selectedEntity != null && e.getButton() == MouseEvent.BUTTON3)
		{
			Editor.currentEditor.selectedEntity = null;
			Editor.currentEditor.selectedEntityOriginal.setBorder(null);
			mouse = null;
			return;
		}
		
		if (mouse != null && Editor.currentEditor.map != null)
		{
			if (e.getButton() == MouseEvent.BUTTON1 && Editor.currentEditor.selectedEntity != null)
			{
				addEntity(Editor.currentEditor.selectedEntity);
				
				Editor.currentEditor.selectedEntity = null;
				Editor.currentEditor.selectedEntityOriginal.setBorder(null);
			}
		}
	}
	
	public void addEntity(final Entity l)
	{
		l.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.getClickCount() == 2) editEntityEvents(l);
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				boolean d = drag == null;
				drag = null;
				dragPos = null;
				
				if (!d) return;
				
				if ((l.getBorder() == null || !((LineBorder) l.getBorder()).getLineColor().equals(Color.red)) && e.getButton() == MouseEvent.BUTTON1)
				{
					for (Component c : MapPanel.this.getComponents())
						((JLabel) c).setBorder(null);
					
					l.setBorder(BorderFactory.createLineBorder(Color.red));
				}
				else
				{
					if (e.getButton() == MouseEvent.BUTTON3)
					{
						MapPanel.this.remove(l);
						repaint();
					}
					else l.setBorder(BorderFactory.createLineBorder(Color.black));
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (l.getBorder() == null || !((LineBorder) l.getBorder()).getLineColor().equals(Color.red)) l.setBorder(BorderFactory.createLineBorder(Color.black));
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				if (l.getBorder() == null || !((LineBorder) l.getBorder()).getLineColor().equals(Color.red)) l.setBorder(null);
			}
		});
		l.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				mouse = new Point(e.getX() + l.getX(), e.getY() + l.getY());
				repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				if (e.getModifiers() != 16) return;
				
				if (drag == null)
				{
					dragPos = l.getLocation();
					drag = e.getPoint();
				}
				
				l.setLocation(l.getX() + e.getX() - drag.x, l.getY() + e.getY() - drag.y);
			}
		});
		if (mouse != null) l.setBounds(mouse.x - l.getPreferredSize().width / 2, mouse.y - l.getPreferredSize().height / 2, l.getPreferredSize().width, l.getPreferredSize().height);
		l.setToolTipText("X: " + l.getX() + ", Y: " + l.getY());
		add(l);
	}
	
	public void editEntityEvents(final Entity l)
	{
		try
		{
			final JDialog d = new JDialog(Editor.currentEditor, "Entity Events bearbeiten", true);
			d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			d.setSize(400, 350);
			d.setLocation((Editor.currentEditor.getX() + Editor.currentEditor.getWidth() - 400) / 2, (Editor.currentEditor.getY() + Editor.currentEditor.getHeight() - 350) / 2);
			d.setResizable(false);
			
			final RSyntaxTextArea a = new RSyntaxTextArea("e: " + l.e.toString(2).replaceAll("([^\\\\])(\")", "$1").replace("\\\"", "\"").replace(";", ";\n").replace("{", "{\n").replace("}", "}\n") + ",/*END*/\nm: " + fmt(l.m));
			LanguageSupportFactory lsf = LanguageSupportFactory.get();
			lsf.register(a);
			a.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
			configureLanguageSupport(a);
			
			a.setCodeFoldingEnabled(true);
			a.setAutoIndentEnabled(true);
			a.setTabsEmulated(true);
			a.setAnimateBracketMatching(false);
			a.setHighlightCurrentLine(false);
			a.setTabSize(2);
			a.setClearWhitespaceLinesEnabled(true);
			
			RTextScrollPane tsp = new RTextScrollPane(a);
			tsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			d.setContentPane(tsp);
			
			d.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					try
					{
						String t = a.getText();
						
						// -- events -- //
						String evts = t.substring(t.indexOf("{"), t.indexOf(",/*END*/"));
						evts = evts.replace("\"", "\\\"").replace("function", "\"function").replace("}", "}\"").replace("\n", "").replace("  ", " ");
						evts = evts.substring(0, evts.length() - 1);
						
						JSONObject arr = new JSONObject(evts);
						if (JSONObject.getNames(arr) != null)
						{
							for (String k : JSONObject.getNames(arr))
								if (!arr.getString(k).startsWith("function")) throw new JSONException("No valid function declaration at event '" + k + "'");
						}
						l.e = arr;
						
						// -- meta -- //
						String meta = t.substring(t.indexOf("m: ") + "m: ".length());
						JSONObject o = new JSONObject(meta);
						l.setM(o);
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
						int r = JOptionPane.showConfirmDialog(d, "Fehlerhafte Eingabe:\n" + e1.getMessage() + "\nTrotzdem schließen?", "Fehler!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
						if (r != JOptionPane.OK_OPTION) return;
					}
					
					d.dispose();
				}
			});
			
			d.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void configureLanguageSupport(RSyntaxTextArea textArea) throws IOException
	{
		RhinoJavaScriptLanguageSupport support1 = new RhinoJavaScriptLanguageSupport();
		JarManager jarManager = support1.getJarManager();
		jarManager.addCurrentJreClassFileSource();
		support1.install(textArea);
	}
	
	private String fmt(JSONObject m) throws JSONException
	{
		return m.toString(2).replaceAll("(\n)( {0,})(\")", "\n$2").replace("{\"", "{").replace("\":", ":").replace("{", "{\n").replace("}", "\n}\n").replace("\n\n", "\n");
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		mouse = null;
		repaint();
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_F5 && Editor.currentEditor.map != null)
		{
			try
			{
				File p = Editor.currentEditor.map.getParentFile();
				ground = ImageIO.read(new File(p, p.getName() + "-0.png"));
				if (new File(p, p.getName() + "-1.png").exists()) above = ImageIO.read(new File(p, p.getName() + "-1.png"));
				else above = null;
				
				repaint();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{}
}
