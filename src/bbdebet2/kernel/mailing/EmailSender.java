/*
 * Copyright (C) 2019  Mathias Lohne
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package bbdebet2.kernel.mailing;

import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.SettingsHolder;
import bbdebet2.kernel.datastructs.User;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;

public class EmailSender {

    private Kernel kernel;
    private SettingsHolder settings;


    public EmailSender(Kernel kernel) {
        this.kernel = kernel;
        settings = kernel.getSettingsHolder();
    }


    public void sendOutOfMoneyNotification(User user) throws MessagingException, InvalidEncryptionException {
        sendMail(
            user,
            "Du er nå i minus",
            TextTemplateLoader.getTemplate(TextTemplate.USERTURNEDNEGATIVE)
        );
    }


    public void sendMail(User user,
                         String subject,
                         String text) throws MessagingException, InvalidEncryptionException {

        Properties props = new Properties();
        props.put("mail.smtp.host", settings.getEmailServer());
        props.put("mail.smtp.socketFactory.port", settings.getEmailPort());

        if (settings.getEmailEncryption().equals("SSL")) {
            props.put(
                "mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"
            );
        } else if (settings.getEmailEncryption().equals("STARTTLS")) {
            props.put("mail.smtp.starttls.enable", "true");
        } else {
            throw new InvalidEncryptionException(settings.getEmailEncryption());
        }

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", settings.getEmailPort());

        Session session = Session.getDefaultInstance(
            props,
            new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        settings.getEmailUser(), settings.getEmailPass());
                }
            }
        );


        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(
                settings.getEmailAddr(),
                "Choostholdsexpært i Biørneblæs"
            ));
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(user.getMail())
            );
            message.setReplyTo(new Address[]{new InternetAddress(settings.getEmailReplyTo())});
            message.setSubject(formatText(subject, user));
            message.setText(formatText(text, user));

            Transport.send(message);
            kernel.getLogger().log("Email sent to " + user);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    private String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


    private String formatText(String text, User user) {
        NumberFormat numberFormatter = new DecimalFormat("#0.00");
        text = text.replaceAll("%saldo", numberFormatter.format(user.getBalance()));
        text = text.replaceAll("%gjeld", numberFormatter.format(-user.getBalance()));
        text = text.replaceAll("%navn", capitalizeFirstLetter(user.getUserName()));
        text = text.replaceAll("%konto", settings.getAccountNumber());
        return text;
    }
}