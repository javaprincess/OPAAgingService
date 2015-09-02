package com.disney.opa.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

//import javax.validation.ValidationException;

/**
 * @author eholloway
 * @author Marcos Serrano serrm042 - Jul 01, 2008
 */
public class StringUtil {

	/**
	 * 
	 * @param inString
	 * @return
	 */
	public static String filterSpecialCharacters(String inString) {
		if (isEmpty(inString))
			return inString;

		char[] newCharArray = new char[inString.length()];
		String filteredString = "";
		int strIndx = 0, newStrIndx = 0;
		int inStringLength = inString.length();
		char inChar = ' ';

		for (strIndx = 0; strIndx < inStringLength; strIndx++) {
			inChar = inString.charAt(strIndx);
			if ((Character.isLetterOrDigit(inChar)) || (inChar == '_')) {
				newCharArray[newStrIndx] = inChar;
				newStrIndx++;
			}
		}
		filteredString = new String(newCharArray).trim();
		return filteredString;
	}

	public static boolean isEmpty(String s) {
		if (s != null && s.trim().length() > 0)
			return false;
		return true;
	}
	
	//TODO: dont break in the middle of a word; break only if the location to break is a space
	public static String limitStringLength(final String s, int len) {
		StringBuffer buffer = new StringBuffer();
		String t = new String(s);
		int iCount = s.length()/len;
		for (int i=0; i<iCount; i++) {
			buffer.append(t.substring(0, len) + "<br>");
			t = t.substring(len);
		}
		buffer.append(t);
		return buffer.toString();
	}
	/**
	 * The method will return a comment translated into the user's languages
	 * This will be only valid when the user reassigns a product or when retrieves the product from his outbox
	 * Example: 
	 * 		pa.system.reassign.comment^licensee@licensee.com^licenseenato@licensee.com
	 * 			(will be saved into the productcomments table)
	 * 	
	 * 		output in English:
	 * 				Reassined from licensee@licensee.com to licenseenato@licensee.com
	 * 
	 * Example
	 * 		pa.outbox.recall.comment^associatenato@disney.com
	 * 			(will be saved into the productcomments table)
	 *  
	 * 		output in English
	 * 				This product was returned by request to: associatenato@disney.com
	 * 
	 * 		output in Spanish
	 * 				associatenato@disney.com solicito la devolucion de este producto.
	 * @param locale
	 * @param comment
	 * @return
	 */
	
	//TODO once this method call. uncomment commentSource 
	public static String getCommentTranslated(final Locale locale, final String comment) {
		final StringTokenizer tokens = new StringTokenizer(comment, "^");
		String commentSource = null;
		
		//TODO revert this code once messageUtil resolved.
//		String commentSource = MessageUtil.getMessageResources().getMessage(locale, tokens.nextToken());
		if (commentSource==null||commentSource!=null && commentSource.length()==0) {
			commentSource = comment;					
		}
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			commentSource = commentSource.replaceFirst("@@@@", token);
		}		
		return commentSource;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isPresent(String value) {
	    return (value != null && !"".equals(value));
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isPresent(String[] value) {
	    return (value != null && value.length > 0);
	}

	public static String removeLineFeed(String text) {
		 return text.replaceAll("[\r\n]", "");
	}
	
	/**
	 * returns list of integer for string that contains integers separated by vertical bar
	 * Example: 123|456|789 will return integer list container integers 123, 456, 789
	 * @param integerStringSeparatedByVerticalBar 
	 * @return list of integers
	 */
	public static List<Integer> getIntegerList(String integerStringSeparatedByVerticalBar) throws NumberFormatException{
		List<Integer> integerList = null;
		
		if(integerStringSeparatedByVerticalBar != null && integerStringSeparatedByVerticalBar.length() > 0) {
			integerList = new ArrayList<Integer>();
			String[] temp;
			String delimiter = "\\|";
			temp = integerStringSeparatedByVerticalBar.split(delimiter);

			for(int i =0; i < temp.length ; i++) {
				integerList.add(Integer.parseInt(temp[i]));
			}				
		}
		return integerList;
	}
	
	public static boolean isNumeric(String string) {
		boolean isNumeric = false;
		try {
			if (string != null && string.length() > 0) {
				Integer.parseInt(string);
				isNumeric = true;
			}
		} catch (NumberFormatException nfe) {
			//ignore
		}
		return isNumeric;
	}
	
	/**
	 * returns list of integer for string that contains integers separated by vertical bar
	 * Example: 123|456|789 will return integer list container integers 123, 456, 789
	 * @param integerStringSeparatedByVerticalBar 
	 * @return list of integers
	 */
	public static List<Integer> getIntegerList(String listAsString, String delimiter) throws NumberFormatException{
		List<Integer> integerList = null;
		
		if(listAsString != null && listAsString.length() > 0) {
			integerList = new ArrayList<Integer>();
			String[] temp;
			temp = listAsString.split("\\" + delimiter);

			for(int i =0; i < temp.length ; i++) {
				integerList.add(Integer.parseInt(temp[i]));
			}				
		}
		return integerList;
	}
	
	public static boolean isUnicode(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (Character.UnicodeBlock.of(str.charAt(i)) != Character.UnicodeBlock.BASIC_LATIN) {
				return true;
			}
		}
		return false;
	}
	
	public static String encodeString(String s, String fromEncoding, String toEncoding) {
		byte[] bytes;
		String str = "";
		try {
			bytes = s.getBytes(fromEncoding);
			str = new String(bytes, toEncoding);
		} catch (UnsupportedEncodingException e) {
			// if we cannot convert, then simply return the original
			str = s;
		}
		return str;
	}

	public static void validateMaxlenth(String s, int lenth) {
		//if (StringUtils.isNotEmpty(s) && s.length() > lenth) {
			//throw new ValidationException("Max length ( " + lenth + " ) exceeds for: " + s);
		//}
	}
}