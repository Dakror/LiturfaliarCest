package de.dakror.liturfaliar.editor;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NPCButton extends JButton
{
  private static final long serialVersionUID = 1L;
  
  public int                x, y, w, h, dir, moveT, lookT;
  public String             name, sprite;
  public double             speed;
  public boolean            move;
  public boolean            look;
  
  public JSONArray          talk;
  
  public NPCButton(int x, int y, int w, int h, int dir, String name, String sprite, double speed, boolean move, boolean look, int moveT, int lookT, Image i)
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
    
    this.talk = new JSONArray();
    
    setToolTipText("NPC: " + name);
    setIcon(new ImageIcon(i));
    setContentAreaFilled(false);
    setBounds(x, y, w, h);
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
      data.put("dir", dir);
      data.put("name", name);
      data.put("char", sprite);
      data.put("speed", speed);
      data.put("talk", talk);
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
