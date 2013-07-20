package de.dakror.liturfaliar.map.creature.ai.path;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Vector;

public class AStar
{
  Comparator<Node> comparator;
  ArrayList<Node>  openList;
  ArrayList<Node>  closedList;
  Field            target;
  
  Node             spec1, spec2;
  
  int              oX, oY, cW, cH;
  
  public Path getPath(Field start, Field t, Map m, int x, int y, int w, int h)
  {
    oX = x;
    oY = y;
    cW = w;
    cH = h;
    
    comparator = new Comparator<Node>()
    {
      @Override
      public int compare(Node o1, Node o2)
      {
        return (int) Math.floor(o1.F - o2.F + 0.5);
      }
    };
    openList = new ArrayList<>();
    closedList = new ArrayList<>();
    target = t;
    
    Node startNode = new Node(start, null, 0, start.getNode().getDistance(target.getNode()));
    startNode.start = true;
    openList.add(startNode);
    
    Node N = null;
    
    int status = 0;
    int loops = 0;
    
    while (true)
    {
      if (openList.size() == 0) // no way
      {
        status = 1;
        break;
      }
      
      Collections.sort(openList, comparator);
      N = openList.get(0);
      
      if (N.H == 0) // found way, current node is target
      {
        status = 2;
        break;
      }
      openList.remove(0);
      closedList.add(N);
      handleNeighbors(N, m);
      
      loops++;
      if (loops % 500 == 0)
      {
        CFG.b("loops", loops, "ol", openList.size(), "cl", closedList.size()); // emergency exit
        return null;
      }
    }
    
    if (CFG.UIDEBUG)
      CFG.p("[AStar]: Found path: " + (status == 2));
    
    if (status == 2)
    {
      ArrayList<Node> path = new ArrayList<>();
      Node P = N;
      while (P.P != null)
      {
        path.add(P);
        P = P.P;
      }
      Collections.reverse(path);
      
      if (path.size() == 0)
        return null;
      
      Path path2 = improvePath(path, m);
      
      return path2;
    }
    return null;
  }
  
  public void drawPath(Graphics2D g, Map m)
  {
    try
    {
      for (Node node : openList)
      {
        Assistant.Shadow(new Arc2D.Double(node.field.getNode().x + m.getX() - 8, node.field.getNode().y + m.getY() - 8, 16, 16, 0, 360, Arc2D.PIE), Color.blue, 1, g);
      }
      for (Node node : closedList)
      {
        Assistant.Shadow(new Arc2D.Double(node.field.getNode().x + m.getX() - 6, node.field.getNode().y + m.getY() - 6, 12, 12, 0, 360, Arc2D.PIE), Color.red, 1, g);
      }
      if (spec1 != null && spec2 != null)
      {
        Assistant.Shadow(new Arc2D.Double(spec1.field.getNode().x + m.getX() - 2, spec1.field.getNode().y + m.getY() - 2, 16, 16, 0, 360, Arc2D.PIE), Color.orange, 1, g);
        Assistant.Shadow(new Arc2D.Double(spec2.field.getNode().x + m.getX() - 2, spec2.field.getNode().y + m.getY() - 2, 16, 16, 0, 360, Arc2D.PIE), Color.green, 1, g);
        
      }
    }
    catch (Exception e)
    {}
  }
  
  private void handleNeighbors(Node n, Map m)
  {
    for (Field neighbor : m.getNeighbors(n.field))
    {
      Node node = new Node(neighbor, n, n.G + 1, neighbor.getNode().getDistance(target.getNode()));
      if (neighbor.getLayer() < CFG.PLAYERLAYER && !closedList.contains(node))
      {
        if (openList.contains(node) && openList.get(openList.indexOf(node)).G > n.G + 1)
        {
          openList.get(openList.indexOf(node)).G = n.G + 1;
          openList.get(openList.indexOf(node)).P = n;
        }
        else if (!openList.contains(node))
        {
          openList.add(node);
        }
      }
    }
  }
  
  private Vector[] toVectors(ArrayList<Node> nodes)
  {
    Vector[] v = new Vector[nodes.size()];
    for (int i = 0; i < nodes.size(); i++)
    {
      v[i] = nodes.get(i).field.getNode();
    }
    return v;
  }
  
  private Path improvePath(ArrayList<Node> nodes, Map m)
  {
    ArrayList<Node> path = new ArrayList<>();
    path.add(nodes.get(0));
    
    int index = 0;
    
    while (index < nodes.size() - 1)
    {
      for (int i = nodes.size() - 1; i > index; i--)
      {
        if (m.isLineAccessible(nodes.get(index).field.getNode(), nodes.get(i).field.getNode(), -CFG.FIELDSIZE / 2 + (CFG.FIELDSIZE - cW) / 2, -CFG.FIELDSIZE / 2 + (CFG.FIELDSIZE - cH) / 2, cW, cH))
        {
          path.add(nodes.get(i));
          index = i;
          break;
        }
        else if (i == index + 1)
        {
          spec1 = nodes.get(index);
          spec2 = nodes.get(i);
          CFG.p("can't get to next node: " + i);
        }
      }
    }
    
    return new Path(toVectors(path));
  }
}
