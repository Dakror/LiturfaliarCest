package de.dakror.liturfaliarcest.editor;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

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
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				FloorEditor.currentFloorEditor.selectedTile = Autotile.this.tileset;
			}
		});
		
		updateIcon();
	}
	
	public void updateIcon()
	{
		if (tileset == null) setIcon(null);
		if (map)
		{}
		else
		{
			setIcon(new ImageIcon(Game.getImage("autotiles/" + tileset).getSubimage(0, 0, 32, 32)));
		}
	}
}
