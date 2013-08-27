package de.dakror.liturfaliar.map.creature;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.Dispatcher;
import de.dakror.liturfaliar.event.Event;
import de.dakror.liturfaliar.event.Events;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.skillanim.SkillAnimation;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.ai.path.AStar;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.Balance;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.settings.Keys;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;
import de.dakror.liturfaliar.util.Vector;

public class Player extends Creature
{
	public JSONObject data;
	
	// -- up -- left -- right -- down -- //
	boolean[] dirs = { false, false, false, false };
	
	public boolean preventTargetChoose = false;
	public int dirAfterReachedGoal = -1;
	
	int frame = 0;
	
	boolean lookingEnabled;
	boolean sprint;
	long time;
	
	ArrayList<Item> skills = new ArrayList<Item>();
	
	Point mouse = new Point(0, 0);
	
	public Player(JSONObject save)
	{
		super(CFG.MAPCENTER.x, CFG.MAPCENTER.y, CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1]);
		
		setHuman();
		setLookingEnabled(true);
		
		massive = true;
		layer = CFG.PLAYERLAYER;
		setData(save);
		frozen = false;
		
		try
		{
			name = save.getJSONObject("char").getString("name");
			
			equipment = new Equipment(save.getJSONObject("char").getJSONObject("equip"));
			updateRealAreas();
			relPos = goTo = new Vector(save.getJSONObject("mappack").getJSONObject("pos").getInt("x"), save.getJSONObject("mappack").getJSONObject("pos").getInt("y"));
			
			attr.loadAttributes(save.getJSONObject("char").getJSONObject("attr"));
			
			Database.setStringVar("player_sp", "" + (int) attr.getAttribute(Attr.skillpoint).getValue());
			Database.setStringVar("player_level", "" + getLevel());
			
			JSONArray skills = save.getJSONObject("char").getJSONArray("skills");
			
			for (int i = 0; i < skills.length(); i++)
			{
				this.skills.add(new Item(skills.getJSONObject(i)));
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(long timePassed, Map m)
	{
		try
		{
			for (SkillAnimation skill : super.skills)
			{
				if (skill.isDone()) super.skills.remove(skill);
				
				else skill.update(timePassed, m);
			}
		}
		catch (ConcurrentModificationException e)
		{}
		
		if (!sprint)
		{
			if ((System.currentTimeMillis() - time) > Balance.Player.STAMINAREGEN && attr.getAttribute(Attr.stamina).getValue() < attr.getAttribute(Attr.stamina).getMaximum())
			{
				attr.getAttribute(Attr.stamina).increaseValue(1);
				time = System.currentTimeMillis();
			}
		}
		
		if (sprint && (dirs[0] || dirs[1] || dirs[2] || dirs[3]) && (System.currentTimeMillis() - time) > Balance.Player.STAMINADECREASE && attr.getAttribute(Attr.stamina).getValue() > 0 && !m.isPeaceful())
		{
			attr.getAttribute(Attr.stamina).decreaseValue(1);
			time = System.currentTimeMillis();
		}
		if (attr.getAttribute(Attr.stamina).getValue() == 0)
		{
			sprint = false;
		}
		
		setSpeed((sprint) ? Balance.Player.SPRINT : Balance.Player.WALK);
		
		double x = 0, y = 0;
		if (dirs[0] && !dirs[3] && m.getBumpMap().contains(new Rectangle2D.Double(m.getX() + relPos.x + bx, m.getY() + relPos.y + by - getSpeed() * 2, bw, bh))) y -= getSpeed();
		else if (dirs[3] && !dirs[0] && m.getBumpMap().contains(new Rectangle2D.Double(m.getX() + relPos.x + bx, m.getY() + relPos.y + by + getSpeed() * 2, bw, bh))) y += getSpeed();
		if (dirs[1] && !dirs[2] && m.getBumpMap().contains(new Rectangle2D.Double(m.getX() + relPos.x + bx - getSpeed() * 2, m.getY() + relPos.y + by, bw, bh))) x -= getSpeed();
		else if (dirs[2] && !dirs[1] && m.getBumpMap().contains(new Rectangle2D.Double(m.getX() + relPos.x + bx + getSpeed() * 2, m.getY() + relPos.y + by, bw, bh))) x += getSpeed();
		
		if (x != 0 || y != 0)
		{
			goTo = new Vector(getPos().x + x, getPos().y + y);
			setPath(null);
		}
		else
		{
			
			if (getPath() != null)
			{
				if (relPos.translate(bx + bw / 2, bh + by).equals(getPath().getNode())) getPath().setNodeReached();
				
				goTo = getPath().getNode().translate(-bx - bw / 2, -bh - by);
				
				if (getPath().isPathComplete()) setPath(null);
			}
		}
		for (Field f : m.fields)
		{
			if (getBumpArea().contains(new Point2D.Double(f.getX() + CFG.FIELDSIZE * 0.5, f.getY() + CFG.FIELDSIZE * 0.5)))
			{
				f.onEvent(new Event(Events.fieldTriggered, this, m));
			}
			else if (getBumpArea().intersects(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE))
			{
				f.onEvent(new Event(Events.fieldTouched, this, m));
			}
		}
		Vector moved = getMovePos(m);
		Vector move = moved.sub(relPos);
		
		boolean scrollLeft = m.getX() < 0;
		boolean scrollUp = m.getY() < 0;
		boolean scrollRight = m.getX() + m.getWidth() > Viewport.w.getWidth();
		boolean scrollDown = m.getY() + m.getHeight() > Viewport.w.getHeight();
		
		double sx = 0, sy = 0;
		
		Vector absPos = moved.add(new Vector(m.getX(), m.getY()));
		if ((scrollRight && move.x > 0 && absPos.x >= Viewport.w.getWidth() / 2 - CFG.FIELDSIZE) || (scrollLeft && move.x < 0 && absPos.x <= Viewport.w.getWidth() / 2 + CFG.FIELDSIZE)) sx = -move.x;
		if ((scrollDown && move.y > 0 && absPos.y >= Viewport.w.getHeight() / 2 - CFG.FIELDSIZE) || (scrollUp && move.y < 0 && absPos.y <= Viewport.w.getHeight() / 2 + CFG.FIELDSIZE)) sy = -move.y;
		if (sx == 0 && sy == 0) move(m);
		else
		{
			m.move(sx, sy);
			relPos = moved;
		}
	}
	
	@Override
	public void draw(Graphics2D g, Map m)
	{
		super.draw(g, m);
		frame = 0;
		
		if ((!relPos.equals(goTo) || !Arrays.equals(dirs, new boolean[] { false, false, false, false })) && !frozen) frame = Viewport.getFrame((sprint) ? 0.3f : 0.5f);
		
		if (lookingEnabled)
		{
			int angle = (int) Math.round(Math.toDegrees(Math.atan2(mouse.y - (relPos.y + m.getY() + h / 2), mouse.x - (relPos.x + m.getX() + w / 2))) / 90.0) + 1;
			if (angle > -1) dir = DIRS[angle];
			else dir = 1;
		}
		
		try
		{
			for (SkillAnimation skill : super.skills)
				skill.drawBelow(g, m);
			
			Assistant.drawChar((int) relPos.x + m.getX(), (int) relPos.y + m.getY(), w, h, dir, frame, equipment, g, true);
			
			for (SkillAnimation skill : super.skills)
				skill.drawAbove(g, m);
		}
		catch (Exception e)
		{}
	}
	
	public void mouseMoved(MouseEvent e, Map m)
	{
		mouse = e.getLocationOnScreen();
	}
	
	public void mousePressed(MouseEvent e, Map m)
	{
		if (e.getButton() == 1 && !e.isControlDown()) setAStarPath(e, m);
	}
	
	public void mouseDragged(MouseEvent e, Map m)
	{
		// goTo = new Vector(e.getXOnScreen() - m.getX(), e.getYOnScreen() - m.getY());
		// path = null;
	}
	
	public void mouseReleased(MouseEvent e, Map m)
	{
		if (getPath() == null)
		{
			goTo = relPos;
		}
	}
	
	private void setAStarPath(MouseEvent e, Map m)
	{
		if (m.getBumpMap().contains(e.getLocationOnScreen()))
		{
			setPath(new AStar().getPath(getField(m), m.findField(e.getXOnScreen() - m.getX(), e.getYOnScreen() - m.getY()), m, bx, by, bw, bh));
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e, Map m)
	{
		if (frozen) return;
		
		int c = e.getExtendedKeyCode();
		
		if (c == Keys.UP) dirs[0] = true;
		
		else if (c == Keys.LEFT) dirs[1] = true;
		
		else if (c == Keys.RIGHT) dirs[2] = true;
		
		else if (c == Keys.DOWN) dirs[3] = true;
		
		else if (c == Keys.SPRINT)
		{
			if (!sprint) time = System.currentTimeMillis();
			
			sprint = true;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e, Map m)
	{
		int c = e.getExtendedKeyCode();
		
		if (c == Keys.UP) dirs[0] = false;
		
		else if (c == Keys.LEFT) dirs[1] = false;
		
		else if (c == Keys.RIGHT) dirs[2] = false;
		
		else if (c == Keys.DOWN) dirs[3] = false;
		
		else if (c == Keys.SPRINT)
		{
			if (sprint) time = System.currentTimeMillis();
			
			sprint = false;
		}
	}
	
	@Override
	public Area getBumpArea()
	{
		return new Area(new Rectangle2D.Double(getPos().x + bx, getPos().y + by, bw, bh));
	}
	
	public JSONObject getData()
	{
		try
		{
			data.getJSONObject("char").put("equip", equipment.serializeEquipment());
			JSONArray s = new JSONArray();
			for (Item skill : skills)
			{
				s.put(skill.serializeItem());
			}
			data.getJSONObject("char").put("skills", s);
			data.getJSONObject("char").put("attr", attr.serializeAttributes());
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return data;
	}
	
	public void setData(JSONObject data)
	{
		this.data = data;
	}
	
	@Override
	public void onEvent(Event e)
	{
		if (e.equals(Events.equipmentChanged) && e.getParam(0).equals(this))
		{
			updateRealAreas();
		}
	}
	
	public void updateRealAreas()
	{
		if (realAreas == null) realAreas = new Area[4][4];
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Assistant.drawChar(0, 0, w, h, i, j, equipment, (Graphics2D) img.getGraphics(), true);
				realAreas[i][j] = Assistant.toArea(img);
			}
		}
	}
	
	public Area getRealArea(Map m)
	{
		if (realAreas == null) return new Area();
		return realAreas[dir][frame % 4].createTransformedArea(AffineTransform.getTranslateInstance(m.getX() + relPos.x, m.getY() + relPos.y));
	}
	
	public void setInventory(JSONArray o)
	{
		try
		{
			data.getJSONObject("char").put("inventory", o);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public void putItemInFirstInventorySlot(Item item)
	{
		try
		{
			JSONArray inv = getInventory();
			for (int i = 0; i < inv.length(); i++)
			{
				if (inv.getJSONObject(i).length() > 0)
				{
					Item cItem = new Item(inv.getJSONObject(i).getJSONObject("item"));
					if (cItem.equals(item) && cItem.getStack() < cItem.getType().getStackSize())
					{
						cItem.setStack(cItem.getStack() + 1);
						inv.put(i, ItemSlot.serializeFakeItemSlot(cItem));
						setInventory(inv);
						return;
					}
				}
			}
			for (int i = 0; i < inv.length(); i++)
			{
				if (inv.getJSONObject(i).length() == 0)
				{
					inv.put(i, ItemSlot.serializeFakeItemSlot(item));
					setInventory(inv);
					return;
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public JSONArray getInventory()
	{
		try
		{
			return data.getJSONObject("char").getJSONArray("inventory");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Attributes getAttributes()
	{
		return getAttributes(false);
	}
	
	public Attributes getAttributes(boolean equip)
	{
		if (equip) return equipment.getAttributes().add(attr);
		
		return attr;
	}
	
	public boolean hasSkill(Item skill)
	{
		for (Item item : skills)
		{
			if (item.equals(skill)) return true;
		}
		return false;
	}
	
	public void addSkill(Item skill)
	{
		skills.add(skill);
	}
	
	public void addXP(int amount)
	{
		int lvl = getLevel();
		attr.getAttribute(Attr.experience).increase(amount);
		
		if (getLevel() > lvl)
		{
			attr.getAttribute(Attr.level).increase(getLevel() - lvl);
			Dispatcher.dispatch(Events.levelUp, lvl);
		}
	}
	
	public void disableDirs()
	{
		dirs = new boolean[] { false, false, false, false };
	}
	
	public boolean isLookingEnabled()
	{
		return lookingEnabled;
	}
	
	public void setLookingEnabled(boolean lookingEnabled)
	{
		this.lookingEnabled = lookingEnabled;
	}
}
