package de.dakror.liturfaliar.item.action;

import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;

public class EmptyAction extends ItemAction
{
  @Override
  public void actionTriggered(Item item, Viewport v)
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
