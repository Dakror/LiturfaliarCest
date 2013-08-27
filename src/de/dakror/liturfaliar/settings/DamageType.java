package de.dakror.liturfaliar.settings;

import java.awt.Color;

public enum DamageType
{
	NORMAL(Color.white, "Basisschaden", 25),
	CRITICAL(Color.orange, "Kritischer Schaden", 35),
	HEAL(Color.green, "Heilung", 35),
	SPECIAL(Colors.SPECIAL, "Spezial", 35),
	FIRE(Color.red, "Feuerschaden", 25);
	
	private Color color;
	private int size;
	private String desc;
	
	private DamageType(Color c, String d, int s)
	{
		color = c;
		size = s;
		desc = d;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public String getDescription()
	{
		return desc;
	}
	
	public int getSize()
	{
		return size;
	}
}
