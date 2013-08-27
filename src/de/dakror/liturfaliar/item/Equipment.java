package de.dakror.liturfaliar.item;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.ui.hud.PlayerHotbar;

public class Equipment
{
	HashMap<String, Item> equips = new HashMap<String, Item>();
	Item[] hotbar;
	Item weapon1, weapon2;
	boolean male;
	
	public Equipment()
	{
		for (Categories c : Categories.EQUIPS)
		{
			setEquipmentItem(c, null);
		}
		weapon1 = weapon2 = null;
		hotbar = new Item[PlayerHotbar.SLOTCOUNT];
	}
	
	public Equipment(JSONObject data)
	{
		loadEquipment(data);
	}
	
	public void loadEquipment(JSONObject data)
	{
		try
		{
			for (Categories c : Categories.EQUIPS)
			{
				JSONObject d = data.getJSONObject(c.name().toLowerCase());
				
				setEquipmentItem(c, (d.length() != 0) ? new Item(d) : null);
			}
			if (data.getJSONObject("weapon1").length() > 0) setFirstWeapon(new Item(data.getJSONObject("weapon1")));
			
			if (data.getJSONObject("weapon2").length() > 0) setSecondWeapon(new Item(data.getJSONObject("weapon2")));
			
			JSONArray hb = data.getJSONArray("hotbar");
			
			hotbar = new Item[PlayerHotbar.SLOTCOUNT];
			for (int i = 0; i < hb.length(); i++)
			{
				if (hb.getJSONObject(i).length() > 0) hotbar[i] = new Item(hb.getJSONObject(i));
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setHotbarItem(int index, Item item)
	{
		hotbar[index] = item;
	}
	
	public Item getHotbarItem(int index)
	{
		return hotbar[index];
	}
	
	public void setFirstWeapon(Item item)
	{
		weapon1 = item;
	}
	
	public Item getFirstWeapon()
	{
		return weapon1;
	}
	
	public Item getSecondWeapon()
	{
		return weapon2;
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
	
	public boolean isProperlyFilled()
	{
		int filled = 0;
		for (Item item : equips.values())
		{
			if (item != null) filled++;
		}
		return filled > 1; // more than skin
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
			
			if (weapon1 != null) o.put("weapon1", weapon1.serializeItem());
			else o.put("weapon1", new JSONObject());
			
			if (weapon2 != null) o.put("weapon2", weapon2.serializeItem());
			else o.put("weapon2", new JSONObject());
			
			JSONArray hb = new JSONArray();
			for (int i = 0; i < hotbar.length; i++)
			{
				if (hotbar[i] == null) hb.put(new JSONObject());
				else hb.put(hotbar[i].serializeItem());
			}
			o.put("hotbar", hb);
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
			e.setEquipmentItem(Categories.HAIR, new Item(Types.HAIR, "1_red", 1));
			e.setEquipmentItem(Categories.SHIRT, new Item(Items.BOYSHIRT, 1));
			e.setEquipmentItem(Categories.PANTS, new Item(Items.BOYPANTS, 1));
			e.setEquipmentItem(Categories.BOOTS, new Item(Items.BOYBOOTS, 1));
			e.setEquipmentItem(Categories.SKIN, new Item(Types.SKIN, "man", 1));
		}
		else
		{
			e.setEquipmentItem(Categories.HAIR, new Item(Types.HAIR, "7_brown", 1));
			e.setEquipmentItem(Categories.SHIRT, new Item(Items.GIRLSHIRT, 1));
			e.setEquipmentItem(Categories.BOOTS, new Item(Items.GIRLBOOTS, 1));
			e.setEquipmentItem(Categories.SKIN, new Item(Types.SKIN, "woman", 1));
		}
		
		e.setMale(male);
		
		return e;
	}
	
	public Attributes getAttributes()
	{
		Attributes attr = new Attributes();
		
		for (Item e : equips.values())
		{
			if (e != null) attr.add(e.getAttributes());
		}
		
		return attr;
	}
}
