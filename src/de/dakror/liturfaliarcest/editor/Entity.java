package de.dakror.liturfaliarcest.editor;

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
				setIcon(Item.items.get(m.getInt("itemID")).getIcon(32));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			setIcon(defaultIcon);
			setBounds(getX(), getY(), 32, 32);
		}
	}
}
