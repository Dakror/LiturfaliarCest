package de.dakror.liturfaliar.editor;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.CFG;

public class Character extends JButton
{
  private static final long serialVersionUID = 1L;
  
  private int               x, y, w, h, randspeed;
  private String            name, sprite;
  private double            speed;
  private boolean           random;
  
  public JSONArray          talk;
  
  public Character(int x, int y, int w, int h, String name, String sprite, double speed, boolean random, int randspeed, Image i)
  {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.randspeed = randspeed;
    this.name = name;
    this.sprite = sprite;
    this.speed = speed;
    this.random = random;
    
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
      data.put("randmd", randspeed);
      data.put("name", name);
      data.put("char", sprite);
      data.put("speed", speed);
      data.put("rand", random);
      data.put("talk", talk);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return data;
  }  
}
