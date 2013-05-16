package de.dakror.liturfaliar.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.CFG;
import de.dakror.liturfaliar.util.Assistant;

public class TileButton extends JButton
{
  private static final long serialVersionUID = 1L;
  private double            l;
  private int               x;
  private int               y;
  private String            tileset;
  private int               tx;
  private int               ty;
  private Image             i;
  public boolean            update           = true;
  private BufferedImage     image;
  public JSONObject         data;
  
  public TileButton(int x, int y, int tx, int ty, double l, String tileset, Image i)
  {
    this.l = l;
    this.x = x;
    this.y = y;
    this.tx = tx;
    this.ty = ty;
    this.i = i;
    this.image = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
    this.tileset = tileset;
    data = new JSONObject();
    // -- setup -- //
    setIcon(new ImageIcon(i));
    setContentAreaFilled(false);
    setBounds(x, y, CFG.FIELDSIZE, CFG.FIELDSIZE);
    setToolTipText("Ebene: " + l);
  }
  
  public int getX()
  {
    return x;
  }
  
  public int getY()
  {
    return y;
  }
  
  public double getLayer()
  {
    return l;
  }
  
  public void setLayer(double l)
  {
    this.l = l;
    update = true;
    setToolTipText("Ebene: " + l);
  }
  
  public int getTx()
  {
    return tx;
  }
  
  public int getTy()
  {
    return ty;
  }
  
  public String getTileset()
  {
    return tileset;
  }
  
  public void addData(String type, JSONObject d)
  {
    try
    {
      data.put(type, d);
    }
    catch (JSONException e)
    {      
      e.printStackTrace();
    }
    update = true;
  }
  
  public JSONObject getData()
  {
    return data;
  }
  
  public void removeDataByType(String type)
  {
    data.remove(type);
    update = true;
  }
  
  public JSONObject getDataByType(String type)
  {
    try
    {
      return data.getJSONObject(type);
    }
    catch (JSONException e)
    {
      return null;
    }
  }
  
  public JSONObject getSave()
  {
    JSONObject t = new JSONObject();
    try
    {
      t.put("x", getX());
      t.put("y", getY());
      t.put("l", getLayer());
      t.put("tx", getTx());
      t.put("ty", getTy());
      t.put("tileset", getTileset());
      t.put("data", getData());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return t;
  }
  
  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if (update)
    {
      image = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = (Graphics2D) image.getGraphics();
      g2.drawImage(i, 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, null);
      if (data.length() > 0)
        Assistant.Rect(0, 0, CFG.FIELDSIZE - 1, CFG.FIELDSIZE - 1, Color.blue, null, g2);
      update = false;
    }
    setIcon(new ImageIcon(image));
    
  }
}
