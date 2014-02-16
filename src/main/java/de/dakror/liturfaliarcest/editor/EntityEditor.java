package de.dakror.liturfaliarcest.editor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import de.dakror.liturfaliarcest.util.RhinoJavaScriptLanguageSupport;

import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JarManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.json.JSONException;
import org.json.JSONObject;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author Dakror
 */
public class EntityEditor extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	public EntityEditor(final Entity l) throws Exception
	{
		super(Editor.currentEditor, "Liturfaliar Cest Editor: Entity", true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(Editor.currentEditor);
		
		String e = "";
		String[] names = JSONObject.getNames(l.e);
		for (int i = 0; i < names.length; i++)
		{
			e += names[i] + ": " + new String(new BASE64Decoder().decodeBuffer(l.e.getString(names[i]))) + (i < names.length - 1 ? ",\n" : "");
		}
		
		final RSyntaxTextArea a = new RSyntaxTextArea("e: {\n" + e.replace("}", "\n}").replace("{", "{\n") + "\n}\n,/*END*/\nm: " + fmt(l.m));
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
					String evts = t.substring(t.indexOf("{"), t.indexOf(",/*END*/")).trim();
					evts = evts.substring(evts.indexOf("{") + 1, evts.lastIndexOf("}"));
					String[] functions = evts.split("},");
					functions[functions.length - 1] = functions[functions.length - 1].substring(0, functions[functions.length - 1].lastIndexOf("}"));
					
					JSONObject evt = new JSONObject();
					for (String f : functions)
					{
						String fn = f.substring(0, f.indexOf(":")).trim();
						String fb = f.substring(f.indexOf(":") + 1).replaceAll("(\n)|(\r\n)", "").trim() + "}";
						evt.put(fn, new BASE64Encoder().encode(fb.getBytes()).replace("\r\n", ""));
					}
					l.e = evt;
					
					// -- meta -- //
					String meta = t.substring(t.indexOf("m: ") + "m: ".length());
					JSONObject o = new JSONObject(meta);
					l.setM(o);
				}
				catch (Exception e1)
				{
					int r = JOptionPane.showConfirmDialog(EntityEditor.this, "Fehlerhafte Eingabe:\n" + e1.getMessage() + "\nTrotzdem schließen?", "Fehler!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
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
