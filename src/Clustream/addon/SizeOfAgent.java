package Clustream.addon;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

public class SizeOfAgent {

	static Instrumentation inst;

        static Boolean messageDisplayed;




	public static void premain(String agentArgs, Instrumentation instP) {
		inst = instP;		
	}



	public static long sizeOf(Object o) {
		if(inst == null) {
			if (messageDisplayed == null) {
				messageDisplayed = true;
	       			System.err.println("Can not access instrumentation environment.\n" +
					"Please check if jar file containing SizeOfAgent class is \n" +
					"specified in the java's \"-javaagent\" command line argument.");
			}
			return 0;
		}
		return inst.getObjectSize(o);
	}


	public static long fullSizeOf(Object obj) {
		Map<Object, Object> visited = new IdentityHashMap<Object, Object>();
		Stack<Object> stack = new Stack<Object>();

	    long result = internalSizeOf(obj, stack, visited);
	    while (!stack.isEmpty()) {
	      result += internalSizeOf(stack.pop(), stack, visited);
	    }
	    visited.clear();
	    return result;
	}		
	  
    private static boolean skipObject(Object obj, Map<Object, Object> visited) {
	    if (obj instanceof String) {
	      // skip interned string
	      if (obj == ((String) obj).intern()) {
	        return true;
	      }
	    }
	    return (obj == null) // skip visited object
	        || visited.containsKey(obj);
	 }

    private static long internalSizeOf(Object obj, Stack<Object> stack, Map<Object, Object> visited) {
	    if (skipObject(obj, visited)){
	    	return 0;
	    }
	    visited.put(obj, null);
	    
	    long result = 0;
	    // get size of object + primitive variables + member pointers 
	    result += SizeOfAgent.sizeOf(obj);
	    
	    // process all array elements
	    Class clazz = obj.getClass();
	    if (clazz.isArray()) {
	      if(clazz.getName().length() != 2) {// skip primitive type array
	    	  int length =  Array.getLength(obj);
			  for (int i = 0; i < length; i++) {
				  stack.add(Array.get(obj, i));
		      }	
	      }	      
	      return result;
	    }
	    
	    // process all fields of the object
	    while (clazz != null) {
	      Field[] fields = clazz.getDeclaredFields();
	      for (int i = 0; i < fields.length; i++) {
	        if (!Modifier.isStatic(fields[i].getModifiers())) {
	          if (fields[i].getType().isPrimitive()) {
	        	  continue; // skip primitive fields
	          } else {
	            fields[i].setAccessible(true);
	            try {
	              // objects to be estimated are put to stack
	              Object objectToAdd = fields[i].get(obj);
	              if (objectToAdd != null) {	            	
	                stack.add(objectToAdd);
	              }
	            } catch (IllegalAccessException ex) { 
	            	assert false; 
	            }
	          }
	        }
	      }
	      clazz = clazz.getSuperclass();
	    }
	    return result;
    }
}