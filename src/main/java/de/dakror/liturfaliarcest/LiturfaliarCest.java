package de.dakror.liturfaliarcest;

import javax.swing.ToolTipManager;
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
			ToolTipManager.sharedInstance().setInitialDelay(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// MUniVersion.offline = !Helper.isInternetReachable();
		// MUniVersion.init("/config/.properties", "https://buildhive.cloudbees.com/view/My Repositories/job/Dakror/job/LiturfaliarCest/ws/target/", "LiturfaliarCest");
		
		new Game();
		
		Game.currentFrame.init("Liturfaliar Cest");
		Game.currentFrame.updater = new UpdateThread();
		Game.currentFrame.setFullscreen();
		
		while (true)
			Game.currentFrame.main();
	}
}
