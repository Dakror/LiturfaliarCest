package de.dakror.liturfaliarcest.game.quest;

import java.util.HashMap;

import de.dakror.gamesetup.util.CSVReader;

/**
 * @author Dakror
 */
public class Quest
{
	public static HashMap<Integer, Quest> quests;
	
	public static void init()
	{
		quests = new HashMap<>();
		
		CSVReader csv = new CSVReader("/quests.csv");
		csv.readRow();
		
		String cell = "";
		Quest quest = null;
		while ((cell = csv.readNext()) != null)
		{
			switch (csv.getIndex())
			{
				case 0:
					if (quest != null) quests.put(quest.id, quest);
					quest = new Quest();
					quest.id = Integer.parseInt(cell);
					break;
				case 1:
					quest.flag = cell;
					break;
				case 2:
					quest.name = cell;
					break;
				case 3:
					quest.originGUID = cell;
					break;
				case 4:
					quest.goal = cell;
					break;
				case 5:
					quest.main = cell.equals("1") ? true : false;
					break;
			}
			
			if (cell.length() == 0) continue;
		}
		
		quests.put(quest.id, quest);
	}
	
	private String name, text, flag, originGUID, goal;
	private boolean main;
	private int id;
	
	public Quest()
	{}
	
	public String getName()
	{
		return name;
	}
	
	public String getText()
	{
		return text;
	}
	
	public String getFlag()
	{
		return flag;
	}
	
	public String getOriginGUID()
	{
		return originGUID;
	}
	
	public String getGoal()
	{
		return goal;
	}
	
	public boolean isMain()
	{
		return main;
	}
	
	public int getId()
	{
		return id;
	}
}
