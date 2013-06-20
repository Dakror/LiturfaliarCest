package de.dakror.liturfaliar.item.action;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.settings.Attribute;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.CFG;
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
  public void actionTriggered(Item item, Map m, Viewport v)
  {
    CFG.p("i got called");
    
    if (item.getItemSlot() == null)
    {
      CFG.p("no itemslot");
      return;
    }
    
    Creature target = m.getCreatureByAccessKey(targetID);
    
    if (target == null)
    {
      CFG.p("no target");
      return;
    }
    
    Attributes attributes = target.getAttributes();
    
    boolean hasEffect = false;
    
    for (Attr attr : Attr.values())
    {
      Attribute attribute = attributes.getAttribute(attr);
      
      if (!changes.getAttribute(attr).isEmpty())
      {
        double sum = changes.getAttribute(attr).getValue() + attribute.getValue();
        sum = (sum < attribute.getMaximum()) ? ((sum >= attr.getMinimum()) ? sum : attr.getMinimum()) : attribute.getMaximum();
        
        if (sum != attribute.getValue())
        {
          hasEffect = true;
          break;
        }
      }
    }
    
    if (!hasEffect)
    {
      CFG.p("has no effect");
      return;
    }
    
    for (Attr attr : Attr.values())
    {
      Attribute attribute = attributes.getAttribute(attr);
      
      if (!changes.getAttribute(attr).isEmpty())
      {
        double sum = changes.getAttribute(attr).getValue() + attribute.getValue();
        attributes.getAttribute(attr).setValue((sum < attribute.getMaximum()) ? ((sum >= attr.getMinimum()) ? sum : attr.getMinimum()) : attribute.getMaximum());
      }
    }
    target.setAttributes(attributes);
    v.playSound("184-DrinkPotion");
    item.getItemSlot().subItem();
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
  
  public Attributes getChanges()
  {
    return changes;
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
  
  @Override
  public boolean equals(ItemAction o)
  {
    if (o instanceof Potion)
    {
      return targetID.equals(((Potion) o).targetID) && changes.equals(((Potion) o).changes);
    }
    else return false;
  }
}
