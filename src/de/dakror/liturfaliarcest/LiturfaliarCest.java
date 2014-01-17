package de.dakror.liturfaliarcest;

import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.UpdateThread;

public class LiturfaliarCest
{
	public static void main(String[] args)
	{
		new Game();
		
		Game.currentFrame.init("Liturfaliar Cest");
		Game.currentFrame.updater = new UpdateThread();
		Game.currentFrame.setFullscreen();
		
		while (true)
			Game.currentFrame.main();
	}
}
