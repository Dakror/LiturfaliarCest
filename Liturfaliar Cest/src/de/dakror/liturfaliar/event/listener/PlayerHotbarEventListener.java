package de.dakror.liturfaliar.event.listener;

import de.dakror.liturfaliar.ui.ItemSlot;

public interface PlayerHotbarEventListener
{
  public void slotTriggered(int index, ItemSlot slot);
}
