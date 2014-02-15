package de.dakror.liturfaliarcest.game;

import de.dakror.gamesetup.Updater;
import de.dakror.liturfaliarcest.util.JSInvoker;

public class UpdateThread extends Updater
{
	public UpdateThread()
	{}
	
	@Override
	public void update()
	{
		if (Game.currentGame.alpha == 1)
		{
			if (Game.currentGame.actionOnFade != null) JSInvoker.invoke(Game.currentGame.actionOnFade);
			Game.currentGame.actionOnFade = null;
		}
	}
}
