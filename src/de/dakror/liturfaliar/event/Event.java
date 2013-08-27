package de.dakror.liturfaliar.event;

import java.util.Arrays;

public class Event
{
	Events type;
	Object[] params;
	
	public Event(Events type, Object... params)
	{
		this.type = type;
		this.params = params;
	}
	
	public Events getType()
	{
		return type;
	}
	
	public Object getParam(String key)
	{
		int index = Arrays.asList(type.getKeys()).indexOf(key);
		if (index == -1) return null;
		return params[index];
	}
	
	public Object getParam(int i)
	{
		return params[i];
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Events)) return false;
		
		return type.equals((Events) o);
	}
}
