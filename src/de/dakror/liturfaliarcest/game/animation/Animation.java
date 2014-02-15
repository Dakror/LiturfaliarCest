package de.dakror.liturfaliarcest.game.animation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import de.dakror.gamesetup.util.CSVReader;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;

/**
 * @author Dakror
 */
public class Animation
{
	private static HashMap<Integer, Animation> animations;
	
	public static void init()
	{
		animations = new HashMap<>();
		
		CSVReader csv = new CSVReader("/csv/anim.csv");
		String cell = "";
		Animation anim = null;
		while ((cell = csv.readNext()) != null)
		{
			switch (csv.getIndex())
			{
				case 0:
					if (anim != null) animations.put(anim.id, anim);
					anim = new Animation();
					anim.id = Integer.parseInt(cell);
					break;
				case 1:
					anim.file = cell;
					break;
				case 2:
					anim.rows = Integer.parseInt(cell);
					break;
				case 3:
					anim.cols = Integer.parseInt(cell);
					break;
			}
		}
	}
	
	public static Animation getAnimationForId(int id)
	{
		return animations.get(id);
	}
	
	public static Animation getAnimationInstance(int id)
	{
		return animations.get(id).clone();
	}
	
	private int id, rows, cols;
	private String file;
	
	public int width, height, index, speed, startTick;
	public boolean smooth;
	
	public Animation()
	{}
	
	public int getId()
	{
		return id;
	}
	
	public void init(int width, int height, boolean smooth)
	{
		this.width = width;
		this.height = height;
		this.smooth = smooth;
		index = 0;
		speed = 4;
		startTick = 0;
	}
	
	public void draw(int x, int y, Graphics2D g)
	{
		Helper.setRenderingHints(g, smooth);
		BufferedImage bi = Game.getImage("anim/" + file + ".png");
		Helper.drawImage(bi, x, y, width, height, bi.getWidth() / cols * (index % cols), bi.getHeight() / rows * (index / rows), bi.getWidth() / cols, bi.getHeight() / rows, g);
		Helper.setRenderingHints(g, !smooth);
	}
	
	public void update(int tick)
	{
		if (startTick == 0)
		{
			startTick = tick;
			return;
		}
		
		if ((tick - startTick) % speed == 0) index = (index + 1) % (cols * rows);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Animation)) return false;
		return ((Animation) obj).id == id;
	}
	
	@Override
	public Animation clone()
	{
		Animation a = new Animation();
		a.cols = cols;
		a.rows = rows;
		a.id = id;
		a.file = new String(file);
		return a;
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
}
