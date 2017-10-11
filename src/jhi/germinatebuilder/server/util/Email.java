/**
 * Copyright 2017 Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */

package jhi.germinatebuilder.server.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Email
{
	/**
	 * Sends an email
	 *
	 * @param emailProps The EmailProperties
	 * @param to         The recipient
	 * @param subject    The email subject
	 * @param message    The email message
	 * @param logFile    The attachment
	 * @throws MessagingException Thrown if sending the email fails
	 */
	public static void send(final EmailProperties emailProps, String to, String subject, String message, String tool, File logFile) throws MessagingException
	{
		if (emailProps == null)
			throw new MessagingException("Invalid email properties");

		String extra = "";

		if (StringUtils.areEqual(to, emailProps.mailAddress))
			extra = "<p>A user has built a new version of " + tool + ". Please find the details below.</p>";

		message = String.format(message, extra);

		Properties props = new Properties();

		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", emailProps.mailServer);
		props.put("mail.smtp.port", StringUtils.isEmpty(emailProps.mailPort) ? "587" : emailProps.mailPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Session session;

		if (StringUtils.isEmpty(emailProps.username, emailProps.password))
		{
			props.put("mail.smtp.auth", "false");
			session = Session.getInstance(props);
		}
		else
		{
			props.put("mail.smtp.auth", "true");
			session = Session.getInstance(props, new javax.mail.Authenticator()
			{
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(emailProps.username, emailProps.password);
				}
			});
		}

		Message mmessage = new MimeMessage(session);
		mmessage.setContent(message, "text/html; charset=utf-8");
		mmessage.setFrom(new InternetAddress(emailProps.mailAddress));
		mmessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		mmessage.setSubject(subject);

		/* Create a multipar message */
		Multipart multipart = new MimeMultipart();

		/* Create the message part */
		BodyPart messageBodyPart = new MimeBodyPart();

		/* Now set the actual message */
		messageBodyPart.setContent(message, "text/html; charset=utf-8");

		multipart.addBodyPart(messageBodyPart);

		messageBodyPart = new MimeBodyPart();

		try
		{
			File htmlFile = new File(logFile.getPath() + ".html");
			Files.copy(logFile.toPath(), htmlFile.toPath());
			logFile = htmlFile;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		/* Attach the attachment */
		DataSource source = new FileDataSource(logFile);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(logFile.getName());
		multipart.addBodyPart(messageBodyPart);

		mmessage.setContent(multipart);

		/* Send the email */
		Transport.send(mmessage);
	}

	public static class EmailProperties
	{
		public String mailServer;
		public String mailAddress;
		public String mailPort;
		public String username;
		public String password;

		public EmailProperties()
		{
		}

		public EmailProperties(String mailServer, String mailAddress, String mailPort, String username, String password)
		{
			this.mailServer = mailServer;
			this.mailAddress = mailAddress;
			this.mailPort = mailPort;
			this.username = username;
			this.password = password;
		}

		public static EmailProperties get()
		{
			EmailProperties result = new EmailProperties();

			result.mailServer = PropertyReader.getProperty(PropertyReader.EMAIL_SERVER);
			result.mailAddress = PropertyReader.getProperty(PropertyReader.EMAIL_ADDRESS);
			result.mailPort = PropertyReader.getProperty(PropertyReader.EMAIL_PORT);
			result.username = PropertyReader.getProperty(PropertyReader.EMAIL_USERNAME);
			result.password = PropertyReader.getProperty(PropertyReader.EMAIL_PASSWORD);

			if (!result.isValid())
				return null;
			else
				return result;
		}

		public boolean isValid()
		{
			/* Check if any of the properties doesn't exist */
			if (mailServer == null || mailAddress == null || username == null || password == null)
				return false;

			/* Note the missing password. Sometimes an empty (but existing) password is valid */
			return !StringUtils.isEmpty(mailServer, mailAddress, username);
		}
	}
}
