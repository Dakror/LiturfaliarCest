package de.dakror.liturfaliarcest.game.entity.creature;

import java.awt.event.KeyEvent;

import de.dakror.gamesetup.util.Vector;
import de.dakror.liturfaliarcest.game.Game;

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
		speed = 1.75f;
	}
	
	@Override
	protected void tick(int tick)
	{
		if (dirs[0] || dirs[1] || dirs[2] || dirs[3])
		{
			Vector lastPos = pos.clone();
			
			if (dirs[0] && Game.world.getBump().contains(pos.x - speed, pos.y, width, height)) pos.x -= speed;
			if (dirs[2] && Game.world.getBump().contains(pos.x + speed, pos.y, width, height)) pos.x += speed;
			
			if (dirs[1] && Game.world.getBump().contains(pos.x, pos.y - speed, width, height)) pos.y -= speed;
			if (dirs[3] && Game.world.getBump().contains(pos.x, pos.y + speed, width, height)) pos.y += speed;
			
			Vector dist = pos.clone().sub(lastPos);
			if (dist.getLength() > 1)
			{
				dist.setLength(speed);
				pos = lastPos.add(dist);
			}
			
			if (tick % 15 == 0) frame = (frame + 1) % 4;
		}
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
