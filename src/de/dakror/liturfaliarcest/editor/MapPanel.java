package de.dakror.liturfaliarcest.editor;

import java.awt.Color;
import java.awt.Component;
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
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

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
	
	public void openMap(File mapDir)
	{
		try
		{
			File p = mapDir.getParentFile();
			ground = ImageIO.read(new File(p, p.getName() + "-0.png"));
			if (new File(p, p.getName() + "-1.png").exists()) above = ImageIO.read(new File(p, p.getName() + "-1.png"));
			else above = null;
			
			tx = ty = 0;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintChildren(Graphics g)
	{
		g.drawImage(ground, tx, ty, null);
		
		super.paintChildren(g);
		
		if (above != null) g.drawImage(above, tx, ty, null);
		
		if (mouse != null && Editor.currentEditor.selectedEntity != null && Editor.currentEditor.map != null)
		{
			Image i = ((ImageIcon) Editor.currentEditor.selectedEntity.getIcon()).getImage();
			g.drawImage(i, mouse.x - i.getWidth(null) / 2, mouse.y - i.getHeight(null) / 2, null);
		}
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
		if (mouse != null && Editor.currentEditor.map != null)
		{
			if (e.getButton() == MouseEvent.BUTTON1 && Editor.currentEditor.selectedEntity != null)
			{
				final JLabel l = Editor.currentEditor.selectedEntity;
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
					
					@Override
					public void mousePressed(MouseEvent e)
					{	
						
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
				l.setBounds(mouse.x - l.getPreferredSize().width / 2, mouse.y - l.getPreferredSize().height / 2, l.getPreferredSize().width, l.getPreferredSize().height);
				add(l);
				
				Editor.currentEditor.selectedEntity = null;
				Editor.currentEditor.selectedEntityOriginal.setBorder(null);
			}
		}
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
