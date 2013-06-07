package de.dakror.liturfaliar.settings;

public class Attribute
{
  private int value, maximum;
  
  public Attribute(int val, int max)
  {
    value = val;
    maximum = max;
  }
  
  public int getValue()
  {
    return value;
  }
  
  public void setValue(int value)
  {
    this.value = value;
  }
  
  public int getMaximum()
  {
    return maximum;
  }
  
  public void setMaximum(int maximum)
  {
    this.maximum = maximum;
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
  
  public void decreaseValue(int v)
  {
    increaseValue(-v);
  }
  
  public String toString()
  {
    return getClass().getName() + "[max=" + maximum + ",value=" + value + "]";
  }
  
  public boolean isEmpty()
  {
    return maximum == -1 || value == -1;
  }
}
