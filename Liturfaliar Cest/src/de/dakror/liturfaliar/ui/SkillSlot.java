package de.dakror.liturfaliar.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.settings.Colors;

public class SkillSlot extends Component
{
  public static final int HGAP = 100;
  public static final int VGAP = 160;
  public static final int SIZE = 66;
  
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
      Color oldColor = g.getColor();
      Stroke oldStroke = g.getStroke();
      g.setColor(Colors.ARROW);
      int lineWidth = 1;
      g.setStroke(new BasicStroke(lineWidth));
      for (SkillSlot child : children)
      {
        g.fill(createArrowShape(new Point(x + width / 2 + 4, y + SIZE), new Point(child.getX() + child.getWidth() / 2 + 4, child.getY())));
      }
      g.setStroke(oldStroke);
      g.setColor(oldColor);
    }
    g.drawImage(Viewport.loadImage("tileset/Wood.png"), x - 2, y - 2, SIZE + 12, SIZE + 12, null);
    item.draw(x, y, g, v);
  }
  
  public Shape createArrowShape(Point fromPt, Point toPt)
  {
    Polygon arrowPolygon = new Polygon();
    arrowPolygon.addPoint(-6, 1);
    arrowPolygon.addPoint(3, 1);
    arrowPolygon.addPoint(3, 3);
    arrowPolygon.addPoint(6, 0);
    arrowPolygon.addPoint(3, -3);
    arrowPolygon.addPoint(3, -1);
    arrowPolygon.addPoint(-6, -1);
    
    Point midPoint = midpoint(fromPt, toPt);
    
    double rotate = Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x);
    
    AffineTransform transform = new AffineTransform();
    transform.translate(midPoint.x, midPoint.y);
    double ptDistance = fromPt.distance(toPt);
    double scale = ptDistance / 12.0; // 12 because it's the length of the arrow polygon.
    transform.scale(scale, scale);
    transform.rotate(rotate);
    return transform.createTransformedShape(arrowPolygon);
  }
  
  private static Point midpoint(Point p1, Point p2)
  {
    return new Point((int) ((p1.x + p2.x) / 2.0), (int) ((p1.y + p2.y) / 2.0));
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
