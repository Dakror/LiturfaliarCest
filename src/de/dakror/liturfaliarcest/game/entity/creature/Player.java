package de.dakror.liturfaliarcest.game.entity.creature;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import de.dakror.gamesetup.util.Vector;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.settings.Attributes.Attribute;

/**
 * @author Dakror
 */
public class Player extends Creature
{
	/**
	 * a, w, d, s
	 */
	boolean[] dirs = { false, false, false, false };
	boolean sprint = true;
	
	public Player(int x, int y)
	{
		super(x, y, 64, 96);
		tex = "char/chars/001-Fighter01.png";
		attr.set(Attribute.SPEED, 2);
		
		bumpY = 70;
		bumpX = 16;
		bumpWidth = width / 2;
		bumpHeight = 24;
		sprint = false;
		
		attr.setWithMax(Attribute.HEALTH, 10);
		attr.setWithMax(Attribute.STAMINA, 10);
		
		uid = 0;
	}
	
	@Override
	protected void tick(int tick)
	{
		float spe = 0.025f;
		if (sprint && attr.get(Attribute.STAMINA) > 0 && (dirs[0] || dirs[1] || dirs[2] || dirs[3]))
		{
			attr.set(Attribute.SPEED, 5);
			attr.add(Attribute.STAMINA, -spe);
		}
		else
		{
			if (!dirs[0] && !dirs[1] && !dirs[2] && !dirs[3]) sprint = false;
			
			attr.set(Attribute.SPEED, 2);
			if (attr.get(Attribute.STAMINA) < attr.get(Attribute.STAMINA_MAX))
			{
				float dif = attr.get(Attribute.STAMINA_MAX) - attr.get(Attribute.STAMINA);
				if (!sprint) attr.add(Attribute.STAMINA, dif > 2 * spe ? 2 * spe : dif);
			}
		}
		
		if (dirs[0] || dirs[1] || dirs[2] || dirs[3])
		{
			Vector lastPos = pos.clone();
			
			float speed = attr.get(Attribute.SPEED);
			
			if (dirs[0] && isFree(-speed, 0)) pos.x -= speed;
			if (dirs[2] && isFree(speed, 0)) pos.x += speed;
			
			if (dirs[1] && isFree(0, -speed)) pos.y -= speed;
			if (dirs[3] && isFree(0, speed)) pos.y += speed;
			
			Vector dist = pos.clone().sub(lastPos);
			if (dist.getLength() > 1)
			{
				dist.setLength(speed);
				pos = lastPos.add(dist);
			}
			
			checkForOnEnterEvent();
			
			if (tick % (30 / attr.get(Attribute.SPEED)) == 0) frame = (frame + 1) % 4;
		}
		else frame = 0;
		
		if (Game.world.width > Game.getWidth())
		{
			Game.world.x = (int) (Game.getWidth() / 2 - pos.x - width / 2);
			if (Game.world.x > bumpX) Game.world.x = bumpX;
			if (Game.world.x + Game.world.width < Game.getWidth()) Game.world.x += Game.getWidth() - (Game.world.x + Game.world.width);
		}
		else Game.world.x = (Game.getWidth() - Game.world.width) / 2;
		
		if (Game.world.height > Game.getHeight())
		{
			Game.world.y = (int) (Game.getHeight() / 2 - pos.y - height / 2);
			if (Game.world.y > bumpY) Game.world.y = bumpY;
			if (Game.world.y + Game.world.height < Game.getHeight()) Game.world.y += Game.getHeight() - (Game.world.y + Game.world.height);
		}
		else Game.world.y = (Game.getHeight() - Game.world.height) / 2;
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		
		float degs = pos.clone().add(new Vector(width / 2, height / 2)).add(new Vector(Game.world.x, Game.world.y)).sub(new Vector(Game.currentGame.mouse)).getAngleOnXAxis();
		if (degs < 0) degs += 360;
		
		if (degs < 45 || degs > 315) dir = 1;
		else if (degs > 135 && degs < 225) dir = 2;
		else if (degs > 45 && degs < 135) dir = 3;
		else dir = 0;
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_A) dirs[0] = true;
		if (e.getKeyCode() == KeyEvent.VK_W) dirs[1] = true;
		if (e.getKeyCode() == KeyEvent.VK_D) dirs[2] = true;
		if (e.getKeyCode() == KeyEvent.VK_S) dirs[3] = true;
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) sprint = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_A) dirs[0] = false;
		if (e.getKeyCode() == KeyEvent.VK_W) dirs[1] = false;
		if (e.getKeyCode() == KeyEvent.VK_D) dirs[2] = false;
		if (e.getKeyCode() == KeyEvent.VK_S) dirs[3] = false;
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) sprint = false;
	}
}
