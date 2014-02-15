package de.dakror.liturfaliarcest.editor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.json.JSONObject;

import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.item.Item;

public class Entity extends JLabel
{
	private static final long serialVersionUID = 1L;
	
	public JSONObject e; // events
	public JSONObject m; // meta
	
	public int uid;
	
	Icon defaultIcon;
	
	public Entity(Icon i)
	{
		super(i);
		e = new JSONObject();
		m = new JSONObject();
		defaultIcon = i;
		addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				Editor.currentEditor.mapPanel.getMouseMotionListeners()[0].mouseMoved(e);
			}
		});
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				Editor.currentEditor.mapPanel.getMouseListeners()[0].mousePressed(e);
			}
		});
	}
	
	public void setM(JSONObject m)
	{
		this.m = m;
		
		if (m.length() == 0) return;
		
		if (m.has("texture"))
		{
			try
			{
				BufferedImage bi = Game.getImage(m.getString("texture"));
				if (bi != null)
				{
					setIcon(new ImageIcon(m.has("frame") || m.has("dir") ? bi.getSubimage((m.has("frame") ? m.getInt("frame") : 0) * bi.getWidth() / 4, (m.has("dir") ? m.getInt("dir") : 0) * bi.getHeight() / 4, bi.getWidth() / 4, bi.getHeight() / 4) : bi.getSubimage(0, 0, bi.getWidth() / 4, bi.getHeight() / 4)));
					setBounds(getX(), getY(), bi.getWidth() / 4, bi.getHeight() / 4);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (m.has("itemID"))
		{
			try
			{
				setIcon(Item.getItemForId(m.getInt("itemID")).getIcon(32));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			setIcon(defaultIcon);
			if (getName().equals("0")) setBounds(getX(), getY(), 32, 32);
		}
	}
}
