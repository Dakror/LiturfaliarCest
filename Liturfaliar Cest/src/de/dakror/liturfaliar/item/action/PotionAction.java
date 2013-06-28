package de.dakror.liturfaliar.item.action;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.settings.Attribute;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;

public class PotionAction extends ItemAction
{
  Attributes changes;
  String     targetID;
  
  public PotionAction(String t, Attributes c)
  {
    changes = c;
    targetID = t;
  }
  
  @Override
  public void actionTriggered(Item item, Map m, Viewport v)
  {
    if (item.getItemSlot() == null)
      return;
    
    Creature target = m.getCreatureByAccessKey(targetID);
    
    if (target == null)
      return;
    
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
      return;
    
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
      o.put("type", getClass().getSimpleName());
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
      return new PotionAction(o.getString("target"), new Attributes(o.getJSONObject("attr")));
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
    if (o instanceof PotionAction)
    {
      return targetID.equals(((PotionAction) o).targetID) && changes.equals(((PotionAction) o).changes);
    }
    else return false;
  }
}
