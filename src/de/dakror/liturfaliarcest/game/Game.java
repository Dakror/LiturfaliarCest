package de.dakror.liturfaliarcest.game;

import java.awt.Graphics2D;

import de.dakror.gamesetup.GameFrame;

public class Game extends GameFrame
{
	public static Game currentGame;
	
	public Game()
	{
		currentGame = this;
	}
	
	@Override
	public void initGame()
	{}
	
	@Override
	public void draw(Graphics2D arg0)
	{}
}
