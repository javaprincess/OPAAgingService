package com.disney.compliance.aging.POC;

public class OPAUniqueId {
	
	    private static OPAUniqueId opaUniqueId;
	    private long number;
	    private long seedValue=100L; //custom seed value to begin with
	     
	    private OPAUniqueId(){ //Singleton !!!
	        number=seedValue; 
	    }
	 
	    public static OPAUniqueId getInstance(){
	        if(opaUniqueId==null){
	        	opaUniqueId=new OPAUniqueId();
	        }
	        return opaUniqueId;
	    }
	     
	    public long getNextId(){
	        number+=1L; //simple increment by 1 , you can modify this further
	        return number;
	    }

}
