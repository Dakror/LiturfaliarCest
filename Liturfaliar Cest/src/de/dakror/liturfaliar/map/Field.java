package de.dakror.liturfaliar.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.listener.MapEventListener;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.data.FieldData;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.Talk;

public class Field implements MapEventListener
{
  int           x;
  int           y;
  double        l;
  
  String        tileset;
  BufferedImage i;
  FieldData[]   datas;
  
  public Field(int dx, int dy, int tx, int ty, double layer, String t, FieldData... data)
  {
    this.x = dx;
    this.y = dy;
    this.l = layer;
    this.tileset = t;
    this.datas = data;
    this.i = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
    this.i.getGraphics().drawImage(Viewport.loadImage("Tiles/" + t + ".png"), 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, tx * CFG.FIELDSIZE, ty * CFG.FIELDSIZE, tx * CFG.FIELDSIZE + CFG.FIELDSIZE, ty * CFG.FIELDSIZE + CFG.FIELDSIZE, null);
  }
  
  public BufferedImage getImage()
  {
    return this.i;
  }
  
  public void drawData(Map m, Graphics2D g, Viewport v)
  {
    for (FieldData fd : this.datas)
      fd.draw(m, this, g, v);
  }
  
  public void update(Map m)
  {
    for (FieldData fd : this.datas)
      fd.update(m, this);
  }
  
  @Override
  public void fieldTouched(Creature c, Map m)
  {
    for (FieldData fd : this.datas)
      fd.fieldTouched(c, m);
  }
  
  @Override
  public void fieldTriggered(Creature c, Map m)
  {
    for (FieldData fd : this.datas)
      fd.fieldTriggered(c, m);
  }
  
  @Override
  public void talkStarted(Talk t, Map m)
  {}
  
  @Override
  public void talkEnded(Talk t, Map m)
  {}
  
  @Override
  public void talkChanged(Talk old, Talk n, Map m)
  {}
  
  public int getX()
  {
    return x;
  }
  
  public void setX(int x)
  {
    this.x = x;
  }
  
  public int getY()
  {
    return y;
  }
  
  public void setY(int y)
  {
    this.y = y;
  }
  
  public double getLayer()
  {
    return l;
  }
  
  public void setLayer(double l)
  {
    this.l = l;
  }
  
  public String getTileset()
  {
    return tileset;
  }
  
  public void setTileset(String tileset)
  {
    this.tileset = tileset;
  }
  
  public static FieldData[] loadFieldData(JSONObject raw)
  {
    ArrayList<FieldData> data = new ArrayList<FieldData>();
    for (Iterator<?> keys = raw.keys(); keys.hasNext();)
    {
      String k = (String) keys.next();
      try
      {
        JSONObject o = raw.getJSONObject(k);
        FieldData fd = (FieldData) Class.forName("de.dakror.liturfaliar.map.data." + k).newInstance();
        fd.loadData(o);
        data.add(fd);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return data.toArray(new FieldData[] {});
  }
  
  @Override
  public String toString()
  {
    return getClass() + "[x=" + x + ", y=" + y + ", l=" + l + ", tileset=" + tileset + "]";
  }
}
