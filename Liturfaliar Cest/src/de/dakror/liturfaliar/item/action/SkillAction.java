package de.dakror.liturfaliar.item.action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Items;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.item.skillanim.SkillAnimation;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class SkillAction extends ItemAction
{
  String         description;
  SkillAnimation animation;
  Items[]        parents;
  
  public SkillAction(String desc, SkillAnimation anim, Items... p)
  {
    description = desc;
    animation = anim;
    parents = p;
  }
  
  @Override
  public void actionTriggered(Item item, Creature c, Map m, Viewport v)
  {
    if (!item.getType().equals(Types.PERKSKILL)) // weapon involved
    {
      if (c.getEquipment().getFirstWeapon() == null && c.getEquipment().getSecondWeapon() == null)
      {
        v.playSound("003-System03");
        return;
      }
    }
    if (!c.isPlayingSkill(animation))
    {
      c.playSkill(animation);
      animation.playAnimation(item, c);
      item.getItemSlot().startCooldown();
    }
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
      o.put("parents", new JSONArray(parents));
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
      Items[] parents = new Items[o.getJSONArray("parents").length()];
      for (int i = 0; i < parents.length; i++)
      {
        parents[i] = Items.valueOf(o.getJSONArray("parents").getString(i));
      }
      return new SkillAction(o.getString("desc"), (SkillAnimation) Class.forName(o.getString("anim")).newInstance(), parents);
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
  
  public SkillAnimation getAnimation()
  {
    return animation;
  }
  
  public Items[] getParents()
  {
    return parents;
  }
}
