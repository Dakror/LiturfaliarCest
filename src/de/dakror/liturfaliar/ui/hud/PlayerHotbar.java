package de.dakror.liturfaliar.ui.hud;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.Dispatcher;
import de.dakror.liturfaliar.event.Event;
import de.dakror.liturfaliar.event.Events;
import de.dakror.liturfaliar.item.Inventory;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.ui.ItemSlot;

public class PlayerHotbar extends HUDComponent implements Inventory
{
	public static final Integer[] KEYSLOTS = { KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_Q, KeyEvent.VK_E, KeyEvent.VK_R, KeyEvent.VK_F };
	public static final Integer[] MOUSESLOTS = { MouseEvent.BUTTON1, MouseEvent.BUTTON3 };
	
	public static final int SLOTCOUNT = KEYSLOTS.length + MOUSESLOTS.length;
	
	ItemSlot[] slots;
	
	Player player;
	
	ItemSlot pickedUp;
	
	Map m;
	
	public boolean frozen;
	
	public PlayerHotbar(Player p)
	{
		super(0, 0, ItemSlot.SIZE * SLOTCOUNT, ItemSlot.SIZE, 10);
		player = p;
		frozen = false;
	}
	
	@Override
	public void update(long timePassed, Map m)
	{
		this.m = m;
		
		visible = m.talk == null;
		
		for (int i = 0; i < SLOTCOUNT; i++)
		{
			ItemSlot slot = slots[i];
			
			if (!frozen) slot.update(timePassed);
			
			Item eItem = player.getEquipment().getHotbarItem(i);
			if (slot.getItem() == null && eItem == null) continue;
			if (slot.getItem() == null && eItem != null) slot.setItem(eItem);
			else if (slot.getItem() != null && eItem == null) slot.setItem(eItem);
			else if (!slot.getItem().equals(eItem)) slot.setItem(eItem);
		}
	}
	
	@Override
	public void draw(Graphics2D g, Map m)
	{
		if (!visible)
		{
			setX(Viewport.w.getWidth() / 2 - width / 2);
			setY(Viewport.w.getHeight() - ItemSlot.SIZE);
			
			slots = ItemSlot.createSlotRow(x, y, 10);
			
			for (int i = 0; i < SLOTCOUNT; i++)
			{
				slots[i].setInventory(this);
				slots[i].setItem(player.getEquipment().getHotbarItem(i));
				slots[i].setHotKey((i < KEYSLOTS.length) ? KEYSLOTS[i] : MOUSESLOTS[i - KEYSLOTS.length], i > KEYSLOTS.length - 1);
			}
			visible = true;
		}
		
		if (visible)
		{
			for (int i = 0; i < slots.length; i++)
				if (slots[i] != null) slots[i].draw(g);
			
			for (int i = 0; i < slots.length; i++)
				if (slots[i] != null) slots[i].drawTooltip(g);
			
			if (pickedUp != null) pickedUp.drawLightWeight(g);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e, Map m)
	{
		if (!visible) return;
		
		List<Integer> slots = Arrays.asList(KEYSLOTS);
		if (slots.contains(e.getKeyCode())) Dispatcher.dispatch(Events.slotTriggered, slots.indexOf(e.getKeyCode()), this.slots[slots.indexOf(e.getKeyCode())]);
	}
	
	@Override
	public void mousePressed(MouseEvent e, Map m)
	{
		if (!visible) return;
		
		List<Integer> slots = Arrays.asList(MOUSESLOTS);
		if (slots.contains(e.getButton()) && !getArea().contains(e.getLocationOnScreen())) Dispatcher.dispatch(Events.slotTriggered, slots.indexOf(e.getButton()) + KEYSLOTS.length, this.slots[slots.indexOf(e.getButton()) + KEYSLOTS.length]);
		
		for (ItemSlot slot : this.slots)
		{
			slot.mousePressed(e);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e, Map m)
	{
		if (!visible) return;
		
		for (ItemSlot slot : this.slots)
		{
			slot.mouseMoved(e);
		}
		
		if (pickedUp != null) pickedUp.mouseMoved(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e, Map m)
	{
		if (!visible) return;
		
		for (ItemSlot slot : this.slots)
		{
			slot.mouseReleased(e);
		}
	}
	
	@Override
	public void onEvent(Event e)
	{
		if (e.equals(Events.slotPressed))
		{
			ItemSlot slot = (ItemSlot) e.getParam("slot");
			if (!Viewport.isSceneEnabled()) return;
			
			pickedUp = new ItemSlot(slot);
			pickedUp.setHotKey(-1, false);
			slot.setItem(null);
			
			player.getEquipment().setHotbarItem(Arrays.asList(slots).indexOf(slot), null);
		}
		else if (e.equals(Events.slotReleased))
		{
			ItemSlot slot = (ItemSlot) e.getParam("slot");
			if (!Viewport.isSceneEnabled()) return;
			
			pickedUp = null;
			player.getEquipment().setHotbarItem(Arrays.asList(slots).indexOf(slot), slot.getItem());
		}
	}
	
	@Override
	public ItemSlot getPickedUpItemSlot()
	{
		return pickedUp;
	}
	
	@Override
	public void setPickedUpItemSlot(ItemSlot item)
	{
		pickedUp = item;
	}
	
	@Override
	public ItemSlot getFirstSlot(Item item)
	{
		return null;
	}
	
	@Override
	public Map getMap()
	{
		return m;
	}
	
	@Override
	public void showContextMenu(ItemSlot slot, int x, int y)
	{}
	
	@Override
	public void hideContextMenu()
	{}
}
