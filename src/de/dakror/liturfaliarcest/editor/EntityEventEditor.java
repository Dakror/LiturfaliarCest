package de.dakror.liturfaliarcest.editor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JarManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliarcest.util.RhinoJavaScriptLanguageSupport;

/**
 * @author Dakror
 */
public class EntityEventEditor extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	public EntityEventEditor(final Entity l) throws Exception
	{
		super(Editor.currentEditor, "Entity Events bearbeiten", true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setSize(400, 350);
		setLocationRelativeTo(Editor.currentEditor);
		
		final RSyntaxTextArea a = new RSyntaxTextArea("e: " + l.e.toString(2).replaceAll("([^\\\\])(\")", "$1").replace("\\\"", "\"").replace(";", ";\n").replace("{", "{\n").replace("}", "}\n") + ",/*END*/\nm: " + fmt(l.m));
		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		lsf.register(a);
		a.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		configureLanguageSupport(a);
		
		a.setCodeFoldingEnabled(true);
		a.setAutoIndentEnabled(true);
		a.setTabsEmulated(true);
		a.setAnimateBracketMatching(false);
		a.setHighlightCurrentLine(false);
		a.setTabSize(2);
		a.setClearWhitespaceLinesEnabled(true);
		
		RTextScrollPane tsp = new RTextScrollPane(a);
		tsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		setContentPane(tsp);
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				try
				{
					String t = a.getText();
					
					// -- events -- //
					String evts = t.substring(t.indexOf("{"), t.indexOf(",/*END*/"));
					evts = evts.replace("\"", "\\\"").replace("function", "\"function").replace("}", "}\"").replace("\n", "").replace("  ", " ");
					evts = evts.substring(0, evts.length() - 1);
					
					JSONObject arr = new JSONObject(evts);
					if (JSONObject.getNames(arr) != null)
					{
						for (String k : JSONObject.getNames(arr))
							if (!arr.getString(k).startsWith("function")) throw new JSONException("No valid function declaration at event '" + k + "'");
					}
					l.e = arr;
					
					// -- meta -- //
					String meta = t.substring(t.indexOf("m: ") + "m: ".length());
					JSONObject o = new JSONObject(meta);
					l.setM(o);
				}
				catch (Exception e1)
				{
					int r = JOptionPane.showConfirmDialog(EntityEventEditor.this, "Fehlerhafte Eingabe:\n" + e1.getMessage() + "\nTrotzdem schließen?", "Fehler!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
					if (r != JOptionPane.OK_OPTION) return;
				}
				
				dispose();
			}
		});
		
		setVisible(true);
	}
	
	private void configureLanguageSupport(RSyntaxTextArea textArea) throws IOException
	{
		RhinoJavaScriptLanguageSupport support1 = new RhinoJavaScriptLanguageSupport();
		JarManager jarManager = support1.getJarManager();
		jarManager.addCurrentJreClassFileSource();
		support1.install(textArea);
		support1.getParser(textArea).setEnabled(false);
	}
	
	private String fmt(JSONObject m) throws JSONException
	{
		return m.toString(2).replaceAll("(\n)( {0,})(\")(.{1,})(\")(:)", "\n$2$4$6").replace("{\"", "{").replace("\":", ":").replace("{", "{\n").replace("}", "\n}\n").replace("\n\n", "\n");
	}
}
