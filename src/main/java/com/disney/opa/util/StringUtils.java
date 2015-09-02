package com.disney.opa.util;


/**
 * Stores functions for use on Strings. 
 * 
 * @author Andy Simcox
 */
public final class StringUtils {
	
	/**
	 * Always returns a non null string
	 */
	public static String notNull(String string) {
		String result = string;
		if (string == null) result = "";
		return result;
	}
	
	/**
	 * Returns true if the passed string is null, equal to the empty string,
	 * or contains only spaces.
     */
    public static boolean isEmpty(String string) {
		return (string == null || string.trim().length() == 0);
	}
	
	/**
	 * Returns true if the passed string is non-null, and contains one or more
	 * characters that are not spaces.
	 */
	public static boolean isNotEmpty(String string) {
		return (string != null && string.trim().length() > 0);
	}

	/**
	 * Returns the input String that was passed if the input String is not empty. Otherwise, 
	 * returns the specified output String that was passed if the input String is empty.
	 */
	public static String handleEmptyString(String input, String output) {
		return isNotEmpty(input) ? input : output;
	}
	
	/**
	 * Pads the left-side of a string with a specific set of characters
	 */
	public static final String LPad(String text, 
								    int paddedLength, 
									char padCharacter) {
		int textLength = text.length();
  		if (textLength < paddedLength) {
			for (int i = 1; i <= paddedLength - textLength; i++) {
				text = padCharacter + text;
			}
  		}
		return text;
	}

	/**
	 * Pads the right-side of a string with a specific set of characters
	 */	
	public static final String RPad(String text, 
									int paddedLength, 
									char padCharacter) {
		int textLength = text.length();
		if (textLength < paddedLength) {
			for (int i = 1; i <= paddedLength - textLength; i++) {
				text = text + padCharacter;
			}
		}
		return text;
	}	

	/**
	 * Convenience methods for formatting strings.
	 */
	public static String toProperCase(String inString) {
	  String currChar = "";
	  String prevChar = " ";
	  String result = "";
	  
	  if (StringUtils.isNotEmpty(inString)) {
	  	
	  	for (int i = 0; i < inString.length(); i++) {
			currChar = inString.substring(i, i + 1);
			if (prevChar.equals(" ") || prevChar.equals("/")) {
				result = result + currChar.toUpperCase();
			} else {
				result = result + currChar.toLowerCase(); 
			}
			prevChar = currChar;
		}	  	
	  	
	  } else {
	  	
	  	result = inString;
	  	
	  }
	  
	  return result;
	}
	
	/**
	 * Convenience methods for replacing empty string with 'default' values.
	 */	
	public static String replaceIfEmpty(String value, String replaceValue) {
		String returnValue;
		
		if (StringUtils.isEmpty(value)) {
			returnValue = replaceValue;
		} else {
			returnValue = value;
		}
		
		return returnValue;
	}
	
	/**
	 * Convenience methods to call String.replaceAll() (in JVM 1.4)
	 * 
	 * TODO: Remove this function and modify Web projects with appropriate jvm for 1.4
	 *  	 which contains String.replaceAll
	 */
	public static String replaceAll(String text, String regex, String replacement) {
		return text.replaceAll(regex, replacement);
	}
	
	/**
	 * 
	 * @param string
	 * @param anotherString
	 * @return
	 */
	public static boolean safeEqualsIgnoreCase(String string, String anotherString) {
	    String s = (string == null) ? "" : string;
	    String x = (anotherString == null) ? "" : anotherString;
	    return s.equalsIgnoreCase(x);
	}
	
	/**
	 * strips all instances of a char from a text string
	 * 
	 * @param text					The text to be modified
	 * @param charToBeStripped		The character to the stripped
	 * @return						The modified string (or null)
	 */
	public static String strip(String text, char charToBeStripped) {
		String result = null;
		final char SPACE = (new Character(' ')).charValue();
		
		if (StringUtils.isNotEmpty(text)) {
			for (int i=0; i<text.length(); i++) {
				result = text.replace(charToBeStripped,SPACE);
			}	
			result = result.trim();
		}
		
		return result;
	}

}
