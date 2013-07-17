package de.dakror.liturfaliar.map.creature.ai.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Vector;

public class AStar
{
  Comparator<Node> comparator;
  ArrayList<Node>  openList;
  ArrayList<Node>  closedList;
  Field            target;
  
  public Path getPath(Field start, Field t, Map m)
  {
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
      handleNeighbors(N);
      
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
      
      
      return new Path(toVectors(path));
    }
    return null;
  }
  
  private void handleNeighbors(Node n)
  {
    for (Field neighbor : n.field.neighbors)
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
}
