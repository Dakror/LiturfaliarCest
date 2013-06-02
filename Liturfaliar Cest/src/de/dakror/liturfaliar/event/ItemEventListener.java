package de.dakror.liturfaliar.event;

import java.awt.event.MouseEvent;

import de.dakror.liturfaliar.item.Item;

public interface ItemEventListener
{
  public void itemPressed(MouseEvent e, Item item);
  
  public void itemDragged(MouseEvent e, Item item);
  
  public void itemReleased(MouseEvent e, Item item);
}
