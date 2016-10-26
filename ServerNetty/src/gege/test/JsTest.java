package gege.test;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;




public class JsTest {
	
	public static void main(String[] args) {
		ScriptEngineManager mgr = new ScriptEngineManager();    
		ScriptEngine engine = mgr.getEngineByExtension("js");  
		try {
//			new FileReader( "D:/q.js" );
//			engine.eval( new FileReader( "D:/q.js" ));
			
			testInvokeScriptMethod( engine );
			
		} catch (FileNotFoundException | ScriptException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void testInvokeScriptMethod(ScriptEngine engine) throws Exception {  
//	    String script = "function helloFunction(name) { return 'Hello everybody,' + name;}";
//	    engine.eval(script);  
	    
	    engine.eval( new FileReader( "D:/q.js" ));
	    
//	    Invocable inv = (Invocable) engine;  
//	    String res = (String) inv.invokeFunction("getJs", "Scripting");  
//	    System.out.println("res:" + res);  
	}
}
