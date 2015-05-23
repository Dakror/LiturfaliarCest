/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
 

package de.dakror.liturfaliarcest;

import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.UpdateThread;


public class LiturfaliarCest {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			ToolTipManager.sharedInstance().setInitialDelay(0);
		} catch (Exception e) {
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
