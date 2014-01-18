package de.dakror.liturfaliarcest.game.entity;

import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Vector;

/**
 * @author Dakror
 */
public abstract class Entity extends Component
{
	protected Vector pos, target;
	protected float speed;
	
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
		if (distance.getLength() > speed) distance.setLength(speed);
		
		pos.add(distance);
		
		x = (int) pos.x;
		y = (int) pos.y;
		
		if (pos.equals(target))
		{
			onReachTarget();
			target = null;
		}
	}
	
	@Override
	public void update(int tick)
	{
		move();
		tick(tick);
	}
	
	
	protected abstract void tick(int tick);
	
	protected void onReachTarget()
	{}
}
