package com.disney.compliance.aging.POC.util;

import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Utils {

    private static Utils instance = new Utils();

    
    
	@Autowired
	EmailUtils emailUtils;	
	/*

	@Autowired
	JbossExceptionsDao jbossExceptionsDao;
	*/
	public static final String TERMCODE_BY_DAY_FORMAT_STRING = "dd-MM-yyyy";
	public static final String TERMCODE_BY_TIMESTAMP_FORMAT_STRING = "dd-MM-yyyy k:m";
	public static final String JDBC_DAY_FORMAT_STRING = "mm-dd-yy";
	public static final String JDBC_TIMESTAMP_FORMAT_STRING = "MM-dd-yy H:m";
	
	final Logger log = Logger.getLogger(this.getClass().getName());

    private Utils() {
    }

    public static synchronized Utils getInstance() {
        return instance;
    }
    
	public static String formatDateString(DateFormat df, String dateString){
    	try
    	{
    		return df.format(df.parse(dateString));
    	}
    	catch(ParseException p)
    	{
    		return null;
    	}
    	
    }
    
    public static  String formatDateTimeString(DateFormat df, String dateString){
    	try
    	{
    		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(df.parse(dateString));
    	}
    	catch(ParseException p)
    	{
    		return null;
    	}
    	
    }
    
	
    public void LogExceptionAndSendEmail(String methodName, Exception ex) {
		try {
			/*
			JbossExceptions jbe = new JbossExceptions();
			jbe.setMethodName(methodName);
			jbe.setStackTrace(getStackTrace(ex).substring(0,4000));
			Long id = jbossExceptionsDao.createRecord(jbe);
			*/
			emailUtils.sendExceptionEmail(methodName,getStackTrace(ex));
		}
		catch (Exception internalException){
			log.error("internalException: " + internalException + " stacktrace: " + getStackTrace(internalException));
		}
    }
	 
    public static String getStackTrace(Exception ex) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString(); // stack trace as a string
    }

    public boolean isNumber(String arg) {
        if (arg == null) {
            return false;
        }
        if (arg.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
            return true;
        }
        return false;
    }

    public Date sqlCurDateTime() {
        return new java.sql.Timestamp(new java.util.Date().getTime());
    }

    public Date sqlInitDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = (Date) format.parse("1970-01-01");
            return new java.sql.Timestamp(date.getTime());
        } catch (Exception ex) {
            //just in case it blows up return something
            return sqlCurDateTime();
        }
    }

    public Date dateFromString(String dateStr, String datetimeFormat) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(datetimeFormat);
        try {
            Date date = (Date) format.parse(dateStr);
            return new java.sql.Timestamp(date.getTime());
        } catch (Exception ex) {
            throw new Exception("Date must be in " + datetimeFormat + " format");
        }
    }

    public String dateStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public String dateTimeStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(date);
    }

    public Long curTimestamp() {
        Date now = new Date();
        Timestamp ts = new Timestamp(now.getTime());
        return ts.getTime();
    }

    public String escapeForXML(String aStr) {
        log.debug("........begin");
        if (aStr == null) {
            return aStr;
        }
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(aStr);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                //result.append("&#039;");
                result.append("&apos;");
            } else if (character == '&') {
                result.append("&amp;");
            } else {
                result.append(character);
            }
            character = iterator.next();
        }

        log.debug("..........end");
        return result.toString();
    }

    public boolean isValidFilename(String filename) {
        final char[] BAD_CHARS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

        for (int i = 0; i < BAD_CHARS.length; i++) {
            if (filename.indexOf(BAD_CHARS[i]) > 0) {
                return false;
            }
        }
        return true;
    }

    public String toSQLString(String strIn) {
		if (strIn == null || strIn.length() == 0) {
			return "";
		}
        String strOut = "";
        if (strIn.indexOf("'") != -1) {
            int index1 = 0;
            int index2 = 0;
            while ((index2 = strIn.indexOf("'", index1)) > -1) {
                if (strIn.indexOf("''", index1) == index2) {
                    strOut += strIn.substring(index1, index2);
                    index1 = index2 + 1;
                } else {
                    strOut += strIn.substring(index1, index2) + "''";
                }
                index1 = index2 + 1;
            }
            strOut += strIn.substring(index1, strIn.length());
        } else {
            strOut = strIn;
        }
        return strOut;
    }

    public String trimWhitespace(String inputStr) {
        //remove non-printable chars from inputStr
        return inputStr.replaceAll("\\p{Cntrl}", "");
    }
	
	public String XMLEncode(String strIn, boolean encodeExtended){
        String strOut = "";
        
        if (strIn.indexOf("&") != -1) {
            int index1 = 0;
            int index2 = 0;
            
            try {
                while((index2 = strIn.indexOf("&", index1)) > -1) {
                    if (strIn.indexOf("&amp;", index1) == index2) {
                        strOut += strIn.substring(index1, index2 + 1);
                    } else if (strIn.indexOf("&lt;", index1) == index2) {
                        strOut += strIn.substring(index1, index2 + 1);
                    } else if (strIn.indexOf("&gt;", index1) == index2) {
                        strOut += strIn.substring(index1, index2 + 1);
                    } else if (strIn.indexOf("&quot;", index1) == index2) {
                        strOut += strIn.substring(index1, index2 + 1);
                    } else if (strIn.indexOf("&apos;", index1) == index2) {
                        strOut += strIn.substring(index1, index2 + 1);
                    } else {
                        strOut += strIn.substring(index1, index2) + "&amp;";
                    }
                    index1 = index2 + 1;
                }
                strOut += strIn.substring(index1, strIn.length());
            } catch (Exception e) {
                System.out.println("Exception in XMLEncode: " + e.getStackTrace());
                strOut = strIn; //just to return something
            }
        } else {
            strOut = strIn;
        }
        
        if (encodeExtended) {
            strOut = strOut.replace("<", "&lt;");
            strOut = strOut.replace(">", "&gt;");
            strOut = strOut.replace("\"", "&quot;");
            strOut = strOut.replace("'", "&apos;");
        }
		
        return strOut; 
    }
	
	
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }
    
 
    
	public static String decodeUTF8(String s) {
		try {
			if (s != null) {
				return URLDecoder.decode(s, "UTF-8");
			}
		} catch (Exception e) {
			System.out.println("Exception decodeUTF8, s: " + s);
		}
		return s;
	}
}
