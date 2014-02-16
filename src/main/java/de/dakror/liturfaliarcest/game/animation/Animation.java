package de.dakror.liturfaliarcest.game.animation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.dakror.liturfaliarcest.game.Game;
import de.dakror.gamesetup.util.CSVReader;
import de.dakror.gamesetup.util.Helper;

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
		csv.readRow();
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
		
		animations.put(anim.id, anim);
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
	private boolean endless, done;
	
	public int width, height, index, speed, startTick;
	public boolean smooth;
	
	public Animation()
	{}
	
	public int getId()
	{
		return id;
	}
	
	public void init(int width, int height, boolean smooth, boolean endless)
	{
		this.width = width;
		this.height = height;
		this.smooth = smooth;
		this.endless = endless;
		index = 0;
		speed = 2;
		startTick = 0;
		done = false;
	}
	
	public void draw(int x, int y, Graphics2D g)
	{
		if (done) return;
		
		Helper.setRenderingHints(g, smooth);
		BufferedImage bi = Game.getImage("anim/" + file + ".png");
		Helper.drawImage(bi, x, y, width, height, bi.getWidth() / cols * (index % cols), bi.getHeight() / rows * (index % rows), bi.getWidth() / cols, bi.getHeight() / rows, g);
		Helper.setRenderingHints(g, !smooth);
	}
	
	public void update(int tick)
	{
		if (startTick == 0) startTick = tick;
		
		if (tick > startTick && (tick - startTick) % speed == 0)
		{
			index = (index + 1) % (cols * rows);
			if (!endless && index == 0) done = true;
		}
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
	
	public int getDefaultWidth()
	{
		BufferedImage bi = Game.getImage("anim/" + file + ".png");
		return bi.getWidth() / cols;
	}
	
	public int getDefaultHeight()
	{
		BufferedImage bi = Game.getImage("anim/" + file + ".png");
		return bi.getHeight() / rows;
	}
	
	public boolean isDone()
	{
		return done;
	}
	
	public Icon getIcon(int width, int height)
	{
		BufferedImage i = Game.getImage("anim/" + file + ".png");
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bi.getGraphics().drawImage(i, 0, 0, width, height, 0, 0, i.getWidth() / cols, i.getHeight() / rows, null);
		
		return new ImageIcon(bi);
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
}
