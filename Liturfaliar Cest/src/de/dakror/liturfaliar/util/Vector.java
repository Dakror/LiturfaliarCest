package de.dakror.liturfaliar.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Vector
{
  public final double[] coords;
  private int           dimensions;
  public final double   length;
  
  public Vector(double... d)
  {
    this.coords = d;
    this.dimensions = d.length;
    this.length = getLength();
  }
  
  public Vector add(Vector other)
  {
    assert (other.dimensions == this.dimensions);
    ArrayList<Double> resarray = new ArrayList<Double>();
    for (int index = 0; index < this.dimensions; index++)
    {
      double a = this.coords[index];
      double b = other.coords[index];
      resarray.add(index, Double.valueOf(a + b));
    }
    return new Vector(converttodouble(resarray));
  }
  
  public Vector sub(Vector other)
  {
    return add(other.neg());
  }
  
  public Vector mul(double skalar)
  {
    ArrayList<Double> resarray = new ArrayList<Double>();
    for (int index = 0; index < this.dimensions; index++)
    {
      resarray.add(index, Double.valueOf(this.coords[index] * skalar));
    }
    return new Vector(converttodouble(resarray));
  }
  
  public Vector neg()
  {
    return mul(-1.0D);
  }
  
  public double getAngle(Vector other)
  {
    assert (this.dimensions == other.dimensions);
    double length = this.length * other.length;
    int ints = 0;
    for (int index = 0; index < this.dimensions; index++)
    {
      ints = (int) (ints + this.coords[index] * other.coords[index]);
    }
    return Math.acos(ints / length);
  }
  
  public static Vector createvec(double[] coords1, double[] coords2)
  {
    Vector a = new Vector(coords1);
    Vector b = new Vector(coords2);
    return a.sub(b);
  }
  
  public static Vector createvec(Vector v1, Vector v2)
  {
    return v1.sub(v2);
  }
  
  public static double get_distance(Vector pos1, Vector pos2)
  {
    return createvec(pos1, pos2).length;
  }
  
  public Vector setLength(double i)
  {
    ArrayList<Double> resArray = new ArrayList<Double>();
    for (int index = 0; index < this.coords.length; index++)
    {
      resArray.add(index, Double.valueOf(this.coords[index] * (i / this.length)));
    }
    return new Vector(converttodouble(resArray));
  }
  
  private static double[] converttodouble(ArrayList<Double> ints)
  {
    double[] result = new double[ints.toArray().length];
    for (int index = 0; index < ints.toArray().length; index++)
    {
      result[index] = ((Double) ints.get(index)).doubleValue();
    }
    return result;
  }
  
  private double getLength()
  {
    double result = 0.0D;
    for (int index = 0; index < this.coords.length; index++)
    {
      result += Math.pow(this.coords[index], 2.0D);
    }
    return Math.sqrt(result);
  }
  
  public static ArrayList<Vector> astar()
  {
    return null;
  }
  
  public void draw(Graphics2D g, Color c)
  {
    Assistant.Rect((int) this.coords[0], (int) this.coords[1], 1, 1, c, c, g);
  }
  
  @Override
  public String toString()
  {
    return this.getClass().toString() + "[x=" + this.coords[0] + ",y=" + this.coords[1] + "]";
  }
}
