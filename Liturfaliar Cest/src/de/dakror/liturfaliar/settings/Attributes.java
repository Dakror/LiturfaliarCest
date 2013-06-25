package de.dakror.liturfaliar.settings;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;


public class Attributes
{
  public static enum Attr
  {
    health("Lebenspunkte", 0),
    stamina("Ausdauer", 0),
    mana("Mana", 0),
    protection("Rüstung", Double.MIN_VALUE),
    speed("Lauftempo", Double.MIN_VALUE),
    attackspeed("Angriffstempo", Double.MIN_VALUE),
    weight("Gewicht", 0),
    damage("Schaden", 0),
    cooldown("Abklingzeit", 0),
    
    strength("Stärke", 0),
    accuracy("Genauigkeit", 0),
    experience("Erfahrung", 0),
    gold("Gold", 0),
    skillpoint("Talentpunkt(e)", 0),
    
    ;
    
    private String text;
    private double minimum;
    
    private Attr(String text, double m)
    {
      this.text = text;
      this.minimum = m;
    }
    
    public double getMinimum()
    {
      return minimum;
    }
    
    public String getText()
    {
      return text;
    }
  }
  
  private HashMap<Attr, Attribute> attributes;
  
  public Attributes()
  {
    attributes = new HashMap<Attr, Attribute>();
    for (Attr attr : Attr.values())
    {
      attributes.put(attr, new Attribute(0, 0));
    }
  }
  
  public Attributes(Object... args)
  {
    this();
    
    if (args.length % 2 != 0)
      return;
    
    for (int i = 0; i < args.length; i += 2)
    {
      if (args[i] instanceof Attr)
      {
        attributes.put((Attr) args[i], new Attribute(Double.parseDouble(args[i + 1].toString()), Double.parseDouble(args[i + 1].toString())));
      }
    }
  }
  
  public Attributes(JSONObject o)
  {
    this();
    loadAttributes(o);
  }
  
  public void loadAttributes(JSONObject o)
  {
    try
    {
      for (Iterator<?> i = o.keys(); i.hasNext();)
      {
        String key = i.next().toString();
        
        if (key.indexOf("max") > -1)
          attributes.get(Attr.valueOf(key.replace("max", ""))).setMaximum(o.getDouble(key));
        
        else attributes.get(Attr.valueOf(key)).setValue(o.getDouble(key));
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public Attributes add(Attributes... os)
  {
    for (Attributes o : os)
    {
      attributes = Attributes.sum(this, o).attributes;
    }
    
    return this;
  }
  
  public Attributes sub(Attributes... os)
  {
    for (Attributes o : os)
    {
      attributes = Attributes.dif(this, o).attributes;
    }
    
    return this;
  }
  
  public JSONObject serializeAttributes()
  {
    JSONObject o = new JSONObject();
    try
    {
      
      for (Attr attr : Attr.values())
      {
        if (attributes.get(attr).isEmpty())
          continue;
        
        o.put("max" + attr.name(), attributes.get(attr).getMaximum());
        o.put(attr.name(), attributes.get(attr).getValue());
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return o;
  }
  
  public Attribute getAttribute(Attr name)
  {
    return attributes.get(name);
  }
  
  public boolean equals(Attributes o)
  {
    for (Attr attr : Attr.values())
    {
      if (!getAttribute(attr).equals(o.getAttribute(attr)))
        return false;
    }
    return true;
  }
  
  public static Attributes sum(Attributes a, Attributes b)
  {
    Attributes sum = new Attributes();
    for (Attr attr : Attr.values())
    {
      if (a.getAttribute(attr).isEmpty() && b.getAttribute(attr).isEmpty())
        continue;
      
      double max = a.getAttribute(attr).getMaximum() + b.getAttribute(attr).getMaximum();
      
      sum.getAttribute(attr).setMaximum(max);
      
      double val = a.getAttribute(attr).getValue() + b.getAttribute(attr).getValue();
      
      sum.getAttribute(attr).setValue((val <= max) ? val : max);
    }
    return sum;
  }
  
  public static Attributes dif(Attributes a, Attributes b)
  {
    Attributes sum = new Attributes();
    for (Attr attr : Attr.values())
    {
      if (a.getAttribute(attr).isEmpty() && b.getAttribute(attr).isEmpty())
        continue;
      
      double max = a.getAttribute(attr).getMaximum() - b.getAttribute(attr).getMaximum();
      
      sum.getAttribute(attr).setMaximum(max);
      
      double val = a.getAttribute(attr).getValue() - b.getAttribute(attr).getValue();
      
      sum.getAttribute(attr).setValue((val <= max) ? val : max);
      
    }
    return sum;
  }
}
