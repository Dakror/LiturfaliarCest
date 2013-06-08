package de.dakror.liturfaliar.settings;

public class Attribute
{
  private double value, maximum;
  
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
    double sum = value + v;
    
    if (sum > 0)
    {
      if (sum < maximum)
        value += v;
      else value = maximum;
    }
    else value = 0;
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
  
  public String toString()
  {
    return getClass().getName() + "[max=" + maximum + ",value=" + value + "]";
  }
}
