package aging.POC.util;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.disney.opa.bean.EmailDetails;
import com.disney.opa.bean.Notification;
import com.disney.opa.service.UtilService;

import javax.mail.internet.AddressException;

@Component
public class EmailUtils {

	 
	@Autowired
	UtilService service;	
	
    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private static final String UTF_EIGHT_CHARSET_DENOTATION = "utf-8";
    private static final String DEFAULT_EMAIL_CONTENT_TYPE = "text/plain";
    private static final String HTML_EMAIL_CONTENT_TYPE = "text/html";
    private static final String HTML_WITH_UTF_EIGHT_CHARSET_EMAIL_CONTENT_TYPE = "text/html; charset=utf-8";
    private static final String PDF_APPLICATION_CONTENT_TYPE = "application/pdf";    
    private static final String DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
    
    private static final String UPDATE_EMAIL_SUBJECT = "Order ";
    private static final String UPDATE_EMAIL_SUBJECT_2 = " Updated";    
    private static final String NEW_EMAIL_SUBJECT = "Order ";
    private static final String NEW_EMAIL_SUBJECT_2 = " Submitted ";

    
    private static final String UPDATE_EMAIL_BODY = "Order ";
    private static final String UPDATE_EMAIL_BODY_1 = " updated to ";
    private static final String UPDATE_EMAIL_BODY_2 = " status";
    private static final String UPDATE_EMAIL_BODY_3 = ". Check My Orders/Ready for Download in DMC Publishing";
    private static final String NEW_EMAIL_BODY = "Your high resolution image order has been submitted. ";
    private static final String NEW_EMAIL_BODY_2 = "PO Number: ";
    private static final String EMAIL_DO_NOT_REPLY = ". Please do not reply back to this automated email. Should you have any questions, please contact the Global Operations Digital Library team. ";
    
    
    public EmailUtils() {
        //empty constructor for Spring injection
    }
    
    public void sendExceptionEmail(String method, String exception) throws Exception {
		try
		{
			EmailDetails emailDetails = new EmailDetails();
			emailDetails.setFromAddress("CP.OPA.Admin@disney.com");
			emailDetails.setSmtpHost("kpaan005.idmzswna.idmz.disney.com");
			//emailDetails.setToAddresses("rex.s.hatch@disney.com; chris.willison@disney.com; andrew.simcox@disney.com");
			emailDetails.setToAddresses(service.getOPAPropertyByName("exceptionEmailList").getValue());
			
			String hostName = InetAddress.getLocalHost().getCanonicalHostName();
			String emailSubject = "Service - " + service.getOPAPropertyByName("environment").getValue() + " - " + method + " threw an exception";
			String emailContents = exception;
			Notification.sendMail(emailDetails.getSmtpHost(), emailDetails.getFromAddress(), emailDetails.getToAddresses(), new String(), new String(), emailSubject, UTF_EIGHT_CHARSET_DENOTATION, emailContents, DEFAULT_EMAIL_CONTENT_TYPE);
		}
		catch(Exception e)
		{
			log.error("Error occured attempting to create and send out the Exception Email - " + e);

		}
    }

	public void sendNotificationEMail() throws Exception {
		try
		{
			EmailDetails emailDetails = new EmailDetails();
			emailDetails.setFromAddress("CP.OPA.Admin@disney.com");
			emailDetails.setSmtpHost("kpaan005.idmzswna.idmz.disney.com");
			emailDetails.setToAddresses("tracy.x.adewunmi.-nd@disney.com");
			//emailDetails.setToAddresses(service.getOPAPropertyByName("exceptionEmailList").getValue());
			
			String hostName = InetAddress.getLocalHost().getCanonicalHostName();
			//String emailSubject = "Service - " + service.getOPAPropertyByName("environment").getValue() + " - OPA Aging Service: Test";
			String emailSubject = "Service  - OPA Aging Service: Test";
			String emailContents = "This is a test of the OPA Aging Service";
			Notification.sendMail(emailDetails.getSmtpHost(), emailDetails.getFromAddress(), emailDetails.getToAddresses(), new String(), new String(), emailSubject, UTF_EIGHT_CHARSET_DENOTATION, emailContents, DEFAULT_EMAIL_CONTENT_TYPE);
		}
		catch(Exception e)
		{
			log.error("Error occured attempting to create and send out the Exception Email - " + e);

		}
		
	}    
	
	
}
