package de.dakror.liturfaliar.settings;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;


public class Attributes
{
  public static enum Attr
  {
    health("Lebenspunkte"),
    stamina("Ausdauer"),
    mana("Mana"),
    protection("Rüstung"),
    speed("Lauftempo"),
    attackspeed("Angriffstempo"),
    weight("Gewicht"),
    damage("Schaden"),
    
    strength("Stärke"),
    accuracy("Genauigkeit"),
    experience("Erfahrung");
    
    private String text;
    
    private Attr(String text)
    {
      this.text = text;
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
      for (Attr attr : Attr.values())
      {
        Attribute attribute = attributes.get(attr);
        if (attribute.isEmpty() && o.attributes.get(attr).isEmpty())
          continue;
        
        double max = attribute.getMaximum() + o.attributes.get(attr).getMaximum();
        
        attribute.setMaximum(max);
        
        double val = attribute.getValue() + o.attributes.get(attr).getValue();
        
        if (val <= attribute.getMaximum())
          attribute.setValue(val);
      }
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
}
