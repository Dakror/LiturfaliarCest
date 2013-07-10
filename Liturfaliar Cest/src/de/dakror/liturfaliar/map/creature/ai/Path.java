package de.dakror.liturfaliar.map.creature.ai;

import java.util.ArrayList;
import java.util.Arrays;

import de.dakror.liturfaliar.settings.CFG;
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
  
  public Vector getNode(int index)
  {
    return nodes.get(index);
  }
  
  public void setNodeReached(Vector v)
  {
    index = nodes.indexOf(v);
    CFG.p(index);
  }
  
  public boolean isPathComplete()
  {
    return index == nodes.size() - 1;
  }
}
