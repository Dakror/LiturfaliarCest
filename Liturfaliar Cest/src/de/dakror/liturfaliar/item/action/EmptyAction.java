package de.dakror.liturfaliar.item.action;

import org.json.JSONObject;

import de.dakror.liturfaliar.item.Item;

public class EmptyAction extends ItemAction
{
  @Override
  public void actionTriggered(Item item)
  {}
  
  @Override
  public JSONObject serializeItemAction()
  {
    return new JSONObject();
  }
}
