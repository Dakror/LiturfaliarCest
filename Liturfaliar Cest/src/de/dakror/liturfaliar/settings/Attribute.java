package de.dakror.liturfaliar.settings;

public class Attribute
{
  private int value, maximum;
  
  public Attribute(int val, int max)
  {
    value = val;
    maximum = max;
  }
  
  public void decreaseValue(int v)
  {
    increaseValue(-v);
  }
  
  public int getMaximum()
  {
    return maximum;
  }
  
  public int getValue()
  {
    return value;
  }
  
  public void increaseValue(int v)
  {
    int sum = value + v;
    
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
    return maximum == 0 || value == 0;
  }
  
  public void setMaximum(int maximum)
  {
    this.maximum = maximum;
  }
  
  public void setValue(int value)
  {
    this.value = value;
  }
  
  public String toString()
  {
    return getClass().getName() + "[max=" + maximum + ",value=" + value + "]";
  }
}
