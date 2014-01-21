package de.dakror.liturfaliarcest.game.entity;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Vector;
import de.dakror.liturfaliarcest.game.Game;

/**
 * @author Dakror
 */
public abstract class Entity extends Component
{
	protected Vector pos, target;
	protected float speed;
	
	public int bumpX, bumpY, bumpWidth, bumpHeight;
	
	public Entity(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		pos = new Vector(x, y);
		speed = 0;
	}
	
	public void move()
	{
		if (target == null || pos.equals(target) || speed == 0) return;
		
		Vector distance = target.clone().sub(pos);
		if (distance.getLength() >= speed) distance.setLength(speed);
		
		pos.add(distance);
		
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
	
	public Rectangle2D getBump()
	{
		return new Rectangle2D.Float(x + bumpX, y + bumpY, bumpWidth, bumpHeight);
	}
	
	public Rectangle2D getBump(float deltaX, float deltaY)
	{
		return new Rectangle2D.Float(x + bumpX + deltaX, y + bumpY + deltaY, bumpWidth, bumpHeight);
	}
	
	protected void onReachTarget()
	{}
}
