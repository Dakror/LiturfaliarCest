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
import de.dakror.liturfaliar.settings.DamageType;

public class WeaponAction extends ItemAction
{
  DamageType dType;
  Attributes effect;
  
  public WeaponAction(Attributes e, DamageType d)
  {
    effect = e;
    dType = d;
  }
  
  public DamageType getDamageType()
  {
    return dType;
  }
  
  public void setDamageType(DamageType dType)
  {
    this.dType = dType;
  }
  
  public Attributes getEffect()
  {
    return effect;
  }
  
  public void setEffect(Attributes effect)
  {
    this.effect = effect;
  }
  
  @Override
  public void actionTriggered(Item item, Creature c, Map m, Viewport v)
  {}
  
  @Override
  public JSONObject serializeItemAction()
  {
    JSONObject o = new JSONObject();
    try
    {
      o.put("type", getClass().getSimpleName());
      o.put("dmgtype", dType.name());
      o.put("effect", effect.serializeAttributes());
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
      return new WeaponAction(new Attributes(o.getJSONObject("effect")), DamageType.valueOf(o.getString("dmgtype")));
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
    if (o instanceof WeaponAction)
    {
      return dType.equals(((WeaponAction) o).dType) && effect.equals(((WeaponAction) o).effect);
    }
    else return false;
  }
  
  public double getReandomValue(Attr attr)
  {
    if (effect.getAttribute(attr).isEmpty()) return 0;
    
    Attribute a = effect.getAttribute(attr);
    
    return Math.round(Math.random() * (a.getMaximum() - a.getValue())) + a.getValue();
  }
}
