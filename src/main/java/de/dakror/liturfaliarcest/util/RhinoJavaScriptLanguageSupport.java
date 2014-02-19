package de.dakror.liturfaliarcest.util;

import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.js.JavaScriptCompletionProvider;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.PreProcesssingScripts;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.type.ecma.v5.TypeDeclarationsECMAv5;
import org.fife.rsta.ac.js.engine.RhinoJavaScriptEngine;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.modes.JavaScriptTokenMaker;

public class RhinoJavaScriptLanguageSupport extends JavaScriptLanguageSupport
{
	private static final String ENGINE = RhinoJavaScriptEngine.RHINO_ENGINE;
	
	public RhinoJavaScriptLanguageSupport()
	{
		JavaScriptTokenMaker.setJavaScriptVersion("1.7");
		setECMAVersion(TypeDeclarationsECMAv5.class.getName(), getJarManager());
	}
	
	@Override
	protected JavaScriptCompletionProvider createJavaScriptCompletionProvider()
	{
		MySourceCompletionProvider provider = new MySourceCompletionProvider(this);
		JavaScriptCompletionProvider s = new JavaScriptCompletionProvider(provider, getJarManager(), this);
		return s;
	}
	
	@Override
	public void install(RSyntaxTextArea textArea)
	{
		// remove javascript support and replace with Rhino support
		LanguageSupport support = (LanguageSupport) textArea.getClientProperty("org.fife.rsta.ac.LanguageSupport");
		if (support != null)
		{
			support.uninstall(textArea);
		}
		super.install(textArea);
	}
	
	private class MySourceCompletionProvider extends SourceCompletionProvider
	{
		JavaScriptLanguageSupport support;
		
		public MySourceCompletionProvider(JavaScriptLanguageSupport support)
		{
			super(ENGINE, false);
			this.support = support;
		}
		
		@Override
		public JavaScriptLanguageSupport getLanguageSupport()
		{
			return support;
		}
		
		@Override
		public String getAlreadyEnteredText(JTextComponent comp)
		{
			PreProcesssingScripts pps = new PreProcesssingScripts(this);
			pps.parseScript(JSInvoker.mainjs, new TypeDeclarationOptions("Dakror", false, true));
			setPreProcessingScripts(pps);
			
			String text = super.getAlreadyEnteredText(comp);
			return text;
		}
	}
}
