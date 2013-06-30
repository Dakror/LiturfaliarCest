package de.dakror.liturfaliar.item.action;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.skillanim.SkillAnimation;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class SkillAction extends ItemAction
{
  String         description;
  SkillAnimation animation;
  
  public SkillAction(String desc, SkillAnimation anim)
  {
    description = desc;
    animation = anim;
  }
  
  @Override
  public void actionTriggered(Item item, Creature c, Map m, Viewport v)
  {
    c.playSkill(animation);
    animation.playAnimation(item, c);
  }
  
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
      o.put("anim", animation.getClass().getName());
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
      return new SkillAction(o.getString("desc"), (SkillAnimation) Class.forName(o.getString("anim")).newInstance());
    }
    catch (Exception e)
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
