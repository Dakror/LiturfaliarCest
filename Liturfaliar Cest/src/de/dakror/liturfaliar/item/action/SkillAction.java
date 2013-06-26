package de.dakror.liturfaliar.item.action;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;

public class SkillAction extends ItemAction
{
  String description;
  
  public SkillAction(String desc)
  {
    description = desc;
  }
  
  @Override
  public void actionTriggered(Item item, Map m, Viewport v)
  {}
  
  public String getDescription()
  {
    return description;
  }
  
  @Override
  public JSONObject serializeItemAction()
  {
    JSONObject o = new JSONObject();
    try
    {
      o.put("type", getClass().getSimpleName());
      o.put("desc", description);
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
      return new SkillAction(o.getString("desc"));
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
    if (o instanceof SkillAction)
    {
      return description.equals(((SkillAction) o).description);
    }
    else return false;
  }
  
}
