package de.dakror.liturfaliarcest.settings;

/**
 * An object - representation of a jsonarray talk for the JS Engine
 * 
 * @author Dakror
 */
public class Talk
{
	public int index;
	public int step;
	public String flags;
	public String modifiers;
	public String text;
	
	public Talk(int index, int step, String flags, String modifiers, String text)
	{
		this.index = index;
		this.step = step;
		this.flags = flags;
		this.modifiers = modifiers;
		this.text = text;
	}
	
	public Talk()
	{
		this(0, 0, "", "", "");
	}
	
	public Talk(String over, boolean ok)
	{
		this(-1024 - (ok ? 0 : 1024), 0, "", "", "");
	}
}
