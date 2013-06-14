package de.dakror.liturfaliar.item.action;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.settings.Attribute;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;

public class Potion extends ItemAction
{
  Attributes changes;
  String     targetID;
  
  public Potion(String t, Attributes c)
  {
    changes = c;
    targetID = t;
  }
  
  @Override
  public void actionTriggered(Item item)
  {
    if (item.getItemSlot() == null)
      return;
    
    Creature target = item.getItemSlot().getInventory().getMap().getCreatureByAccessKey(targetID);
    
    Attributes attributes = target.getAttributes();
    
    for (Attr attr : Attr.values())
    {
      Attribute attribute = attributes.getAttribute(attr);
      
      if (item.getType().equals(Types.HEALPOTION))
      {
        if (!changes.getAttribute(attr).isEmpty())
        {
          double sum = changes.getAttribute(attr).getValue() + attribute.getValue();
          attributes.getAttribute(attr).setValue((sum < attribute.getMaximum()) ? ((sum >= attr.getMinimum()) ? sum : attr.getMinimum()) : attribute.getMaximum());
        }
      }
    }
    target.setAttributes(attributes);
  }
  
  
  @Override
  public JSONObject serializeItemAction()
  {
    JSONObject o = new JSONObject();
    try
    {
      o.put("type", "Potion");
      o.put("target", targetID);
      o.put("attr", changes.serializeAttributes());
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return o;
  }
  
  public static ItemAction loadItemAction(JSONObject o)
  {
    try
    {
      return new Potion(o.getString("target"), new Attributes(o.getJSONObject("attr")));
    }
    catch (JSONException e)
    {
      e.printStackTrace();
      return null;
    }
  }
}
