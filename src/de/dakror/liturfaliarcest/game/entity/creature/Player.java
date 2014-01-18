package de.dakror.liturfaliarcest.game.entity.creature;

import java.awt.event.KeyEvent;

import de.dakror.gamesetup.util.Vector;

/**
 * @author Dakror
 */
public class Player extends Creature
{
	/**
	 * a, w, d, s
	 */
	boolean[] dirs = { false, false, false, false };
	
	public Player(int x, int y)
	{
		super(x, y, 64, 96);
		tex = "001-Fighter01";
		speed = 2f;
	}
	
	@Override
	protected void tick(int tick)
	{
		super.tick(tick);
		if (dirs[0] || dirs[1] || dirs[2] || dirs[3])
		{
			target = new Vector(0, 0);
			target.x += dirs[0] ? -speed : dirs[2] ? speed : 0;
			target.x *= 2;
			target.y += dirs[1] ? -speed : dirs[3] ? speed : 0;
			target.y *= 2;
			
			target.add(pos);
		}
		else target = null;
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_A) dirs[0] = true;
		if (e.getKeyCode() == KeyEvent.VK_W) dirs[1] = true;
		if (e.getKeyCode() == KeyEvent.VK_D) dirs[2] = true;
		if (e.getKeyCode() == KeyEvent.VK_S) dirs[3] = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_A) dirs[0] = false;
		if (e.getKeyCode() == KeyEvent.VK_W) dirs[1] = false;
		if (e.getKeyCode() == KeyEvent.VK_D) dirs[2] = false;
		if (e.getKeyCode() == KeyEvent.VK_S) dirs[3] = false;
	}
}
