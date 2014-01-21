package de.dakror.liturfaliarcest.game.entity;

import java.awt.Rectangle;

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
	
	protected int bumpX, bumpY, bumpWidth, bumpHeight;
	
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
	
	public boolean clips(float deltaX, float deltaY)
	{
		return Game.world.getBump().contains(new Rectangle((int) (pos.x + bumpX + deltaX), (int) (pos.y + bumpY + deltaY), bumpWidth, bumpHeight));
	}
	
	protected void onReachTarget()
	{}
}
