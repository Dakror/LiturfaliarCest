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
  
  SkillSlot[]             parents;
  private Item            item;
  public boolean          drawArrow;
  
  public SkillSlot(int x, int y, Item i)
  {
    super(x, y, SIZE, SIZE);
    item = i;
    item.setWidth(SIZE);
    item.setHeight(SIZE);
    item.init();
    drawArrow = true;
  }
  
  public void setParents(SkillSlot... s)
  {
    parents = s;
  }
  
  @Override
  public void update()
  {}
  
  public void drawArrows(Graphics2D g, Viewport v){
    if (parents != null && drawArrow)
    {
      for (SkillSlot parent : parents)
      {
        Point a = new Point(parent.getX() + parent.getWidth() / 2 + 4, parent.getY() + SIZE);
        Point b = new Point(x + width / 2 + 4, y - 2);
        
        double angle = Math.toDegrees(getAngle(a, b));
        if (angle <= -45 && angle >= - 90)
        {
          a.translate(SIZE / 2, 0);
          b.translate(-SIZE / 2, 4);
        }
        if (angle <= - 90)
        {
          a.translate(-SIZE / 2, -SIZE / 2 +  4);
          b.translate(SIZE / 2 + 5, SIZE / 2 + 7);
        }
        
        if (angle >= 45 && angle <= 90)
        {
          a.translate(-SIZE / 2, 0);
          b.translate(SIZE / 2 + 4, 0);
        }
        Shape arrow = createArrowShape(a, b);
        
        Assistant.Shadow(arrow, Colors.ARROW, 1, g);
      }
    }
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    
    g.drawImage(Viewport.loadImage("tileset/Wood.png"), x - 2, y - 2, SIZE + 12, SIZE + 12, null);
    item.draw(x, y, g, v);    
  }
  
  public double getAngle(Point fromPt, Point toPt)
  {
    return Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x) - Math.toRadians(90);
  }
  
  public Shape createArrowShape(Point fromPt, Point toPt)
  {
    Polygon arrowPolygon = new Polygon();
    arrowPolygon.addPoint(3, ((toPt.distanceSq(fromPt) < 0) ? 1 : -1) * (int) toPt.distance(fromPt));
    arrowPolygon.addPoint(-3, ((toPt.distanceSq(fromPt) < 0) ? 1 : -1) * (int) toPt.distance(fromPt));
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
