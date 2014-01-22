package de.dakror.liturfaliarcest.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;

/**
 * @author Dakror
 */
public class MapPanel extends JPanel implements MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	
	BufferedImage ground, above;
	
	int tx, ty;
	Point mouse;
	Point drag;
	Point dragPos;
	
	public MapPanel()
	{
		addMouseListener(this);
		addMouseMotionListener(this);
		setLayout(null);
	}
	
	public void openMap(File map)
	{
		try
		{
			File p = map.getParentFile();
			ground = ImageIO.read(new File(p, p.getName() + "-0.png"));
			if (new File(p, p.getName() + "-1.png").exists()) above = ImageIO.read(new File(p, p.getName() + "-1.png"));
			else above = null;
			
			JSONArray e = new JSONArray(Helper.getFileContent(map));
			for (int i = 0; i < e.length(); i++)
			{
				JSONObject en = e.getJSONObject(i);
				JSONObject o = Editor.currentEditor.entities.getJSONObject(en.getInt("i"));
				JLabel l = new JLabel(new ImageIcon(Game.getImage("tiles/" + o.getString("t")).getSubimage(o.getInt("x"), o.getInt("y"), o.getInt("w"), o.getInt("h"))));
				l.setPreferredSize(new Dimension(o.getInt("w"), o.getInt("h")));
				l.setName(en.getInt("i") + "");
				l.setBounds(en.getInt("x"), en.getInt("y"), o.getInt("w"), o.getInt("h"));
				addEntity(l);
			}
			
			setPreferredSize(new Dimension(ground.getWidth(), ground.getHeight()));
			getParent().getParent().revalidate();
			
			tx = ty = 0;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintChildren(Graphics g)
	{
		g.drawImage(ground, tx, ty, null);
		
		super.paintChildren(g);
		
		if (mouse != null && Editor.currentEditor.selectedEntity != null && Editor.currentEditor.map != null)
		{
			Image i = ((ImageIcon) Editor.currentEditor.selectedEntity.getIcon()).getImage();
			g.drawImage(i, mouse.x - i.getWidth(null) / 2, mouse.y - i.getHeight(null) / 2, null);
		}
		
		if (above != null) g.drawImage(above, tx, ty, null);
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		mouse = e.getPoint();
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
	
	public void addEntity(final JLabel l)
	{
		l.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				boolean d = drag == null;
				drag = null;
				dragPos = null;
				
				if (!d) return;
				
				if (!((LineBorder) l.getBorder()).getLineColor().equals(Color.red) && e.getButton() == MouseEvent.BUTTON1)
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
			public void mouseDragged(MouseEvent e)
			{
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
}
