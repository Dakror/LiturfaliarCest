package de.dakror.liturfaliar.util;

public class Projection
{
	public double min, max, length;
	
	public Projection(double min, double max)
	{
		this.min = min;
		this.max = max;
		this.length = max - min;
	}
	
	public boolean intersects(Projection o)
	{
		if (min < o.min) return max > o.min;
		else return min < o.max;
	}
	
	public double getIntersection(Projection o)
	{
		return Math.min(max, o.max) - Math.max(min, o.min);
	}
}
