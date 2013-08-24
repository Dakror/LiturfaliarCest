package de.dakror.liturfaliar.map;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.Event;
import de.dakror.liturfaliar.event.Listener;
import de.dakror.liturfaliar.map.data.FieldData;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Vector;

public class Field implements Listener
{
  int           x;
  int           y;
  double        l;
  
  String        tileset;
  BufferedImage i;
  FieldData[]   datas;
  Area          area;
  
  public Field(int dx, int dy, int tx, int ty, double layer, String t, FieldData... data)
  {
    this.x = dx;
    this.y = dy;
    this.l = layer;
    this.tileset = t;
    this.datas = data;
    this.i = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
    this.i.getGraphics().drawImage(Viewport.loadImage("Tiles/" + t + ".png"), 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, tx * CFG.FIELDSIZE, ty * CFG.FIELDSIZE, tx * CFG.FIELDSIZE + CFG.FIELDSIZE, ty * CFG.FIELDSIZE + CFG.FIELDSIZE, null);
    if (l > CFG.PLAYERLAYER) area = Assistant.toArea(i);
  }
  
  public BufferedImage getImage()
  {
    return this.i;
  }
  
  public Area getArea(Map m)
  {
    if (m == null) return area.createTransformedArea(AffineTransform.getTranslateInstance(x, y));
    return area.createTransformedArea(AffineTransform.getTranslateInstance(m.getX() + x, m.getY() + y));
  }
  
  public void drawData(Map m, Graphics2D g)
  {
    for (FieldData fd : this.datas)
      fd.draw(m, this, g);
  }
  
  public void update(Map m)
  {
    for (FieldData fd : this.datas)
      fd.update(m, this);
  }
  
  @Override
  public void onEvent(Event e)
  {
    for (FieldData fd : this.datas)
      fd.onEvent(e);
  }
  
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
  
  public Vector getNode()
  {
    return new Vector(x + CFG.FIELDSIZE / 2, y + CFG.FIELDSIZE / 2);
  }
  
  public String uID()
  {
    return Assistant.MD5(toString().getBytes());
  }
}
