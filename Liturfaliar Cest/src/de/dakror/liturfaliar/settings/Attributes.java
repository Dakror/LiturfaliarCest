package de.dakror.liturfaliar.settings;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;


public class Attributes
{
  public Attributes()
  {}
  
  public void loadAttributes(JSONObject o)
  {
    try
    {
      for (Iterator<?> i = o.keys(); i.hasNext();)
      {
        String key = i.next().toString();
        
        if (key.indexOf("max") > -1)
        {
          Attribute.valueOf(key.replace("max", "")).setMaximum(o.getInt(key));
        }
        else
        {
          Attribute.valueOf(key).setValue(o.getInt(key));
        }
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public JSONObject serializeAttributes()
  {
    JSONObject o = new JSONObject();
    try
    {
      for (Attribute attr : Attribute.values())
      {
        o.put("max" + attr.name(), attr.getMaximum());
        o.put(attr.name(), attr.getValue());
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return o;
  }
  
  public Attribute getAttribute(String name)
  {
    return Attribute.valueOf(name);
  }
  
  public static enum Attribute
  {
    health, stamina, mana;
    
    private int max;
    private int value;
    
    /**
     * Attribute values of <code>-1</code> disables this attribute for the creature.<br>
     * By default all attributes are disabled.
     */
    private Attribute()
    {
      max = -1;
      value = -1;
    }
    
    public void setMaximum(int m)
    {
      max = m;
    }
    
    public void setValue(int v)
    {
      value = v;
    }
    
    public void increaseValue(int v)
    {
      int sum = value + v;
      
      if (sum > 0)
      {
        if (sum < max)
          value += v;
        else value = max;
      }
      else value = 0;
    }
    
    public void decreaseValue(int v)
    {
      increaseValue(-v);
    }
    
    public int getMaximum()
    {
      return max;
    }
    
    public int getValue()
    {
      return value;
    }
    
    public String toString()
    {
      return getClass().getName() + "." + name() + "[max=" + max + ",value=" + value + "]";
    }
  }
}
