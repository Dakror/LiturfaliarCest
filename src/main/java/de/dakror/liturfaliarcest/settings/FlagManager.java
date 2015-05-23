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
 

package de.dakror.liturfaliarcest.settings;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.dakror.liturfaliarcest.game.Game;

/**
 * @author Dakror
 */
public class FlagManager {
	public static ArrayList<String> flags = new ArrayList<>();
	
	public static JTextArea jta;
	
	public static void toggleFlag(String name) {
		if (flags.contains(name.toUpperCase())) {
			flags.remove(name.toUpperCase());
			Game.world.dispatchFlagChange(name, false);
		} else {
			flags.add(name.toUpperCase());
			Game.world.dispatchFlagChange(name, true);
		}
	}
	
	public static void setFlag(String name) {
		if (!flags.contains(name)) {
			flags.add(name.toUpperCase());
			Game.world.dispatchFlagChange(name, true);
		}
	}
	
	public static void removeFlag(String name) {
		flags.remove(name.toUpperCase());
		Game.world.dispatchFlagChange(name, false);
	}
	
	public static void removeAllFlags(String name) {
		int amount = countFlag(name);
		for (int i = 0; i < amount; i++) {
			flags.remove(name.toUpperCase());
			Game.world.dispatchFlagChange(name, false);
		}
	}
	
	public static void addFlag(String name) {
		flags.add(name.toUpperCase());
		Game.world.dispatchFlagChange(name, true);
	}
	
	public static boolean isFlag(String name) {
		return flags.contains(name.toUpperCase());
	}
	
	public static boolean matchesFlags(String text) {
		text = text.trim();
		String[] flags = text.split(" ");
		for (String flag : flags) {
			if (flag.contains("|")) {
				String[] fl = flag.split("\\|");
				boolean match = false;
				for (String f : fl) {
					if ((f.startsWith("!") && !isFlag(f.substring(1))) || (!f.startsWith("!") && isFlag(f))) {
						match = true;
						break;
					}
				}
				
				if (!match) return false;
			} else if ((flag.startsWith("!") && isFlag(flag.substring(1))) || (!flag.startsWith("!") && !isFlag(flag))) return false;
		}
		
		return true;
	}
	
	public static int countFlag(String name) {
		int i = 0;
		for (String n : flags)
			if (n.toUpperCase().equals(name.toUpperCase())) i++;
		
		return i;
	}
	
	public static void showDebugWindow() {
		JFrame frame = new JFrame("Flag Manager Debug");
		frame.setSize(350, 700);
		
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jta = new JTextArea("");
		
		if (jta != null) // debug frame
		{
			Collections.sort(flags);
			String string = flags.toString().replace(", ", "\n");
			jta.setText(string.substring(1, string.length() - 1));
		}
		
		jta.setWrapStyleWord(true);
		jta.setLineWrap(true);
		JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.setContentPane(jsp);
		frame.setVisible(true);
	}
}
