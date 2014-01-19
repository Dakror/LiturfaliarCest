package de.dakror.liturfaliarcest.game;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.editor.Editor;
import de.dakror.liturfaliarcest.game.entity.creature.Player;
import de.dakror.liturfaliarcest.game.world.World;

public class Game extends GameFrame
{
	public static Game currentGame;
	public static World world;
	public static Player player;
	
	public static Editor editor;
	
	public Game()
	{
		currentGame = this;
	}
	
	@Override
	public void initGame()
	{
		// world = new World("map1");
		// player = new Player(0, 128);
		// world.addEntity(player);
		// addLayer(world);
		editor = new Editor();
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawLayers(g);
		
		Helper.drawString("FPS: " + getFPS(), 10, 26, g, 18);
		Helper.drawString("UPS: " + getUPS(), 10, 52, g, 18);
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		super.keyPressed(e);
		if (e.getKeyCode() == KeyEvent.VK_F2)
		{
			if (editor == null) editor = new Editor();
			else
			{
				editor.dispose();
				editor = null;
			}
		}
	}
}
