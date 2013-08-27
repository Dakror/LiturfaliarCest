package de.dakror.liturfaliar.item.action;

import org.json.JSONObject;

import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class EmptyAction extends ItemAction
{
	@Override
	public void actionTriggered(Item item, Creature c, Map m)
	{}
	
	@Override
	public JSONObject serializeItemAction()
	{
		return new JSONObject();
	}
	
	@Override
	public boolean equals(ItemAction o)
	{
		
		return (o instanceof EmptyAction);
	}
}
