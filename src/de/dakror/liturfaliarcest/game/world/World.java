package de.dakror.liturfaliarcest.game.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Compressor;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.Entity;
import de.dakror.liturfaliarcest.game.entity.object.Object;
import de.dakror.liturfaliarcest.game.entity.object.ObjectType;

public class World extends Layer
{
	public static final int TILE_SIZE = 64;
	
	public int x, y;
	
	String name;
	Area bump;
	
	boolean groundLayer, aboveLayer;
	
	public World(String name)
	{
		this.name = name;
		x = y = 0;
		
		Game.worlds.put(name, this);
	}
	
	@Override
	public void init()
	{
		groundLayer = Game.getImage("/maps/" + name + "/" + name + "-0.png") != null;
		aboveLayer = Game.getImage("/maps/" + name + "/" + name + "-1.png") != null;
		
		try
		{
			if (getClass().getResource("/maps/" + name + "/" + name + ".bump") != null)
			{
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Compressor.decompress(Compressor.getURLContentAsByteArray(getClass().getResource("/maps/" + name + "/" + name + ".bump")))));
				Path2D p = (Path2D) ois.readObject();
				bump = new Area(AffineTransform.getScaleInstance(TILE_SIZE / 32, TILE_SIZE / 32).createTransformedShape(new Area(p)));
			}
			else
			{
				int size = 4;
				
				BufferedImage bumpImage = Game.getImage("/maps/" + name + "/" + name + "-2.png");
				bumpImage = Helper.toBufferedImage(bumpImage.getScaledInstance(bumpImage.getWidth() / 32 * TILE_SIZE, bumpImage.getHeight() / 32 * TILE_SIZE, BufferedImage.SCALE_FAST));
				
				bump = new Area();
				for (int i = 0; i < bumpImage.getWidth(); i += size)
					for (int j = 0; j < bumpImage.getHeight(); j += size)
						if (new Color(bumpImage.getRGB(i, j)).equals(Color.white)) bump.add(new Area(new Rectangle(i, j, size, size)));
			}
			
			if (getClass().getResource("/maps/" + name + "/" + name + ".ent") != null)
			{
				JSONArray e = new JSONArray(Helper.getURLContent(getClass().getResource("/maps/" + name + "/" + name + ".ent")));
				for (int i = 0; i < e.length(); i++)
				{
					JSONObject o = e.getJSONObject(i);
					Object obj = new Object(o.getInt("x"), o.getInt("y"), ObjectType.objectTypes.get(o.getInt("i")));
					if (o.has("e")) obj.setEventFunctions(o.getJSONObject("e"));
					addEntity(obj);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (groundLayer)
		{
			Helper.setRenderingHints(g, false);
			
			BufferedImage img = Game.getImage("/maps/" + name + "/" + name + "-0.png");
			g.drawImage(img, x, y, img.getWidth() / 32 * TILE_SIZE, img.getHeight() / 32 * TILE_SIZE, Game.w);
			
			Helper.setRenderingHints(g, true);
		}
		
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.translate(x, y);
		g.setTransform(at);
		
		drawComponents(g);
		
		g.setTransform(old);
		
		if (aboveLayer)
		{
			Helper.setRenderingHints(g, false);
			
			BufferedImage img = Game.getImage("/maps/" + name + "/" + name + "-1.png");
			g.drawImage(img, x, y, img.getWidth() / 32 * TILE_SIZE, img.getHeight() / 32 * TILE_SIZE, Game.w);
			
			Helper.setRenderingHints(g, true);
		}
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		
		ArrayList<Component> c = new ArrayList<>(components);
		Collections.sort(c, new Comparator<Component>()
		{
			@Override
			public int compare(Component o1, Component o2)
			{
				return Integer.compare(o1.getY() + ((Entity) o1).bumpY + ((Entity) o1).bumpHeight, o2.getY() + ((Entity) o2).bumpY + ((Entity) o2).bumpHeight);
			}
		});
		components = new CopyOnWriteArrayList<>(c);
	}
	
	public void addEntity(Entity e)
	{
		components.add(e);
	}
	
	public Area getBump()
	{
		return bump;
	}
}
