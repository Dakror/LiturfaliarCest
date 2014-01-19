package de.dakror.liturfaliarcest;

import javax.swing.UIManager;

import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.UpdateThread;

public class LiturfaliarCest
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		new Game();
		
		Game.currentFrame.init("Liturfaliar Cest");
		Game.currentFrame.updater = new UpdateThread();
		Game.currentFrame.setFullscreen();
		
		while (true)
			Game.currentFrame.main();
	}
}
