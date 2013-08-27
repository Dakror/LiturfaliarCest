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

public class PotionAction extends ItemAction
{
	public static final String CASTER = "CASTER";
	public static final String HOSTILE = "HOSTILE";
	public static final String ALLY = "ALLY";
	
	Attributes changes;
	
	/**
	 * Defines who gets the potion effect<br>
	 * default = {@link PotionAction.CASTER}<br>
	 * can be absolute or relative<br>
	 * absolute: "npc_0"<br>
	 * relative: "{@link PotionAction.HOSTILE}"
	 */
	String targetID;
	
	DamageType dmgType;
	
	public PotionAction(String t, Attributes c, DamageType d)
	{
		changes = c;
		targetID = t;
		dmgType = d;
	}
	
	@Override
	public void actionTriggered(Item item, Creature c, Map m)
	{
		if (item.getItemSlot() == null) return;
		
		Creature target = c;
		
		if (targetID.indexOf("_") > -1) target = m.getCreatureByAccessKey(targetID);
		// else if(targetID.equals(CASTER)) // TODO: add relative target mechanic
		
		if (target == null) return;
		
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
			Viewport.playSound("003-System03");
			return;
		}
		
		// for (Attr attr : Attr.values())
		// {
		// Attribute attribute = attributes.getAttribute(attr);
		//
		// if (!changes.getAttribute(attr).isEmpty())
		// {
		// double sum = changes.getAttribute(attr).getValue() + attribute.getValue();
		//
		// if (!attr.equals(Attr.health))
		// attributes.getAttribute(attr).setValue((sum < attribute.getMaximum()) ? ((sum >= attr.getMinimum()) ? sum : attr.getMinimum()) : attribute.getMaximum());
		// }
		// }
		// target.setAttributes(attributes);
		
		if (!changes.getAttribute(Attr.health).isEmpty())
		{
			target.dealDamage(c, dmgType, (int) changes.getAttribute(Attr.health).getValue());
		}
		
		Viewport.playSound("184-DrinkPotion");
		item.getItemSlot().subItem();
		item.getItemSlot().startCooldown();
	}
	
	@Override
	public JSONObject serializeItemAction()
	{
		JSONObject o = new JSONObject();
		try
		{
			o.put("type", getClass().getSimpleName());
			o.put("dmgtype", dmgType.name());
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
			return new PotionAction(o.getString("target"), new Attributes(o.getJSONObject("attr")), DamageType.valueOf(o.getString("dmgtype")));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String getTarget()
	{
		return targetID;
	}
	
	public DamageType getDamageType()
	{
		return dmgType;
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
