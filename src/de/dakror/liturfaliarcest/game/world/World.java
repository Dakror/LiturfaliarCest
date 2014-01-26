package de.dakror.liturfaliarcest.game.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
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
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Compressor;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.Entity;
import de.dakror.liturfaliarcest.game.entity.EntityType;
import de.dakror.liturfaliarcest.game.entity.creature.NPC;
import de.dakror.liturfaliarcest.game.entity.object.Object;

public class World extends Layer
{
	public static final int TILE_SIZE = 64;
	
	public int x, y, width, height;
	
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
		
		Image img = Game.getImage("/maps/" + name + "/" + name + "-0.png");
		width = img.getWidth(null) / 32 * TILE_SIZE;
		height = img.getHeight(null) / 32 * TILE_SIZE;
		
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
					Entity entity = null;
					
					if (o.has("m") && o.getJSONObject("m").getBoolean("npc")) entity = new NPC(o.getInt("x") * (World.TILE_SIZE / 32), o.getInt("y") * (World.TILE_SIZE / 32), EntityType.entityTypes.get(o.getInt("i")), o.getJSONObject("m"));
					else entity = new Object(o.getInt("x") * (World.TILE_SIZE / 32), o.getInt("y") * (World.TILE_SIZE / 32), EntityType.entityTypes.get(o.getInt("i")));
					
					if (o.has("e")) entity.setEventFunctions(o.getJSONObject("e"));
					entity.uid = o.getInt("uid");
					addEntity(entity);
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
			
			g.drawImage(Game.getImage("/maps/" + name + "/" + name + "-0.png"), x, y, width, height, Game.w);
			
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
			
			g.drawImage(Game.getImage("/maps/" + name + "/" + name + "-1.png"), x, y, width, height, Game.w);
			
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
	
	public String getName()
	{
		return name;
	}
	
	public JSONObject getData() throws JSONException
	{
		JSONObject o = new JSONObject();
		o.put("n", name);
		JSONArray e = new JSONArray();
		for (Component c : components)
			e.put(((Entity) c).getData());
		o.put("e", e);
		return o;
	}
	
	public Area getBump()
	{
		return bump;
	}
}
