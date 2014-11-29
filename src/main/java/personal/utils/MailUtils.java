package personal.utils;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

public class MailUtils {

    public static void main(String[] args) {
	new MailUtils().SendMail("soso@mia.gov.ge", "test", "test");
    }

    @Value("${mail.smtp.auth}")
    private String smtpAuth;

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private String smtpPort;

    @Value("${mail.smtp.from}")
    private String smtpFrom;

    @Value("${mail.smtp.user}")
    private String smtpUser;

    @Value("${mail.smtp.pass}")
    private String smptPass;

    @Value("${mail.smtp.starttls.required}")
    private String smptTls;

    @Value("${mail.smtp.ssl.enable}")
    private String smptSsl;

    public void SendMail(String to, String subject, String messageText) {

	// Get system properties
	Properties properties = System.getProperties();

	// Setup mail server
	properties.setProperty("mail.smtp.host", smtpHost);
	
	if (isNotBlank(smtpPort)) {
	properties.setProperty("mail.smtp.port", smtpPort);
	}
	
	if (isNotBlank(smptTls)) {
	    properties.setProperty("mail.smtp.starttls.required", smptTls);
	}

	if (isNotBlank(smptSsl)) {
	    properties.setProperty("mail.smtp.ssl.enable", smptSsl);
	}

	if (isNotBlank(smtpAuth)) {
	    // Get the default Session object.
	    properties.setProperty("mail.smtp.auth", smtpAuth);
	}

	Session session = Session.getDefaultInstance(properties);
	try {
	    // Create a default MimeMessage object.
	    MimeMessage message = new MimeMessage(session);

	    // Set From: header field of the header.
	    if (StringUtils.isNotBlank(smtpFrom)) {
		message.setFrom(new InternetAddress(smtpFrom));
	    }

	    // Set To: header field of the header.
	    message.addRecipient(Message.RecipientType.TO, new InternetAddress(
		    to));

	    // Set Subject: header field
	    message.setSubject("Subject");

	    // Now set the actual message
	    message.setText("This is actual message");
	    message.saveChanges();

	    if (isNotBlank(smtpAuth) && smtpAuth.equals("true")) {
		Transport tr = session.getTransport("smtp");
		tr.connect(smtpHost, smtpUser, smptPass);
		// Send message
		tr.sendMessage(message, message.getAllRecipients());
		tr.close();
	    } else {
		Transport.send(message);
	    }
	} catch (MessagingException mex) {
	    mex.printStackTrace();
	}
    }

}
