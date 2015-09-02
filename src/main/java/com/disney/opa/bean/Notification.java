package com.disney.opa.bean;

import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.disney.opa.util.StringUtils;



/**
 * Notification system that uses JavaMail interface.
 * JavaMail API hides the implementation details of the mail protocols.
 * Programmers create and send messages using a simple object model.
 * 
 */

public class Notification {
	
	/**
	 * Sending an email message using JavaMail.
	 * The task consists of three steps:  
	 * 1. Create a Session that represents a connection to an email service provider.
	 * 2. Create and initialize a Message object that contains the data to be sent in the email 
	 * 3. Send the email message.
	 * 
	 * @param smtpHost.
	 * @param smtpFrom.
	 * @param smtpTo.
	 * @param msgSubject.
	 * @param msgContent.
	 */	
	public static void sendMail(String smtpHost, String smtpFrom, String smtpTo, String smtpCc, String smtpBcc, String msgSubject, String msgSubjectCharset, String msgContent, String msgContentType) throws AddressException, MessagingException {
				
		try {
			
			Date now = new Date();
			String to = null;
			String cc = null;
			String bcc = null;
			StringTokenizer st;
			
			// Get system properties
			Properties props = System.getProperties();
		
			// Setup mail server
			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.helo", "false");
			props.put("mail.smtp.ehlo", "false");
			props.put("mail.smtp.auth", "false");
		
			// Get session
			Session session = Session.getInstance(props, null);
			
			// Define message
			MimeMessage message = new MimeMessage(session);
		
			// Set the from address
			message.setFrom(new InternetAddress(smtpFrom));
		
			// Set TO address (smtpTo is a delimited string ).
			st = new StringTokenizer(smtpTo, ";");
			while (st.hasMoreTokens()) {
				to = st.nextToken();
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			}
			
			// Set CC address (smtpCc is a delimited string ).
			if(StringUtils.isNotEmpty(smtpCc))
			{
				st = new StringTokenizer(smtpCc, ";");
				while (st.hasMoreTokens()) {
					cc = st.nextToken();
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
				}
			}
			
			// Set BCC address (smtpBcc is a delimited string ).	
			if(StringUtils.isNotEmpty(smtpBcc))
			{
				st = new StringTokenizer(smtpBcc, ";");
				while (st.hasMoreTokens()) {
					bcc = st.nextToken();
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
				}
			}
		
			// Set the subject
			message.setSubject(msgSubject, msgSubjectCharset);
		
			// Set the content
			message.setContent(msgContent, msgContentType);

			// Set send date
			message.setSentDate(now);
		
			// Send message
			Transport.send(message);
	
		}catch (AddressException e) {
			throw new AddressException("Address exception in Notification: " + e);
		}catch (MessagingException e){
			throw new MessagingException("Messaging exception in Notification: " + e);
		}		
		
	}

	/**
	 * Sending an email message with attachments using JavaMail.
	 * The task consists of three steps:  
	 * 1. Create a Session that represents a connection to an email service provider.
	 * 2. Create and initialize a Message object that contains the data to be sent in the email
	 * 3. Attach a document to the email message
	 * 4. Send the email message.
	 * 
	 * @param smtpHost.
	 * @param smtpFrom.
	 * @param smtpTo.
	 * @param msgSubject.
	 * @param msgContent.
	 */	
	public static void sendMailWithAttachments(String smtpHost, String smtpFrom, String smtpTo, String smtpCc, String smtpBcc, String msgSubject, String msgSubjectCharset, String msgContent, String msgContentType, String attachmentFilePath, String attachmentFilename) throws AddressException, MessagingException
	{	
		try
		{
			Date now = new Date();
			String to = null;
			String cc = null;
			String bcc = null;
			StringTokenizer st;
			
			// Get system properties
			Properties props = System.getProperties();
		
			// Setup mail server
			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.helo", "false");
			props.put("mail.smtp.ehlo", "false");
			props.put("mail.smtp.auth", "false");
		
			// Get session
			Session session = Session.getInstance(props, null);
			
			// Define message
			MimeMessage message = new MimeMessage(session);
		
			// Set send date
			message.setSentDate(now);
			
			// Set the FROM address
			message.setFrom(new InternetAddress(smtpFrom));
			
			// Set TO address (smtpTo is a delimited string ).	
			st = new StringTokenizer(smtpTo, ";");
			while (st.hasMoreTokens()) {
				to = st.nextToken();
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			}
			
			// Set CC address (smtpCc is a delimited string ).
			st = new StringTokenizer(smtpCc, ";");
			while (st.hasMoreTokens()) {
				cc = st.nextToken();
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			}
			
			// Set BCC address (smtpBcc is a delimited string ).
			st = new StringTokenizer(smtpBcc, ";");
			while (st.hasMoreTokens()) {
				bcc = st.nextToken();
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
			}			
		
			// Set the subject
			message.setSubject(msgSubject, msgSubjectCharset);
			
			// Create the message part
		    MimeBodyPart messageBodyPart = new MimeBodyPart();

		    // Set message content
		    messageBodyPart.setContent(msgContent, msgContentType);

		    // Create multipart to hold message body and attachment parts
		    Multipart multipart = new MimeMultipart();
		    
		    // Add message part
		    multipart.addBodyPart(messageBodyPart);

		    // Attach document
		    MimeBodyPart attachmentPart = new MimeBodyPart();
		    DataSource source = new FileDataSource(attachmentFilePath);

		    attachmentPart.setDataHandler(new DataHandler(source));
		    attachmentPart.setFileName(attachmentFilename);
		    multipart.addBodyPart(attachmentPart);

		    // Assemble message
		    message.setContent(multipart);

			// Send message
			Transport.send(message);
		}
		catch (AddressException e)
		{
			throw new AddressException("Address exception in Notification: " + e);
		}
		catch (MessagingException e)
		{
			throw new MessagingException("Messaging exception in Notification: " + e);
		}
	}
	

}

