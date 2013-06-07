package de.dakror.liturfaliar.item;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.settings.Attributes;

public class Equipment
{
  HashMap<String, Item> equips = new HashMap<String, Item>();
  Item                  weapon1, weapon2;
  boolean               male;
  
  public Equipment()
  {
    for (Categories c : Categories.values())
    {
      setEquipmentItem(c, null);
    }
    weapon1 = weapon2 = null;
  }
  
  public Equipment(JSONObject data)
  {
    loadEquipment(data);
  }
  
  public void loadEquipment(JSONObject data)
  {
    try
    {
      for (Categories c : Categories.values())
      {
        if (c.equals(Categories.WEAPON))
          continue;
        
        JSONObject d = data.getJSONObject(c.name().toLowerCase());
        
        setEquipmentItem(c, (d.length() != 0) ? new Item(d) : null);
      }
      if (data.getJSONObject("weapon1").length() > 0)
        setFirstWeapon(new Item(data.getJSONObject("weapon1")));
      
      if (data.getJSONObject("weapon2").length() > 0)
        setSecondWeapon(new Item(data.getJSONObject("weapon2")));
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public void setFirstWeapon(Item item)
  {
    weapon1 = item;
  }
  
  public void setSecondWeapon(Item item)
  {
    weapon1 = item;
  }
  
  public Item getEquipmentItem(Categories c)
  {
    return equips.get(c.name().toLowerCase());
  }
  
  public boolean hasEquipmentItem(Categories c)
  {
    return equips.get(c.name().toLowerCase()) != null;
  }
  
  public void setEquipmentItem(Categories c, Item item)
  {
    equips.put(c.name().toLowerCase(), item);
  }
  
  public JSONObject serializeEquipment()
  {
    JSONObject o = new JSONObject();
    try
    {
      for (String key : equips.keySet())
      {
        o.put(key, (equips.get(key) != null) ? equips.get(key).serializeItem() : new JSONObject());
      }
      
      if (weapon1 != null)
        o.put("weapon1", weapon1.serializeItem());
      else o.put("weapon1", new JSONObject());
      
      if (weapon2 != null)
        o.put("weapon2", weapon2.serializeItem());
      else o.put("weapon2", new JSONObject());
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    
    return o;
  }
  
  public boolean isMale()
  {
    return male;
  }
  
  public void setMale(boolean male)
  {
    this.male = male;
  }
  
  public static Equipment getDefault(boolean male)
  {
    Equipment e = new Equipment();
    if (male)
    {
      e.setEquipmentItem(Categories.HAIR, new Item(Types.HAIR, "1_red"));
      e.setEquipmentItem(Categories.SHIRT, new Item(Items.BOYSHIRT));
      e.setEquipmentItem(Categories.PANTS, new Item(Items.BOYPANTS));
      e.setEquipmentItem(Categories.BOOTS, new Item(Items.BOYBOOTS));
      e.setEquipmentItem(Categories.SKIN, new Item(Types.SKIN, "man"));
    }
    else
    {
      e.setEquipmentItem(Categories.HAIR, new Item(Types.HAIR, "7_brown"));
      e.setEquipmentItem(Categories.SHIRT, new Item(Items.GIRLSHIRT));
      e.setEquipmentItem(Categories.BOOTS, new Item(Items.GIRLBOOTS));
      e.setEquipmentItem(Categories.SKIN, new Item(Types.SKIN, "woman"));
    }
    
    e.setMale(male);
    
    return e;
  }

  public Attributes getAttributes() {
    Attributes attr = new Attributes();
    
    
    
    return attr;
  }
}
