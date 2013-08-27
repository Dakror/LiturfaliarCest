package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.Dispatcher;
import de.dakror.liturfaliar.event.Event;
import de.dakror.liturfaliar.event.Events;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Inventory;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Items;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.settings.Attribute;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.settings.Keys;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.Dialog;
import de.dakror.liturfaliar.ui.HTMLLabel;
import de.dakror.liturfaliar.ui.Icon;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.Notification;
import de.dakror.liturfaliar.ui.TextSelect;
import de.dakror.liturfaliar.ui.hud.PlayerHotbar;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class OVScene_Inventory extends OVScene implements Inventory
{
	public static final String USEITEM = "Benutzen";
	public static final String TRASHITEM = "Verschrotten";
	public static final String THROWITEM = "Fallen lassen";
	
	public static final int WIDTH = 12;
	public static final int HEIGHT = 11;
	
	boolean showTrashDialog;
	
	Scene_Game sg;
	Container c1;
	
	ItemSlot[] equipSlots;
	ItemSlot[] inventory;
	ItemSlot[] hotbar;
	
	ItemSlot pickedUp;
	ItemSlot pickUpSource;
	
	HTMLLabel labels1, labels2;
	HTMLLabel stats1, stats2;
	HTMLLabel invWeight;
	HTMLLabel money;
	
	Icon goldIcon;
	
	TextSelect contextMenu;
	ItemSlot contextItemSlot;
	
	public OVScene_Inventory(Scene_Game sg)
	{
		sg.setPaused(true);
		this.sg = sg;
	}
	
	@Override
	public void construct()
	{
		Dispatcher.addListener(this);
		c1 = new Container(0, 0, Viewport.w.getWidth(), 55);
		c1.tileset = null;
		
		for (Attr attr : Attr.values())
		{
			Database.setStringVar("ov_inv_attr_" + attr.name(), Attribute.FORMAT.format(sg.getPlayer().getAttributes(true).getAttribute(attr).getValue()));
		}
		
		equipSlots = new ItemSlot[12];
		
		equipSlots[0] = new ItemSlot(183, 80); // helmet
		equipSlots[0].setCategoryFilter(Categories.HELMET);
		if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.HELMET)) equipSlots[0].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.HELMET));
		
		equipSlots[1] = new ItemSlot(80, 160); // cape
		equipSlots[1].setCategoryFilter(Categories.CAPE);
		if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.CAPE)) equipSlots[1].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.CAPE));
		
		equipSlots[2] = new ItemSlot(266, 210); // shoulder
		equipSlots[2].setCategoryFilter(Categories.SHOULDER);
		if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.SHOULDER)) equipSlots[2].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.SHOULDER));
		
		equipSlots[3] = new ItemSlot(155, 240); // shirt
		equipSlots[3].setCategoryFilter(Categories.SHIRT);
		if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.SHIRT)) equipSlots[3].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.SHIRT));
		
		equipSlots[4] = new ItemSlot(99, 240); // arm
		equipSlots[4].setCategoryFilter(Categories.ARM);
		if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.ARM)) equipSlots[4].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.ARM));
		
		equipSlots[5] = new ItemSlot(210, 240); // armor
		equipSlots[5].setCategoryFilter(Categories.ARMOR);
		if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.ARMOR)) equipSlots[5].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.ARMOR));
		
		equipSlots[6] = new ItemSlot(320, 270); // gloves
		equipSlots[6].setCategoryFilter(Categories.GLOVES);
		if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.GLOVES)) equipSlots[6].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.GLOVES));
		
		equipSlots[7] = new ItemSlot(183, 305); // belt
		equipSlots[7].setCategoryFilter(Categories.BELT);
		if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.BELT)) equipSlots[7].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.BELT));
		
		equipSlots[8] = new ItemSlot(75, 330); // left wpn
		equipSlots[8].setCategoryFilter(Categories.WEAPON);
		equipSlots[8].setItem(sg.getPlayer().getEquipment().getFirstWeapon());
		
		// TODO: for weapon equipment special care is needed (2-hand weapons, etc.)
		
		equipSlots[9] = new ItemSlot(290, 330); // right wpn
		equipSlots[9].setCategoryFilter(Categories.WEAPON);
		equipSlots[9].setItem(sg.getPlayer().getEquipment().getSecondWeapon());
		
		equipSlots[10] = new ItemSlot(183, 368); // pants
		equipSlots[10].setCategoryFilter(Categories.PANTS);
		if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.PANTS)) equipSlots[10].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.PANTS));
		
		equipSlots[11] = new ItemSlot(182, 430); // boots
		equipSlots[11].setCategoryFilter(Categories.BOOTS);
		if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.BOOTS)) equipSlots[11].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.BOOTS));
		
		inventory = ItemSlot.createSlotGrid(Viewport.w.getWidth() / 2 - 180, Viewport.w.getHeight() / 2 - 350 + 110 - 52, WIDTH, HEIGHT);
		
		hotbar = ItemSlot.createSlotRow(Viewport.w.getWidth() / 2 - PlayerHotbar.SLOTCOUNT * ItemSlot.SIZE / 2, Viewport.w.getHeight() - ItemSlot.SIZE, PlayerHotbar.SLOTCOUNT);
		
		for (ItemSlot is : equipSlots)
		{
			is.setInventory(this);
		}
		
		ItemSlot.loadItemSlots(sg.getPlayer().getInventory(), inventory);
		
		for (ItemSlot is : inventory)
		{
			is.setInventory(this);
			is.update(System.currentTimeMillis() - sg.inventoryLastClosed);
			is.setCooldownFrozen(true);
		}
		
		for (int i = 0; i < hotbar.length; i++)
		{
			hotbar[i].setInventory(this);
			if (sg.getPlayer().getEquipment().getHotbarItem(i) != null)
			{
				hotbar[i].setItem(new Item(sg.getPlayer().getEquipment().getHotbarItem(i)));
				hotbar[i].setCooldownFrozen(true);
			}
			else hotbar[i].setItem(sg.getPlayer().getEquipment().getHotbarItem(i));
			
			hotbar[i].setHotKey((i < PlayerHotbar.KEYSLOTS.length) ? PlayerHotbar.KEYSLOTS[i] : PlayerHotbar.MOUSESLOTS[i - PlayerHotbar.KEYSLOTS.length], i > PlayerHotbar.KEYSLOTS.length - 1);
		}
		
		updateStats(true);
	}
	
	@Override
	public void destruct()
	{
		Dispatcher.removeListener(this);
	}
	
	@Override
	public void update(long timePassed)
	{
		if (Viewport.dialog != null && Viewport.dialog.buttons.length > 0)
		{
			if (Viewport.dialog.buttons[0].getState() == 1)
			{
				trashItem();
				Viewport.dialog = null;
			}
			else if (Viewport.dialog.buttons[1].getState() == 1)
			{
				Viewport.dialog = null;
			}
		}
		
		if (contextMenu != null)
		{
			contextMenu.update();
			
			if (contextMenu.getSelected(false) != null)
			{
				switch (contextMenu.getSelected(false))
				{
					case USEITEM:
					{
						contextItemSlot.triggerAction(getMap(), sg.getPlayer());
						break;
					}
					case TRASHITEM:
					{
						if (contextItemSlot.getCategoryFilter() != null)
						{
							Viewport.notification = new Notification("Ausger�stete Items k�nnen nicht\n\nverschrottet werden!", Notification.ERROR);
							break;
						}
						showTrashDialog = true;
						break;
					}
					case THROWITEM:
					{
						int ran = 16;
						int rx = (int) Math.round(Math.random() * ran) - ran / 2;
						int ry = (int) Math.round(Math.random() * ran) - ran / 2;
						Item item = new Item(contextItemSlot.getItem());
						sg.getMapPack().getActiveMap().addItemDrop(item, (int) sg.getPlayer().getPos().x + rx, (int) sg.getPlayer().getPos().y + ry);
						contextItemSlot.setItem(null);
						break;
					}
				}
				contextMenu = null;
			}
		}
		
		for (ItemSlot is : inventory)
		{
			is.update(timePassed);
		}
		
		for (ItemSlot is : hotbar)
		{
			is.update(timePassed);
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (showTrashDialog)
		{
			Viewport.dialog = new Dialog("Verschrotten", "Bist du sicher, dass du diesen Gegenstand verschrotten m�chtest?[br]Diese Aktion kann nicht r�ckg�ngig gemacht werden!", Dialog.MESSAGE);
			Viewport.dialog.closeDisabled = true;
			Viewport.dialog.freezeOVScene = true;
			Viewport.dialog.draw(g);
			Viewport.dialog.setButtons("Ja", "Nein");
			Viewport.dialog.update();
			showTrashDialog = false;
		}
		Assistant.Shadow(Viewport.w.getBounds(), Color.black, 0.6f, g);
		c1.draw(g);
		Assistant.drawHorizontallyCenteredString("Inventar", Viewport.w.getWidth(), 43, g, 45, Color.white);
		
		// -- inventory -- //
		Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), Viewport.w.getWidth() / 2 - 190, Viewport.w.getHeight() / 2 - 350, ItemSlot.SIZE * WIDTH + 20, 700, g);
		
		for (ItemSlot is : inventory)
		{
			is.draw(g);
		}
		
		invWeight.draw(g);
		
		if (money == null)
		{
			String gold = (int) sg.getPlayer().getAttributes().getAttribute(Attr.gold).getValue() + "";
			int length = g.getFontMetrics(g.getFont().deriveFont(0, 20)).stringWidth(gold);
			money = new HTMLLabel(Viewport.w.getWidth() / 2 - 175 + ItemSlot.SIZE * WIDTH - 40 - length, Viewport.w.getHeight() / 2 - 350 + 110 - 52 + HEIGHT * ItemSlot.SIZE - 7, length, 30, "<" + Assistant.ColorToHex(Colors.GRAY) + ";20;0>" + gold + "[br]");
			
			goldIcon = new Icon(Viewport.w.getWidth() / 2 - 175 + ItemSlot.SIZE * WIDTH - 40, Viewport.w.getHeight() / 2 - 350 + 110 - 52 + HEIGHT * ItemSlot.SIZE - 3, 30, 30, 13, 12);
		}
		money.draw(g);
		goldIcon.draw(g);
		
		// -- character equip -- //
		Assistant.stretchTileset(Viewport.loadImage("tileset/EmbededWood.png"), Viewport.w.getWidth() / 2 - 600, Viewport.w.getHeight() / 2 - 350, 410, 550, g);
		
		g.drawImage(Viewport.loadScaledImage("system/EquipGuy.png", 292, 390, Image.SCALE_FAST), Viewport.w.getWidth() / 2 - 541, Viewport.w.getHeight() / 2 - 290, Viewport.w);
		
		for (ItemSlot is : equipSlots)
		{
			is.draw(Viewport.w.getWidth() / 2 - 600, Viewport.w.getHeight() / 2 - 350, g);
		}
		
		// -- hotbar -- //
		
		for (ItemSlot is : hotbar)
		{
			is.draw(g);
		}
		
		// -- stats -- //
		Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), Viewport.w.getWidth() / 2 - 600, Viewport.w.getHeight() / 2 - 350 + 550, 410, 150, g);
		labels1.draw(g);
		stats1.draw(g);
		labels2.draw(g);
		stats2.draw(g);
		
		for (ItemSlot is : inventory)
		{
			is.drawTooltip(g);
		}
		
		for (ItemSlot is : equipSlots)
		{
			is.drawTooltip(g);
		}
		
		for (ItemSlot is : hotbar)
		{
			is.drawTooltip(g);
		}
		if (pickedUp != null) pickedUp.drawLightWeight(g);
		
		if (contextMenu != null) contextMenu.draw(g);
	}
	
	public void trashItem()
	{
		Item scrap = new Item(Items.SCRAP, 1);
		ItemSlot scrapSlot = null;
		ItemSlot nullSlot = getFirstSlot(null);
		
		for (ItemSlot is : inventory)
		{
			if (is.getItem() == null) continue;
			if (is.getItem().equals(scrap) && is.getItem().getStack() + 1 <= scrap.getType().getStackSize())
			{
				scrapSlot = is;
				break;
			}
		}
		
		if (scrapSlot != null)
		{
			scrapSlot.addItem();
			contextItemSlot.subItem();
		}
		else if (nullSlot != null)
		{
			nullSlot.setItem(scrap);
			contextItemSlot.subItem();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		
		for (ItemSlot slot : inventory)
		{
			slot.keyPressed(e);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == Keys.INVENTORY)
		{
			if (pickedUp != null)
			{
				pickUpSource.setItem(pickedUp.getItem());
				if (pickUpSource.getCategoryFilter() == null && pickUpSource.hasHotKey()) sg.getPlayer().getEquipment().setHotbarItem(Arrays.asList(hotbar).indexOf(pickUpSource), pickedUp.getItem());
			}
			
			sg.getPlayer().setInventory(ItemSlot.serializeItemSlots(inventory));
			sg.setPaused(false);
			sg.inventoryLastClosed = System.currentTimeMillis();
			Viewport.setFramesFrozen(false);
			Viewport.removeOVScene("Inventory");
			Viewport.skipEvent = e;
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		if (contextMenu != null)
		{
			contextMenu.mouseMoved(e);
			return;
		}
		
		for (ItemSlot slot : inventory)
		{
			slot.mouseMoved(e);
		}
		
		for (ItemSlot slot : equipSlots)
		{
			slot.mouseMoved(e);
		}
		
		for (ItemSlot slot : hotbar)
		{
			slot.mouseMoved(e);
		}
		
		if (pickedUp != null) pickedUp.mouseMoved(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (contextMenu != null && contextMenu.getArea().contains(e.getPoint()))
		{
			contextMenu.mousePressed(e);
			return;
		}
		
		for (ItemSlot slot : inventory)
		{
			slot.mousePressed(e);
		}
		
		for (ItemSlot slot : equipSlots)
		{
			slot.mousePressed(e);
		}
		
		for (ItemSlot slot : hotbar)
		{
			slot.mousePressed(e);
		}
		
		if (contextMenu != null && e.getButton() == 1) contextMenu = null;
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (contextMenu != null)
		{
			contextMenu.mouseReleased(e);
			return;
		}
		
		for (ItemSlot slot : inventory)
		{
			slot.mouseReleased(e);
		}
		
		for (ItemSlot slot : equipSlots)
		{
			slot.mouseReleased(e);
		}
		
		for (ItemSlot slot : hotbar)
		{
			slot.mouseReleased(e);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (contextMenu != null)
		{
			contextMenu.mouseDragged(e);
			return;
		}
		
		for (ItemSlot slot : inventory)
		{
			slot.mouseDragged(e);
		}
		
		for (ItemSlot slot : equipSlots)
		{
			slot.mouseDragged(e);
		}
		
		for (ItemSlot slot : hotbar)
		{
			slot.mouseDragged(e);
		}
	}
	
	@Override
	public void onEvent(Event e)
	{
		if (e.equals(Events.slotPressed))
		{
			ItemSlot slot = (ItemSlot) e.getParam("slot");
			
			if (slot.getCategoryFilter() != null && !slot.isOnlyLabel()) // is from equip menu
			{
				int index = Arrays.asList(equipSlots).indexOf(slot);
				switch (index)
				{
					case 8: // left weapon
						sg.getPlayer().getEquipment().setFirstWeapon(null);
						break;
					case 9: // right weapon
						sg.getPlayer().getEquipment().setSecondWeapon(null);
						break;
					default:
					{
						sg.getPlayer().getEquipment().setEquipmentItem(slot.getItem().getType().getCategory(), null);
						Dispatcher.dispatch(Events.equipmentChanged, sg.getPlayer());
					}
				}
			}
			
			else if (slot.hasHotKey()) // is from hotbar
			sg.getPlayer().getEquipment().setHotbarItem(Arrays.asList(hotbar).indexOf(slot), null);
			
			if (slot.getItem().getType().getCategory().equals(Categories.SKILL))
			{
				slot.setItem(null);
				return;
			}
			
			pickedUp = new ItemSlot(slot);
			pickedUp.setHotKey(-1, true);
			pickUpSource = slot;
			
			slot.setItem(null);
			
			updateStats(true);
			
			for (ItemSlot is : equipSlots)
			{
				if (is.getItem() != null && !is.getItem().areRequirementsSatisfied(Attributes.dif(sg.getPlayer().getAttributes(true), is.getItem().getAttributes())))
				{
					sg.getPlayer().getEquipment().setEquipmentItem(is.getItem().getType().getCategory(), null);
					Dispatcher.dispatch(Events.equipmentChanged, sg.getPlayer());
					getFirstSlot(null).setItem(is.getItem());
					is.setItem(null);
					
					updateStats(true);
				}
			}
		}
		else if (e.equals(Events.slotHovered))
		{
			ItemSlot slot = (ItemSlot) e.getParam("slot");
			if (slot.getCategoryFilter() != null && pickedUp != null && slot.getCategoryFilter().equals(pickedUp.getItem().getType().getCategory()) && !slot.isOnlyLabel()) // is from equip menu
			{
				Attributes attributes = pickedUp.getItem().getAttributes();
				
				if (slot.equals(pickedUp)) return;
				
				Attributes player = sg.getPlayer().getAttributes(false);
				Attributes totalplayer = sg.getPlayer().getAttributes(true);
				
				for (Attr attr : Attr.values())
				{
					String color = "#ffffff";
					
					if (slot.getItem() == null)
					{
						if (player.getAttribute(attr).getValue() + attributes.getAttribute(attr).getValue() > player.getAttribute(attr).getValue()) color = (attr.equals(Attr.weight)) ? Colors.WORSE : Colors.BETTER;
						else if (player.getAttribute(attr).getValue() + attributes.getAttribute(attr).getValue() < player.getAttribute(attr).getValue()) color = (attr.equals(Attr.weight)) ? Colors.BETTER : Colors.WORSE;
					}
					else
					{
						if (attributes.getAttribute(attr).getValue() > slot.getItem().getAttributes().getAttribute(attr).getValue()) color = (attr.equals(Attr.weight)) ? Colors.WORSE : Colors.BETTER;
						else if (attributes.getAttribute(attr).getValue() < slot.getItem().getAttributes().getAttribute(attr).getValue()) color = (attr.equals(Attr.weight)) ? Colors.BETTER : Colors.WORSE;
					}
					
					Database.setStringVar("ov_inv_attr_color_" + attr.name(), color);
					Database.setStringVar("ov_inv_attr_display_" + attr.name(), Attribute.FORMAT.format(totalplayer.getAttribute(attr).getValue() + attributes.getAttribute(attr).getValue() - ((slot.getItem() != null) ? slot.getItem().getAttributes().getAttribute(attr).getValue() : 0.0)));
				}
				
				updateStats(false);
			}
		}
		else if (e.equals(Events.slotReleased))
		{
			ItemSlot slot = (ItemSlot) e.getParam("slot");
			slot.setCooldownFrozen(true);
			
			if (slot.getCategoryFilter() != null && !slot.isOnlyLabel()) // is from equip menu
			{
				int index = Arrays.asList(equipSlots).indexOf(slot);
				switch (index)
				{
					case 8: // left weapon
						sg.getPlayer().getEquipment().setFirstWeapon(slot.getItem());
						break;
					case 9: // right weapon
						sg.getPlayer().getEquipment().setSecondWeapon(slot.getItem());
						break;
					default:
					{
						sg.getPlayer().getEquipment().setEquipmentItem(slot.getItem().getType().getCategory(), slot.getItem());
						Dispatcher.dispatch(Events.equipmentChanged, sg.getPlayer());
					}
				}
			}
			
			else if (slot.hasHotKey()) // is from hotbar
			sg.getPlayer().getEquipment().setHotbarItem(Arrays.asList(hotbar).indexOf(slot), slot.getItem());
			
			updateStats(true);
		}
		else if (e.equals(Events.slotExited))
		{
			updateStats(true);
		}
	}
	
	public void updateStats(boolean force)
	{
		Attributes attributes = sg.getPlayer().getAttributes(true);
		
		String w = "<" + Assistant.ColorToHex(Colors.GRAY) + ";20;0>";
		String br = "[br]";
		
		if (force)
		{
			for (Attr attr : Attr.values())
			{
				Database.setStringVar("ov_inv_attr_color_" + attr.name(), "#ffffff");
				Database.setStringVar("ov_inv_attr_" + attr.name(), Attribute.FORMAT.format(attributes.getAttribute(attr).getValue()));
				Database.setStringVar("ov_inv_attr_display_" + attr.name(), Attribute.FORMAT.format(attributes.getAttribute(attr).getValue()));
			}
		}
		String lb1 = w + Attr.protection.getText() + br +
		
		Attr.stamina.getText() + br +
		
		Attr.speed.getText() + br +
		
		Attr.attackspeed.getText() + br +
		
		Attr.weight.getText() + br;
		
		String lb2 = "";
		
		labels1 = new HTMLLabel(Viewport.w.getWidth() / 2 - 590, Viewport.w.getHeight() / 2 - 350 + 546, 130, 150, lb1);
		labels2 = new HTMLLabel(Viewport.w.getWidth() / 2 - 590 + 205, Viewport.w.getHeight() / 2 - 350 + 546, 130, 150, lb2);
		
		String st1 =
		
		w + " <%ov_inv_attr_color_" + Attr.protection.name() + "%;20;1>%ov_inv_attr_display_" + Attr.protection.name() + "%" + br +
		
		w + " <%ov_inv_attr_color_" + Attr.stamina.name() + "%;20;1>%ov_inv_attr_display_" + Attr.stamina.name() + "%" + br +
		
		w + " <%ov_inv_attr_color_" + Attr.speed.name() + "%;20;1>%ov_inv_attr_display_" + Attr.speed.name() + "%" + br +
		
		w + " <%ov_inv_attr_color_" + Attr.attackspeed.name() + "%;20;1>%ov_inv_attr_display_" + Attr.attackspeed.name() + "%" + br +
		
		w + " <%ov_inv_attr_color_" + Attr.weight.name() + "%;20;1>%ov_inv_attr_display_" + Attr.weight.name() + "% kg" + br;
		
		
		String st2 = "";
		
		if (stats1 == null) stats1 = new HTMLLabel(Viewport.w.getWidth() / 2 - 590 + 125, Viewport.w.getHeight() / 2 - 350 + 546, 97, 150, st1);
		else stats1.doUpdate(st1);
		
		if (stats2 == null) stats2 = new HTMLLabel(Viewport.w.getWidth() / 2 - 590 + 125 + 205, Viewport.w.getHeight() / 2 - 350 + 546, 97, 150, st2);
		else stats2.doUpdate(st2);
		
		if (invWeight == null) invWeight = new HTMLLabel(Viewport.w.getWidth() / 2 - 175, Viewport.w.getHeight() / 2 - 350 + 110 - 52 + HEIGHT * ItemSlot.SIZE - 7, 160, 30, w + "Gewicht: <#ffffff;20;1>" + Attribute.FORMAT.format(getInventoryWeight()) + " kg[br]");
		else invWeight.doUpdate(w + "Gewicht: <#ffffff;20;1>" + Attribute.FORMAT.format(getInventoryWeight()) + " kg[br]");
		
		for (ItemSlot is : equipSlots)
		{
			if (is.getItem() != null) is.getItem().updateTooltip();
		}
		for (ItemSlot is : inventory)
		{
			if (is.getItem() != null) is.getItem().updateTooltip();
		}
	}
	
	public double getInventoryWeight()
	{
		double w = 0.0;
		
		for (ItemSlot is : inventory)
		{
			w += is.getWeight();
		}
		
		return w;
	}
	
	@Override
	public ItemSlot getPickedUpItemSlot()
	{
		return pickedUp;
		
	}
	
	@Override
	public void setPickedUpItemSlot(ItemSlot item)
	{
		if (item != null) item.setHotKey(-1, true);
		pickedUp = item;
	}
	
	@Override
	public ItemSlot getFirstSlot(Item item)
	{
		for (ItemSlot is : inventory)
		{
			if (item != null)
			{
				if (is.getItem() != null && is.getItem().equals(item)) return is;
			}
			else if (is.getItem() == null) return is;
		}
		return null;
	}
	
	@Override
	public Map getMap()
	{
		return sg.getMapPack().getActiveMap();
	}
	
	@Override
	public void showContextMenu(ItemSlot slot, int x, int y)
	{
		if (slot.getItem() == null || slot.getItem().getType().getCategory().equals(Categories.SKILL)) return;
		
		contextItemSlot = slot;
		
		Object[] options = {};
		
		Types type = slot.getItem().getType();
		
		if (type.getCategory().equals(Categories.CONSUMABLE)) options = new Object[] { USEITEM, THROWITEM };
		
		else if (Arrays.asList(Categories.EQUIPS).indexOf(type.getCategory()) > -1 || type.getCategory().equals(Categories.WEAPON) || type.getCategory().equals(Categories.ITEM)) options = new Object[] { TRASHITEM, THROWITEM };
		
		int lx = x;
		int ly = y;
		int w = 300;
		int h = 28 * options.length + 18;
		
		if (lx + w > Viewport.w.getWidth()) lx -= (lx + w) - Viewport.w.getWidth();
		
		if (ly + h > Viewport.w.getHeight()) ly -= (ly + h) - Viewport.w.getHeight();
		
		contextMenu = new TextSelect(lx, ly, w, h, options);
	}
	
	@Override
	public void hideContextMenu()
	{
		contextMenu = null;
		contextItemSlot = null;
	}
}