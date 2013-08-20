package de.dakror.liturfaliar.map.creature.ai.path;

import de.dakror.liturfaliar.map.Field;

public class Node
{
  public boolean start, target;
  public double  F, G, H;
  public Node    P;
  public Field   field;
  
  public Node(Field f, Node p, double G, double H)
  {
    P = p;
    field = f;
    start = false;
    target = false;
    this.G = G;
    this.H = H;
    F = G + H;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof Node)) return false;
    return field.equals(((Node) o).field);
  }
  
  public String toString()
  {
    return getClass().getName() + "[P=" + P + ", F=" + F + ", G=" + G + ", H=" + H + "]";
    
  }
}
