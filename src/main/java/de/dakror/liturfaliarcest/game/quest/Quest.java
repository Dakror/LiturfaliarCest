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
 

package de.dakror.liturfaliarcest.game.quest;

import java.util.HashMap;

import de.dakror.gamesetup.util.CSVReader;

/**
 * @author Dakror
 */
public class Quest {
	public static HashMap<Integer, Quest> quests;
	
	public static void init() {
		quests = new HashMap<>();
		
		CSVReader csv = new CSVReader("/csv/quests.csv");
		csv.readRow();
		
		String cell = "";
		Quest quest = null;
		while ((cell = csv.readNext()) != null) {
			switch (csv.getIndex()) {
				case 0:
					if (quest != null) quests.put(quest.id, quest);
					quest = new Quest();
					quest.id = Integer.parseInt(cell);
					break;
				case 1:
					quest.flags = cell;
					break;
				case 2:
					quest.name = cell;
					break;
				case 3:
					quest.text = cell;
					break;
				case 4:
					quest.originGUID = cell;
					break;
				case 5:
					quest.goal = cell;
					break;
				case 6:
					quest.main = cell.equals("1") ? true : false;
					break;
			}
			
			if (cell.length() == 0) continue;
		}
		
		quests.put(quest.id, quest);
	}
	
	private String name, text, flags, originGUID, goal;
	private boolean main;
	private int id;
	
	public Quest() {}
	
	public String getName() {
		return name;
	}
	
	public String getText() {
		return text;
	}
	
	public String getFlags() {
		return flags;
	}
	
	public String getOriginGUID() {
		return originGUID;
	}
	
	public String getGoal() {
		return goal;
	}
	
	public boolean isMain() {
		return main;
	}
	
	public int getId() {
		return id;
	}
}
