package de.terrestris.shogun2.util.mail;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 *
 * @author Daniel Koch
 *
 */
@Component
public class MailPublisher {

	/**
	 * The Logger.
	 */
	private static final Logger LOG = Logger.getLogger(MailPublisher.class);

	/**
	 * The autowired JavaMailSender class.
	 */
	@Autowired
	@Qualifier("mailSender")
	private JavaMailSender mailSender;

	/**
	 * The default mail sender (e.g. noreply@shogun2.de).
	 */
	@Autowired
	@Qualifier("defaultMailSender")
	private String defaultMailSender;

	/**
	 * Sends a SimpleMailMessage.
	 *
	 * @param from The mail sender address.
	 * @param replyTo The reply to address.
	 * @param to A list of mail recipient addresses.
	 * @param cc A list of carbon copy mail recipient addresses.
	 * @param bcc A list of blind carbon copy mail recipient addresses.
	 * @param subject The mail subject.
	 * @param msg The mail message text.
	 * @throws Exception 
	 */
	public void sendMail(String from, String replyTo, String[] to, String[] cc,
			String[] bcc, String subject, String msg) throws Exception {

		SimpleMailMessage simpleMailMassage = new SimpleMailMessage();

		// fallback to default mail sender
		if (from == null || from.isEmpty()) {
			from = defaultMailSender;
		}

		simpleMailMassage.setFrom(from);
		simpleMailMassage.setReplyTo(replyTo);
		simpleMailMassage.setTo(to);
		simpleMailMassage.setBcc(bcc);
		simpleMailMassage.setCc(cc);
		simpleMailMassage.setSubject(subject);
		simpleMailMassage.setText(msg);

		sendMail(simpleMailMassage);
	}

	/**
	 *
	 * Sends a MimeMessage.
	 *
	 * @param from The mail sender address.
	 * @param replyTo The reply to address.
	 * @param to A list of mail recipient addresses.
	 * @param cc A list of carbon copy mail recipient addresses.
	 * @param bcc A list of blind carbon copy mail recipient addresses.
	 * @param subject The mail subject.
	 * @param msg The mail message text.
	 * @param html Whether to apply content type "text/html" or the default
	 *             content type ("text/plain").
	 * @param attachmentFilename The attachment file name.
	 * @param attachmentFile The file resource to be applied to the mail.
	 * @throws Exception 
	 */
	public void sendMimeMail(String from, String replyTo, String[] to, String[] cc,
			String[] bcc, String subject, String msg, Boolean html,
			String attachmentFilename, File attachmentFile) throws Exception {

		Boolean multipart = false;

		// if a attachment file is required, we have to use a multipart massage
		if (attachmentFilename!= null && attachmentFile != null) {
			multipart = true;
		}

		MimeMessage mimeMailMessage = mailSender.createMimeMessage();
		MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMailMessage,
				multipart);

		// fallback to default mail sender
		if (from == null || from.isEmpty()) {
			from = defaultMailSender;
		}

		// set minimal configuration
		mimeHelper.setFrom(from);
		mimeHelper.setTo(to);
		mimeHelper.setSubject(subject);
		mimeHelper.setText(msg, html);

		// add replyTo address if set
		if (replyTo != null && !replyTo.isEmpty()) {
			mimeHelper.setReplyTo(replyTo);
		}

		// add bcc address(es) if set
		if (bcc != null && bcc.length > 0) {
			mimeHelper.setBcc(bcc);
		}

		// add cc address(es) if set
		if (cc != null && cc.length > 0) {
			mimeHelper.setCc(cc);
		}

		// add attachment file if set
		if (attachmentFilename!= null && attachmentFile != null) {
			mimeHelper.addAttachment(attachmentFilename, attachmentFile);
		}

		sendMail(mimeMailMessage);
	}

	/**
	 *
	 * @param mailMessage
	 * @throws Exception 
	 */
	public void sendMail(SimpleMailMessage mailMessage) throws Exception {
		LOG.debug("Requested to send a mail");
		try {
			mailSender.send(mailMessage);
			LOG.debug("Successfully send mail.");
		} catch(MailException e) {
			throw new Exception("Could not send the mail: " + e.getMessage());
		}
	}

	/**
	 *
	 * @param mimeMessage
	 * @throws Exception 
	 */
	public void sendMail(MimeMessage mimeMessage) throws Exception {
		LOG.debug("Requested to send a mail");
		try {
			mailSender.send(mimeMessage);
			LOG.debug("Successfully send mail.");
		} catch(MailException e) {
			throw new Exception("Could not send the mail: " + e.getMessage());
		}
	}

	/**
	 * @return the mailSender
	 */
	public JavaMailSender getMailSender() {
		return mailSender;
	}

	/**
	 * @param mailSender the mailSender to set
	 */
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * @return the defaultMailSender
	 */
	public String getDefaultMailSender() {
		return defaultMailSender;
	}

	/**
	 * @param defaultMailSender the defaultMailSender to set
	 */
	public void setDefaultMailSender(String defaultMailSender) {
		this.defaultMailSender = defaultMailSender;
	}

}
