package de.dakror.liturfaliarcest.game.entity;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Vector;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.creature.Player;
import de.dakror.liturfaliarcest.settings.Attributes;
import de.dakror.liturfaliarcest.settings.Attributes.Attribute;
import de.dakror.liturfaliarcest.settings.Inventory;
import de.dakror.liturfaliarcest.util.Assistant;
import de.dakror.liturfaliarcest.util.JSInvoker;

/**
 * @author Dakror
 */
public abstract class Entity extends ClickableComponent
{
	protected Attributes attr;
	protected Inventory inv;
	protected Vector pos, target, spawn;
	
	public int uid;
	
	public float alpha;
	protected boolean dead, frozen;
	
	public int bumpX, bumpY, bumpWidth, bumpHeight;
	
	protected JSONObject eventFunctions;
	
	public Entity(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		pos = new Vector(x, y);
		spawn = new Vector(x, y);
		alpha = 1;
		
		attr = new Attributes();
		
		eventFunctions = new JSONObject();
		dead = false;
		frozen = false;
		
		addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				if (Entity.this instanceof Player) return;
				
				Game.player.setClickTarget(Entity.this);
			}
		});
	}
	
	public void move()
	{
		if (target == null || pos.equals(target) || attr.get(Attribute.SPEED) == 0) return;
		
		Vector distance = target.clone().sub(pos);
		if (distance.getLength() >= attr.get(Attribute.SPEED)) distance.setLength(attr.get(Attribute.SPEED));
		
		if (isFree(distance.x, distance.y)) pos.add(distance);
		else target = pos;
		
		checkForOnEnterEvent();
		
		if (pos.equals(target)) onReachTarget();
	}
	
	@Override
	public void update(int tick)
	{
		if (!frozen) move();
		tick(tick);
		
		x = (int) pos.x;
		y = (int) pos.y;
	}
	
	protected abstract void tick(int tick);
	
	public boolean isFree(float deltaX, float deltaY)
	{
		boolean world = Game.world.getBump().contains(new Rectangle((int) (pos.x + bumpX + deltaX), (int) (pos.y + bumpY + deltaY), bumpWidth, bumpHeight));
		if (!world) return false;
		
		for (Component e : Game.world.components)
		{
			if (e.equals(this)) continue;
			if (e instanceof Entity) if (((Entity) e).getBump().intersects(getBump(deltaX, deltaY))) return false;
		}
		
		return true;
	}
	
	public void setEventFunctions(JSONObject o)
	{
		eventFunctions = o;
	}
	
	public Rectangle2D getBump()
	{
		return getBump(0, 0);
	}
	
	public Rectangle2D getBump(float deltaX, float deltaY)
	{
		return new Rectangle2D.Float(pos.x + bumpX + deltaX, pos.y + bumpY + deltaY, bumpWidth, bumpHeight);
	}
	
	public boolean hasBump()
	{
		return bumpWidth > 2 && bumpHeight > 2;
	}
	
	public Rectangle2D getArea()
	{
		return new Rectangle2D.Float(pos.x, pos.y, width, height);
	}
	
	public Rectangle2D getArea2()
	{
		return new Rectangle2D.Float(Game.world.x + pos.x, Game.world.y + pos.y, width, height);
	}
	
	public boolean hasMoved()
	{
		return !pos.equals(spawn);
	}
	
	protected void checkForOnEnterEvent()
	{
		for (Component e : Game.world.components)
		{
			if (e.equals(this)) continue;
			Entity e1 = (Entity) e;
			Rectangle2D is = getBump().createIntersection(e1.hasBump() ? e1.getBump() : e1.getArea());
			if (is.getWidth() > 8 && is.getHeight() > 8) onEnter(e1);
		}
	}
	
	@Override
	public void setX(int x)
	{
		super.setX(x);
		pos.x = x;
	}
	
	@Override
	public void setY(int y)
	{
		super.setY(y);
		pos.y = y;
	}
	
	public JSONObject getData() throws JSONException
	{
		JSONObject o = new JSONObject();
		if (!hasMoved()) return o;
		
		o.put("uid", uid);
		o.put("pos", Assistant.serializeVector(pos));
		if (target != null) o.put("target", Assistant.serializeVector(target));
		return o;
	}
	
	public void setPos(JSONArray v) throws JSONException
	{
		pos = new Vector((float) v.getDouble(0), (float) v.getDouble(1));
	}
	
	public void setTarget(JSONArray v) throws JSONException
	{
		target = new Vector((float) v.getDouble(0), (float) v.getDouble(1));
	}
	
	public void setTarget(Vector v)
	{
		target = v;
	}
	
	@Override
	public boolean contains(int x, int y)
	{
		return super.contains(x - Game.world.x, y - Game.world.y);
	}
	
	public Attributes getAttributes()
	{
		return attr;
	}
	
	public Inventory getInventory()
	{
		return inv;
	}
	
	public float getDistance(Entity e)
	{
		return new Vector(pos.x + bumpX + bumpWidth / 2, pos.y + bumpY + bumpHeight / 2).getDistance(new Vector(e.pos.x + e.bumpX + e.bumpWidth / 2, e.pos.y + e.bumpY + e.bumpHeight / 2));
	}
	
	public float getBumpRadius()
	{
		return (float) Math.sqrt(Math.pow(bumpWidth / 2, 2) + Math.pow(bumpHeight / 2, 2));
	}
	
	public void kill()
	{
		onDeath();
		dead = true;
	}
	
	public boolean isDead()
	{
		return dead;
	}
	
	public boolean isFrozen()
	{
		return frozen;
	}
	
	public void setFrozen(boolean f)
	{
		frozen = f;
	}
	
	// -- self applying events -- //
	protected void onReachTarget()
	{
		if (eventFunctions.has("onReachTarget"))
		{
			try
			{
				JSInvoker.invoke(eventFunctions.getString("onReachTarget"), this);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	protected void onDeath()
	{
		if (eventFunctions.has("onDeath"))
		{
			try
			{
				JSInvoker.invoke(eventFunctions.getString("onDeath"), this);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void onPickup()
	{
		if (eventFunctions.has("onPickup"))
		{
			try
			{
				JSInvoker.invoke(eventFunctions.getString("onPickup"), this);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// -- on other entities applying event -- //
	protected void onClickReach(Entity entity)
	{
		if (entity.eventFunctions.has("onClickReach"))
		{
			try
			{
				JSInvoker.invoke(entity.eventFunctions.getString("onClickReach"), entity, this);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	protected void onEnter(Entity entity)
	{
		if (entity.eventFunctions.has("onEnter"))
		{
			try
			{
				JSInvoker.invoke(entity.eventFunctions.getString("onEnter"), entity, this);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
}
