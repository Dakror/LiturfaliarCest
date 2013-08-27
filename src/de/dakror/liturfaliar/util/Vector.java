package de.dakror.liturfaliar.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Vector
{
	public final double x;
	public final double y;
	
	public final double length;
	private final double[] coords;
	private int dimensions;
	
	public Vector(double... d)
	{
		this.coords = d;
		this.x = coords[0];
		this.y = coords[1];
		this.dimensions = d.length;
		this.length = getLength();
	}
	
	public Vector add(Vector other)
	{
		if (this.dimensions != other.dimensions) return null;
		
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
	
	public double skalar(Vector o)
	{
		double skalar = 0;
		for (int i = 0; i < dimensions; i++)
		{
			skalar += coords[i] * o.coords[i];
		}
		return skalar;
	}
	
	public Vector neg()
	{
		return mul(-1.0D);
	}
	
	public double getAngle(Vector other)
	{
		if (this.dimensions != other.dimensions) return 0;
		
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
	
	public static double getDistance(Vector pos1, Vector pos2)
	{
		return createvec(pos1, pos2).length;
	}
	
	public double getDistance(Vector pos2)
	{
		return createvec(this, pos2).length;
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
	
	public void draw(Graphics2D g, Color c)
	{
		Assistant.Rect((int) this.coords[0], (int) this.coords[1], 1, 1, c, c, g);
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "[x=" + this.coords[0] + ",y=" + this.coords[1] + ", length=" + length + "]";
	}
	
	public Vector translate(double x, double y)
	{
		return new Vector(this.x + x, this.y + y);
	}
	
	public boolean equals(Vector o)
	{
		return Arrays.equals(coords, o.coords);
	}
	
	public Vector normalize()
	{
		return setLength(1);
	}
	
	public Vector normL()
	{
		return new Vector(-y, x);
	}
	
	public Vector normR()
	{
		return new Vector(y, -x);
	}
	
	public double dot(Vector o)
	{
		double result = 0;
		for (int i = 0; i < dimensions; i++)
		{
			result += coords[i] * o.coords[i];
		}
		return result;
	}
	
	public static Vector[] translateGroup(Vector[] v, double x, double y)
	{
		Vector[] result = new Vector[v.length];
		for (int i = 0; i < v.length; i++)
		{
			result[i] = v[i].translate(x, y);
		}
		
		return result;
	}
}
