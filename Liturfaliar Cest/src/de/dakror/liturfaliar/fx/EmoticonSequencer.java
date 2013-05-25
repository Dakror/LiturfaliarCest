package de.dakror.liturfaliar.fx;

import java.util.ArrayList;
import java.util.HashMap;

import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class EmoticonSequencer
{
  Map                        map;
  String                     raw;
  
  HashMap<Integer, Emoticon> emoticons = new HashMap<Integer, Emoticon>();
  ArrayList<Creature>        creatures = new ArrayList<Creature>();
  
  public EmoticonSequencer(Map m, String r)
  {
    map = m;
    raw = r;
    
    parse();
  }
  
  
  /**
   * Template for tag: "{ ( npc_ID | player ) : emoticonID }"<br>
   * Example: "{npc_0:35}"
   */
  private void parse()
  {
    
    String[] parts = raw.split("\\{");
    int startIndex = 0;
    for (int i = 1; i < parts.length; i++)
    {
      String parent = parts[i].substring(0, parts[i].indexOf(":"));
      int type = Integer.parseInt(parts[i].substring(parts[i].indexOf(":") + 1, parts[i].indexOf("}")));
      
      Emoticon emoticon = null;
      
      if (type > -1)
      {
        Creature creature = map.getCreatureByAccessKey(parent);
        creatures.add(creature);
        emoticon = new Emoticon(creature, type, true, -1);
      }
      
      emoticons.put(startIndex, emoticon);
      
      startIndex += parts[i].length();
    }
  }
  
  public String getClearedString()
  {
    return raw.replaceAll("\\{.{1,}\\}", "");
  }
  
  public void update(String s)
  {
    int key = s.length() - 1;
    if (emoticons.containsKey(key))
    {
      emoticons.get(key).getParent().setEmoticon(emoticons.get(key));
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
