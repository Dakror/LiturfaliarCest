package de.dakror.liturfaliarcest.game.entity;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Vector;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.util.JSInvoker;

/**
 * @author Dakror
 */
public abstract class Entity extends Component
{
	protected Vector pos, target;
	protected float speed;
	
	public float alpha;
	
	public int bumpX, bumpY, bumpWidth, bumpHeight;
	
	protected JSONObject eventFunctions;
	
	public Entity(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		pos = new Vector(x, y);
		speed = 0;
		alpha = 1;
		
		eventFunctions = new JSONObject();
	}
	
	public void move()
	{
		if (target == null || pos.equals(target) || speed == 0) return;
		
		Vector distance = target.clone().sub(pos);
		if (distance.getLength() >= speed) distance.setLength(speed);
		
		pos.add(distance);
		checkForOnEnterEvent();
		
		if (pos.equals(target))
		{
			onReachTarget();
		}
	}
	
	@Override
	public void update(int tick)
	{
		move();
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
			
			if (e instanceof Entity)
			{
				if (((Entity) e).getBump().intersects(getBump(deltaX, deltaY))) return false;
			}
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
	
	// -- events -- //
	protected void onReachTarget()
	{
		if (eventFunctions.has("onReachTarget"))
		{
			try
			{
				JSInvoker.invoke(eventFunctions.getString("onReachTarget"));
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
				JSInvoker.invoke(entity.eventFunctions.getString("onEnter"), this);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
}
