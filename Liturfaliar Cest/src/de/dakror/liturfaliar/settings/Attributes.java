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
    speed("Tempo"),
    attackspeed("Angriffstempo"),
    weight("Gewicht");
    
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
      attributes.put(attr, new Attribute(-1, -1));
    }
    
  }
  
  public void loadAttributes(JSONObject o)
  {
    try
    {
      for (Iterator<?> i = o.keys(); i.hasNext();)
      {
        String key = i.next().toString();
        
        if (key.indexOf("max") > -1)
          attributes.get(Attr.valueOf(key.replace("max", ""))).setMaximum(o.getInt(key));
        else attributes.get(Attr.valueOf(key)).setValue(o.getInt(key));
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public Attributes sum(Attributes o)
  {
    CFG.p(serializeAttributes());
    for (Attr attr : Attr.values())
    {
      Attribute attribute = attributes.get(attr);
      if(attribute.isEmpty() || o.attributes.get(attr).isEmpty()) continue;
      
      int max = attribute.getMaximum() + o.attributes.get(attr).getMaximum();
      
      attribute.setMaximum((max > 0) ? max : 0);
      
      int val = attribute.getValue() + o.attributes.get(attr).getValue();
      
      if (val <= attribute.getMaximum())
        attribute.setValue((val > -1) ? val : -1);
    }
    
    CFG.p(serializeAttributes());
    
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
