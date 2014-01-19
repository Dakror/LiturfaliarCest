package de.dakror.liturfaliarcest.game;

import java.awt.Graphics2D;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.entity.creature.Player;
import de.dakror.liturfaliarcest.game.world.World;

public class Game extends GameFrame
{
	public static Game currentGame;
	public static World world;
	public static Player player;
	
	public Game()
	{
		currentGame = this;
	}
	
	@Override
	public void initGame()
	{
		world = new World("map1");
		player = new Player(0, 128);
		world.addEntity(player);
		addLayer(world);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawLayers(g);
		
		Helper.drawString("FPS: " + getFPS(), 10, 26, g, 18);
		Helper.drawString("UPS: " + getUPS(), 10, 52, g, 18);
	}
}
