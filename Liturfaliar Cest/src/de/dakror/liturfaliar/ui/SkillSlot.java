package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;

public class SkillSlot extends Component
{
  public static final int HGAP = 100;
  public static final int VGAP = 130;
  public static final int SIZE = 55;
  
  SkillSlot[]             children;
  private Item            item;
  
  public SkillSlot(int x, int y, Item i)
  {
    super(x, y, SIZE, SIZE);
    item = i;
    item.setWidth(SIZE);
    item.setHeight(SIZE);
    item.init();
  }
  
  public void setChildren(SkillSlot... s)
  {
    children = s;
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    if (children != null)
    {
      for (SkillSlot child : children)
      {
        Point a = new Point(x + width / 2 + 4, y + SIZE);
        Point b = new Point(child.getX() + child.getWidth() / 2 + 4, child.getY() - 2);
        if(Math.toDegrees(getAngle(a,b))<= -45) {
          a.translate(SIZE / 2 - 4, 0);
        }
        if(Math.toDegrees(getAngle(a,b))>= 45) {
          a.translate(-SIZE / 2 - 4, 0);
        }
        Assistant.Shadow(createArrowShape(a, b), Colors.ARROW, 1, g);
      }
    }
    g.drawImage(Viewport.loadImage("tileset/Wood.png"), x - 2, y - 2, SIZE + 12, SIZE + 12, null);
    item.draw(x, y, g, v);
  }
  
  public double getAngle(Point fromPt, Point toPt){
    return Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x) - Math.toRadians(90);
  }
  
  public Shape createArrowShape(Point fromPt, Point toPt)
  {
    Polygon arrowPolygon = new Polygon();
    arrowPolygon.addPoint(3, -100);
    arrowPolygon.addPoint(-3, -100);
    arrowPolygon.addPoint(-3, -10);
    arrowPolygon.addPoint(-10, -10);
    arrowPolygon.addPoint(0, 0);
    arrowPolygon.addPoint(10, -10);
    arrowPolygon.addPoint(3, -10);
    double rotate = getAngle(fromPt, toPt);
    
    AffineTransform transform = new AffineTransform();
    transform.translate(toPt.x, toPt.y);
    transform.rotate(rotate);
    return transform.createTransformedShape(arrowPolygon);
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    item.mouseMoved(e);
  }
  
  public Item getItem()
  {
    return item;
  }
}
