package de.dakror.liturfaliarcest.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;

/**
 * @author Dakror
 */
public class Autotile extends JLabel
{
	private static final long serialVersionUID = 1L;
	
	public String tileset;
	boolean map;
	
	public Autotile(int x, int y, String tileset, boolean map)
	{
		if (map) setBounds(x * 32, y * 32, 32, 32);
		setPreferredSize(new Dimension(32, 32));
		this.tileset = tileset;
		this.map = map;
		
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (!Autotile.this.map && e.getButton() == MouseEvent.BUTTON1) FloorEditor.currentFloorEditor.selectedTile = Autotile.this.tileset;
				if (Autotile.this.map && e.getButton() == MouseEvent.BUTTON1 && FloorEditor.currentFloorEditor.selectedTile != null)
				{
					Autotile.this.tileset = FloorEditor.currentFloorEditor.selectedTile;
					updateIcon();
					updateMap();
				}
			}
		});
		
		updateIcon();
	}
	
	public void updateMap()
	{
		JPanel map = FloorEditor.currentFloorEditor.map;
		for (Component c : map.getComponents())
			if (c instanceof Autotile) ((Autotile) c).updateIcon();
	}
	
	public void updateIcon()
	{
		if (tileset == "")
		{
			setIcon(null);
			return;
		}
		if (map)
		{
			JPanel map = FloorEditor.currentFloorEditor.map;
			
			/*
			 * 0 -- 1 -- 2
			 * | .. | .. |
			 * 3 -- + -- 4
			 * | .. | .. |
			 * 5 -- 6 -- 7
			 */
			boolean[] dirs = { false, false, false, false, false, false, false, false };
			Point[] points = { new Point(-1, -1), new Point(0, -1), new Point(1, -1), new Point(-1, 0), new Point(1, 0), new Point(-1, 1), new Point(0, 1), new Point(1, 1) };
			for (int i = 0; i < points.length; i++)
			{
				Component t = map.getComponentAt(getX() + points[i].x * 32, getY() + points[i].y * 32);
				if (t != null && t instanceof Autotile) dirs[i] = ((Autotile) t).tileset.equals(tileset);
			}
			
			BufferedImage bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) bi.getGraphics();
			BufferedImage tex = Game.getImage("autotiles/" + tileset);
			if (!Arrays.toString(dirs).contains("false")) Helper.drawImage2(tex, 0, 0, 32, 32, 32, 64, 32, 32, g);
			else if (!dirs[1] && !dirs[3] && !dirs[4] && !dirs[6]) Helper.drawImage2(tex, 0, 0, 32, 32, 0, 0, 32, 32, g);
			else
			{
				int x = 1, y = 2;
				if (!dirs[1]) y--;
				if (!dirs[3]) x--;
				if (!dirs[4]) x++;
				if (!dirs[6]) y++;
				Helper.drawImage2(tex, 0, 0, 32, 32, x * 32, y * 32, 32, 32, g);
				if (!dirs[0] && dirs[1] && dirs[3]) Helper.drawImage2(tex, 0, 0, 16, 16, 64, 0, 16, 16, g);
				if (!dirs[2] && dirs[1] && dirs[4]) Helper.drawImage2(tex, 16, 0, 16, 16, 80, 0, 16, 16, g);
				if (!dirs[5] && dirs[3] && dirs[6]) Helper.drawImage2(tex, 0, 16, 16, 16, 64, 16, 16, 16, g);
				if (!dirs[7] && dirs[4] && dirs[6]) Helper.drawImage2(tex, 16, 16, 16, 16, 80, 16, 16, 16, g);
				
				if (dirs[3] && !dirs[1] && !dirs[6] && !dirs[4])
				{
					Helper.drawImage2(tex, 0, 0, 32, 16, 64, 32, 32, 16, g);
					Helper.drawImage2(tex, 0, 16, 32, 16, 64, 112, 32, 16, g);
				}
				if (dirs[4] && !dirs[1] && !dirs[6] && !dirs[3])
				{
					Helper.drawImage2(tex, 0, 0, 32, 16, 0, 32, 32, 16, g);
					Helper.drawImage2(tex, 0, 16, 32, 16, 0, 112, 32, 16, g);
				}
				if (dirs[1] && !dirs[3] && !dirs[4] && !dirs[6])
				{
					Helper.drawImage2(tex, 0, 0, 16, 32, 0, 96, 16, 32, g);
					Helper.drawImage2(tex, 16, 0, 16, 32, 80, 96, 16, 32, g);
				}
				if (dirs[6] && !dirs[3] && !dirs[4] && !dirs[1])
				{
					Helper.drawImage2(tex, 0, 0, 16, 32, 0, 32, 16, 32, g);
					Helper.drawImage2(tex, 16, 0, 16, 32, 80, 32, 16, 32, g);
				}
				if (dirs[6] && dirs[1] && !dirs[3] && !dirs[4])
				{
					Helper.drawImage2(tex, 0, 0, 16, 32, 0, 64, 16, 32, g);
					Helper.drawImage2(tex, 16, 0, 16, 32, 80, 64, 16, 32, g);
				}
				if (dirs[3] && dirs[4] && !dirs[1] && !dirs[6])
				{
					Helper.drawImage2(tex, 0, 0, 32, 16, 32, 32, 32, 16, g);
					Helper.drawImage2(tex, 0, 16, 32, 16, 32, 112, 32, 16, g);
				}
			}
			
			setIcon(new ImageIcon(bi));
		}
		else setIcon(new ImageIcon(Game.getImage("autotiles/" + tileset).getSubimage(0, 0, 32, 32)));
		
		repaint();
	}
}
