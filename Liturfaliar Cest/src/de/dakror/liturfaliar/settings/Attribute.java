package de.dakror.liturfaliar.settings;

import java.text.DecimalFormat;

public class Attribute
{
  public static final DecimalFormat FORMAT = new DecimalFormat("#.####");
  
  private double                    value, maximum;
  
  public Attribute(double val, double max)
  {
    value = val;
    maximum = max;
  }
  
  public void decreaseValue(int v)
  {
    increaseValue(-v);
  }
  
  public double getMaximum()
  {
    return maximum;
  }
  
  public double getValue()
  {
    return value;
  }
  
  public void increaseValue(double v)
  {
    if (value + v < maximum)
      value += v;
  }
  
  public boolean isEmpty()
  {
    return maximum == 0.0 || value == 0.0;
  }
  
  public void setMaximum(double maximum)
  {
    this.maximum = maximum;
  }
  
  public void setValue(double value)
  {
    this.value = value;
  }
  
  public boolean equals(Attribute o)
  {
    return value == o.value && maximum == o.maximum;
  }
  
  public String toString()
  {
    return getClass().getName() + "[max=" + maximum + ",value=" + value + "]";
  }
}
