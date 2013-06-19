package de.dakror.liturfaliar.item.action;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;

public abstract class ItemAction
{
  public abstract void actionTriggered(Item item, Map m, Viewport v);
  
  public abstract JSONObject serializeItemAction();
  
  public abstract boolean equals(ItemAction o);
  
  public static ItemAction loadItemAction(JSONObject o)
  {
    return new EmptyAction();
  }
  
  public static ItemAction load(JSONObject o)
  {
    if (o.length() == 0)
      return new EmptyAction();
    try
    {
      switch (o.getString("type"))
      {
        case "Potion":
          return Potion.loadItemAction(o);
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return loadItemAction(o);
  }
}
