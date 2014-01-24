package de.dakror.liturfaliarcest.editor;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.json.JSONArray;

public class Entity extends JLabel
{
	private static final long serialVersionUID = 1L;
	
	public JSONArray e;
	
	public Entity(Icon i)
	{
		super(i);
		e = new JSONArray();
	}
}
