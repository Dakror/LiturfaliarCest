package de.dakror.liturfaliarcest.editor;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.json.JSONObject;

public class Entity extends JLabel
{
	private static final long serialVersionUID = 1L;
	
	public JSONObject e; // events
	public JSONObject m; // meta
	
	public Entity(Icon i)
	{
		super(i);
		e = new JSONObject();
		m = new JSONObject();
	}
}
