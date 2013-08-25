package de.dakror.liturfaliar.map.creature.ai.path;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.util.Vector;

public class Path
{
  ArrayList<Vector> nodes = new ArrayList<Vector>();
  int               index;
  
  public Path(Vector... vectors)
  {
    nodes.addAll(Arrays.asList(vectors));
    index = 0;
  }
  
  public void draw(Graphics2D g, Map m)
  {
    for (int i = 1; i < nodes.size(); i++)
    {
      Color oldColor = g.getColor();
      g.setColor(Color.orange);
      g.drawLine(m.getX() + (int) nodes.get(i).x, m.getY() + (int) nodes.get(i).y, m.getX() + (int) nodes.get(i - 1).x, m.getY() + (int) nodes.get(i - 1).y);
      g.setColor(oldColor);
    }
  }
  
  public Vector getNode()
  {
    return nodes.get(index);
  }
  
  public void setNodeReached()
  {
    index++;
  }
  
  public boolean isPathComplete()
  {
    return index == nodes.size() - 1;
  }
}
