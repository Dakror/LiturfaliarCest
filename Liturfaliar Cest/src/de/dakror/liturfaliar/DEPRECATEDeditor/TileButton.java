package de.dakror.liturfaliar.DEPRECATEDeditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Assistant;

@Deprecated
public class TileButton extends JButton
{
  private static final long serialVersionUID = 1L;
  
  public boolean            update;
  
  public boolean            fitsFilter;
  
  private int               x;
  private int               y;
  private int               tx;
  private int               ty;
  
  private double            layer;
  
  private String            tileset;
  private Image             i;
  private BufferedImage     image;
  public JSONObject         data;
  
  
  public TileButton(int x, int y, int tx, int ty, double layer, String tileset, Image i, MapEditor m)
  {
    this.layer = layer;
    this.x = x;
    this.y = y;
    this.update = true;
    this.fitsFilter = false;
    this.tx = tx;
    this.ty = ty;
    this.i = i;
    this.image = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
    this.tileset = tileset;
    data = new JSONObject();
    // -- setup -- //
    setIcon(new ImageIcon(i));
    setDisabledIcon(getIcon());
    setContentAreaFilled(false);
    setBounds(x, y, CFG.FIELDSIZE, CFG.FIELDSIZE);
    setToolTipText("Ebene: " + layer);
    setBorder(BorderFactory.createEmptyBorder());
    
    addMouseListener(m.new SelectionListener(this));
    addMouseMotionListener(m.new SelectionListener(this));
    
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
    return layer;
  }
  
  public void setLayer(double l)
  {
    this.layer = l;
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
  
  public void checkReplaceFilterFits(String tileset, Double layer, Integer tx, Integer ty)
  {
    fitsFilter = ((tileset.equals("Ignorieren")) ? true : tileset.equals(this.tileset)) && ((layer.equals(Double.NaN)) ? true : layer == this.layer) && ((tx == null) ? true : this.tx == tx) && ((ty == null) ? true : this.ty == ty);
    update = true;
    repaint();
  }
  
  public void execFilterReplace(String tileset, Double layer, Integer tx, Integer ty)
  {
    if (!fitsFilter) return;
    
    if (!tileset.equals("Ignorieren")) this.tileset = tileset;
    if (layer != Double.NaN) this.layer = layer;
    if (tx != null) this.tx = tx;
    if (ty != null) this.ty = ty;
    
    setToolTipText("Ebene: " + layer);
    
    update = true;
    repaint();
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
      
      if (data.length() > 0) Assistant.Rect(0, 0, CFG.FIELDSIZE - 1, CFG.FIELDSIZE - 1, Color.blue, null, g2);
      
      if (fitsFilter) Assistant.Rect(0, 0, CFG.FIELDSIZE - 1, CFG.FIELDSIZE - 1, Color.green, null, g2);
      
      setIcon(new ImageIcon(image));
      setDisabledIcon(getIcon());
      update = false;
    }
    
  }
}
