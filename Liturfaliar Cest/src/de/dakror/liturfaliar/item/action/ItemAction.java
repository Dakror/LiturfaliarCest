package de.dakror.liturfaliar.item.action;

import org.json.JSONObject;

import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public abstract class ItemAction
{
  public abstract void actionTriggered(Item item, Creature c, Map m);
  
  public abstract JSONObject serializeItemAction();
  
  public abstract boolean equals(ItemAction o);
  
  static ItemAction loadItemAction(JSONObject o)
  {
    return new EmptyAction();
  }
  
  public static ItemAction load(JSONObject o)
  {
    if (o.length() == 0) return new EmptyAction();
    
    try
    {
      Class<?> c = Class.forName(ItemAction.class.getPackage().getName() + "." + o.getString("type"));
      return (ItemAction) c.getMethod("loadItemAction", JSONObject.class).invoke(null, o);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return loadItemAction(o);
  }
}
