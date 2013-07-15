package de.dakror.liturfaliar.editor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.util.Assistant;

public class NPCButton extends JButton
{
  private static final long serialVersionUID = 1L;
  
  public int                x, y, w, h, dir, ID, moveT, lookT;
  public String             name, sprite, ai;
  public double             speed;
  public boolean            move, look, hostile;
  
  public JSONArray          talk;
  public Attributes         attributes;
  
  private Equipment         equipment;
  
  public NPCButton(int x, int y, int w, int h, int dir, String name, String sprite, double speed, boolean move, boolean look, int moveT, int lookT, Image i, boolean hostile, int ID, String ai, MapEditor m)
  {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.dir = dir;
    this.moveT = moveT;
    this.lookT = lookT;
    this.move = move;
    this.look = look;
    this.name = name;
    this.sprite = sprite;
    this.speed = speed;
    this.ai = ai;
    this.ID = ID;
    this.hostile = hostile;
    
    this.attributes = new Attributes();
    this.equipment = new Equipment();
    
    this.talk = new JSONArray();
    
    setToolTipText("NPC #" + ID + ": " + name);
    setIcon(new ImageIcon(i));
    setFocusPainted(false);
    setContentAreaFilled(false);
    setBounds(x, y, w, h);
    setBorder(BorderFactory.createEmptyBorder());
    
    addMouseListener(m.new SelectionListener(this));
    addMouseMotionListener(m.new SelectionListener(this));
  }
  
  public void setEquipment(Equipment e)
  {
    equipment = e;
    if (e.isProperlyFilled())
    {
      BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      Assistant.drawChar(0, 0, w, h, dir, 0, equipment, (Graphics2D) bi.getGraphics(), null, true);
      setIcon(new ImageIcon(bi));
    }
    else
    {
      BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      Assistant.drawChar(0, 0, w, h, dir, 0, "chars", sprite, (Graphics2D) bi.getGraphics(), null, true);
      setIcon(new ImageIcon(bi));
    }
  }
  
  public Equipment getEquipment()
  {
    return equipment;
  }
  
  public JSONObject getSave()
  {
    JSONObject data = new JSONObject();
    try
    {
      data.put("x", x);
      data.put("y", y);
      data.put("w", w);
      data.put("h", h);
      data.put("id", ID);
      data.put("dir", dir);
      data.put("name", name);
      data.put("char", sprite);
      data.put("speed", speed);
      data.put("talk", talk);
      data.put("attr", attributes.serializeAttributes());
      data.put("equip", equipment.serializeEquipment());
      data.put("ai", ai);
      data.put("hostile", hostile);
      
      JSONObject random = new JSONObject();
      random.put("move", move);
      random.put("look", look);
      random.put("moveT", moveT);
      random.put("lookT", lookT);
      data.put("random", random);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return data;
  }
}
