package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.dispatcher.ItemSlotEventDispatcher;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class SkillSlot extends ItemSlot
{
  public static final int HGAP = 100;
  public static final int VGAP = 130;
  public static final int SIZE = 67;
  
  SkillSlot[]             parents;
  private Item            item;
  public boolean          drawArrow;
  public boolean          known;
  
  Scene_Game              sg;
  
  public SkillSlot(int x, int y, Item i, Scene_Game sg)
  {
    super(x, y, SIZE);
    this.sg = sg;
    item = i;
    item.setWidth(SIZE - 12);
    item.setHeight(SIZE - 12);
    item.init();
    drawArrow = true;
    known = sg.getPlayer().hasSkill(item);
    item.showSkillCosts = !known;
    item.updateTooltip();
  }
  
  public SkillSlot(SkillSlot o)
  {
    this(o.x, o.y, new Item(o.item), o.sg);
    drawArrow = o.drawArrow;
    known = o.known;
    item.showSkillCosts = !known;
    item.updateTooltip();
  }
  
  public void setParents(SkillSlot... s)
  {
    parents = s;
  }
  
  @Override
  public void update()
  {}
  
  public void drawArrows(Graphics2D g, Viewport v)
  {
    if (parents != null && drawArrow)
    {
      for (SkillSlot parent : parents)
      {
        Point a = new Point(parent.getX() + parent.getWidth() / 2, parent.getY() + SIZE);
        Point b = new Point(x + width / 2, y);
        
        double angle = Math.toDegrees(getAngle(a, b));
        if (angle <= -45 && angle >= -90)
        {
          a.translate(SIZE / 2, 0);
          b.translate(-SIZE / 2, 4);
        }
        if (angle <= -90)
        {
          a.translate(-SIZE / 2, -SIZE / 2);
          b.translate(SIZE / 2, SIZE / 2);
        }
        
        if (angle >= 45 && angle <= 90)
        {
          a.translate(-SIZE / 2, 0);
          b.translate(SIZE / 2 + 4, 0);
        }
        Shape arrow = createArrowShape(a, b);
        
        Assistant.Shadow(arrow, (parent.known) ? Colors.ARROW : Colors.DARROW, 1, g);
      }
    }
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    g.drawImage(Viewport.loadImage("tileset/Wood.png"), x, y, SIZE, SIZE, null);
    item.draw(x + 2, y + 2, g, v);
    
    if (!known)
      Assistant.Shadow(item.getArea(), Colors.DGRAY, 0.6f, g);
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
  
  public boolean canLearn()
  {
    boolean can = item.areRequirementsSatisfied(sg.getPlayer().getAttributes());
    
    if (parents != null)
    {
      for (SkillSlot p : parents)
      {
        if (!p.canLearn() || !p.known)
          can = false;
      }
    }
    return can;
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    item.mouseMoved(e);
  }
  
  public void mousePressed(MouseEvent e)
  {
    if (!getArea().contains(e.getLocationOnScreen()))
      return;
    
    if (e.getButton() == 1 && e.getClickCount() == 2 && canLearn() && !known)
    {
      sg.getPlayer().getAttributes().getAttribute(Attr.skillpoint).increaseValue(-item.getRequirements().getAttribute(Attr.skillpoint).getValue());
      sg.getPlayer().addSkill(item);
      Database.setStringVar("player_sp", "" + (int) sg.getPlayer().getAttributes().getAttribute(Attr.skillpoint).getValue());
      known = true;
      item.showSkillCosts = !known;
      item.updateTooltip();
    }
    else if (e.getButton() == 1 && e.getClickCount() == 1 && known)
    {
      ItemSlotEventDispatcher.dispatchSlotPressed(e, this);
    }
  }
  
  public Item getItem()
  {
    return item;
  }
}
