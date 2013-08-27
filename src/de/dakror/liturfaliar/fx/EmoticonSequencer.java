package de.dakror.liturfaliar.fx;

import java.util.ArrayList;
import java.util.HashMap;

import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

/**
 * Template for tag: "{ ( npc_ID | player ) : emoticonID }"<br>
 * Example: "{npc_0:35}"
 */
public class EmoticonSequencer
{
	Map map;
	String raw;
	
	HashMap<Integer, Emoticon> emoticons = new HashMap<Integer, Emoticon>();
	ArrayList<Creature> creatures = new ArrayList<Creature>();
	
	public EmoticonSequencer(Map m, String r)
	{
		map = m;
		raw = r;
		
		parse();
	}
	
	private void parse()
	{
		String[] parts = raw.split("\\{");
		int startIndex = 0;
		for (int i = 1; i < parts.length; i++)
		{
			String parent = parts[i].substring(0, parts[i].indexOf(":"));
			int type = Integer.parseInt(parts[i].substring(parts[i].indexOf(":") + 1, parts[i].indexOf("}")));
			
			Emoticon emoticon = null;
			
			Creature creature = map.getCreatureByAccessKey(parent);
			
			if (!creatures.contains(creature))
			{
				creatures.add(creature);
			}
			
			emoticon = new Emoticon(creature, type, true, -1);
			
			emoticons.put(startIndex, emoticon);
			
			startIndex += parts[i].substring(parts[i].indexOf("}") + 1).length();
		}
	}
	
	public String getClearedString()
	{
		return raw.replaceAll("\\{\\S{1,}\\}", "");
	}
	
	public void update(String s)
	{
		int key = s.length() - 1;
		if (emoticons.containsKey(key))
		{
			Emoticon emoticon = emoticons.get(key);
			
			emoticon.getParent().setEmoticon((emoticon.getType() == -1) ? null : emoticon);
		}
	}
	
	public void clearCreatureEmoticons()
	{
		for (Creature creature : creatures)
		{
			creature.setEmoticon(null);
		}
	}
}
