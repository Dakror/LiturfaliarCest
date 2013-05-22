package de.dakror.liturfaliar.editor;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.CFG;

public class NPC extends JButton
{
  private static final long serialVersionUID = 1L;
  
  private int               x, y, w, h, moveT, lookT;
  private String            name, sprite;
  private double            speed;
  private boolean           move;
  private boolean           look;
  
  public JSONArray          talk;
  
  public NPC(int x, int y, int w, int h, String name, String sprite, double speed, boolean move, boolean look, int moveT, int lookT, Image i)
  {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.moveT = moveT;
    this.lookT = lookT;
    this.move = move;
    this.look = look;
    this.name = name;
    this.sprite = sprite;
    this.speed = speed;
    
    this.talk = new JSONArray();
    
    setIcon(new ImageIcon(i));
    setContentAreaFilled(false);
    setBounds(x, y, CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1]);
  }
  
  public JSONObject getData()
  {
    JSONObject data = new JSONObject();
    try
    {
      data.put("x", x);
      data.put("y", y);
      data.put("w", w);
      data.put("h", h);
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
