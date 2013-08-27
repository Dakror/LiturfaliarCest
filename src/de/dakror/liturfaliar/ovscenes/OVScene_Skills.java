package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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
import de.dakror.liturfaliar.item.action.SkillAction;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.Keys;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.Flicker;
import de.dakror.liturfaliar.ui.Flicker.FlickObject;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.SkillSlot;
import de.dakror.liturfaliar.ui.hud.PlayerHotbar;
import de.dakror.liturfaliar.util.Assistant;

public class OVScene_Skills extends OVScene implements Inventory
{
	Scene_Game sg;
	Container c1;
	int lastSelectedTree = -1;
	Flicker flicker;
	
	ItemSlot[] hotbar;
	ArrayList<SkillSlot> slots = new ArrayList<SkillSlot>();
	
	int rX;
	int rY;
	
	ItemSlot pickedUp;
	ItemSlot pickUpSource;
	
	public OVScene_Skills(Scene_Game sg)
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
		
		hotbar = ItemSlot.createSlotRow(Viewport.w.getWidth() / 2 - PlayerHotbar.SLOTCOUNT * ItemSlot.SIZE / 2, Viewport.w.getHeight() - ItemSlot.SIZE, PlayerHotbar.SLOTCOUNT);
		
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
		
		int h = 96 - 36;
		flicker = new Flicker(Viewport.w.getWidth() / 2 - 400, Viewport.w.getHeight() - 200, 800, 96, new FlickObject(9, 8, h, Types.PERKSKILL.getName()), new FlickObject(1, 0, h, Types.SWORDSKILL.getName()), new FlickObject(0, 253, h, Types.BOWSKILL.getName()));
	}
	
	@Override
	public void destruct()
	{
		Dispatcher.removeListener(this);
	}
	
	@Override
	public void update(long timePassed)
	{
		flicker.update();
		
		if (flicker.getSelectedIndex() != lastSelectedTree)
		{
			lastSelectedTree = flicker.getSelectedIndex();
			loadSkillTree();
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Assistant.Shadow(Viewport.w.getBounds(), Color.black, 0.6f, g);
		c1.draw(g);
		Assistant.drawHorizontallyCenteredString("Fähigkeiten", Viewport.w.getWidth(), 43, g, 45, Color.white);
		
		// -- tree area -- //
		Assistant.stretchTileset(Viewport.loadImage("tileset/EmbededWood.png"), Viewport.w.getWidth() / 2 - 400, 100, 800, Viewport.w.getHeight() - 300, g);
		for (SkillSlot ss : slots)
		{
			ss.drawArrows(g);
		}
		
		for (SkillSlot ss : slots)
		{
			ss.draw(g);
		}
		
		// -- skillpoints -- //
		Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), Viewport.w.getWidth() / 2 + 400, 100, 192, 96, g);
		String string = (int) sg.getPlayer().getAttributes().getAttribute(Attr.skillpoint).getValue() + " SP";
		int size = 70;
		Font oldFont = g.getFont();
		g.setFont(new Font("Times New Roman", 0, 1));
		if (g.getFontMetrics(g.getFont().deriveFont(size)).stringWidth(string) > 50) size -= g.getFontMetrics(g.getFont().deriveFont(size)).stringWidth(string) - 50;
		
		Assistant.drawHorizontallyCenteredString(string, Viewport.w.getWidth() / 2 + 410, 172, 174, g, size, Color.white);
		g.setFont(oldFont);
		// -- type area -- //
		flicker.draw(g);
		
		// -- hotbar -- //
		for (ItemSlot is : hotbar)
		{
			is.draw(g);
		}
		
		// -- tooltips -- //
		for (SkillSlot is : slots)
		{
			is.getItem().tooltip.draw(g);
		}
		
		for (ItemSlot is : hotbar)
		{
			is.drawTooltip(g);
		}
		
		if (pickedUp != null)
		{
			if (pickedUp instanceof SkillSlot) ((SkillSlot) pickedUp).getItem().draw(g);
			else pickedUp.drawLightWeight(g);
		}
	}
	
	public void loadSkillTree()
	{
		int x = Viewport.w.getWidth() / 2 - 400;
		
		int y = 100 + 50;
		int width = 800;
		int cx = x + width / 2 - SkillSlot.SIZE / 2;
		
		slots.clear();
		
		String key = flicker.getSelectedObject().getKey();
		if (key.equals(Types.SWORDSKILL.getName()))
		{
			SkillSlot s = new SkillSlot(cx, y, new Item(Items.SWORD0, 1), sg);
			slots.add(s);
			
			SkillSlot s1 = new SkillSlot(cx - SkillSlot.HGAP * 3, y, new Item(Items.SWORD1, 1), sg);
			slots.add(s1);
			
			SkillSlot s2 = new SkillSlot(cx - SkillSlot.HGAP * 3, y + SkillSlot.VGAP, new Item(Items.SWORD2, 1), sg);
			slots.add(s2);
			
			SkillSlot s3 = new SkillSlot(cx - SkillSlot.HGAP * 3, y + SkillSlot.VGAP * 2, new Item(Items.SWORD3, 1), sg);
			slots.add(s3);
			
			for (SkillSlot slot : slots)
			{
				SkillAction sa = (SkillAction) slot.getItem().getAction();
				if (sa.getParents().length == 0) continue;
				
				ArrayList<SkillSlot> parents = new ArrayList<SkillSlot>();
				
				for (SkillSlot slot1 : slots)
				{
					if (slot1.equals(slot)) continue;
					
					for (Items parent : sa.getParents())
					{
						if (slot1.getItem().equals(new Item(parent, 1))) parents.add(slot1);
					}
				}
				slot.setParents(parents.toArray(new SkillSlot[] {}));
			}
		}
	}
	
	@Override
	public void onEvent(Event e)
	{
		if (e.equals(Events.slotPressed))
		{
			ItemSlot slot = (ItemSlot) e.getParam("slot");
			
			if (slot instanceof SkillSlot) pickedUp = new SkillSlot((SkillSlot) slot);
			
			else
			// hotbar
			{
				pickedUp = new ItemSlot(slot);
				pickedUp.setHotKey(-1, false);
				sg.getPlayer().getEquipment().setHotbarItem(Arrays.asList(hotbar).indexOf(slot), null);
				slot.setItem(null);
			}
		}
		else if (e.equals(Events.slotHovered))
		{
			// fake usage: updating tooltips
			for (SkillSlot s : slots)
			{
				s.getItem().updateTooltip();
			}
		}
		else if (e.equals(Events.slotReleased))
		{
			ItemSlot slot = (ItemSlot) e.getParam("slot");
			if (slot instanceof ItemSlot) // hotbar
			{
				if (slot.getItem().getType().getCategory().equals(Categories.SKILL))
				{
					for (int i = 0; i < PlayerHotbar.SLOTCOUNT; i++)
					{
						if (sg.getPlayer().getEquipment().getHotbarItem(i) == null || Arrays.asList(hotbar).indexOf(slot) == i) continue;
						
						if (sg.getPlayer().getEquipment().getHotbarItem(i).equals(slot.getItem()))
						{
							sg.getPlayer().getEquipment().setHotbarItem(i, null);
							hotbar[i].setItem(null);
						}
					}
				}
				
				sg.getPlayer().getEquipment().setHotbarItem(Arrays.asList(hotbar).indexOf(slot), slot.getItem());
			}
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
		return null;
	}
	
	@Override
	public void showContextMenu(ItemSlot slot, int x, int y)
	{
		if (slot.getItem().getType().getCategory().equals(Categories.SKILL))
		{
			slot.setItem(null);
			sg.getPlayer().getEquipment().setHotbarItem(Arrays.asList(hotbar).indexOf(slot), null);
		}
	}
	
	@Override
	public void hideContextMenu()
	{}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == Keys.SKILLS)
		{
			sg.setPaused(false);
			Viewport.setFramesFrozen(false);
			Viewport.removeOVScene("Skills");
			Viewport.skipEvent = e;
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		for (ItemSlot slot : hotbar)
		{
			slot.mouseMoved(e);
		}
		
		for (SkillSlot slot : slots)
		{
			slot.mouseMoved(e);
		}
		
		if (pickedUp != null)
		{
			pickedUp.mouseMoved(e);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		for (ItemSlot slot : hotbar)
		{
			slot.mousePressed(e);
		}
		
		for (SkillSlot slot : slots)
		{
			slot.mousePressed(e);
		}
		
		flicker.mousePressed(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (pickedUp != null && e.getButton() == 3)
		{
			pickedUp = null;
			return;
		}
		
		for (ItemSlot slot : hotbar)
		{
			slot.mouseReleased(e);
		}
		
		for (SkillSlot slot : slots)
		{
			slot.mouseReleased(e);
		}
		
		flicker.mouseReleased(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		for (ItemSlot slot : hotbar)
		{
			slot.mouseDragged(e);
		}
	}
}
