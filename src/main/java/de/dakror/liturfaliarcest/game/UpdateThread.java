package de.dakror.liturfaliarcest.game;

import de.dakror.liturfaliarcest.util.JSInvoker;
import de.dakror.gamesetup.Updater;

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
