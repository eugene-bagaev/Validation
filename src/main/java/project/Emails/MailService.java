package project.Emails;
import java.io.FileNotFoundException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.PasswordAuthentication;
import javax.mail.Authenticator;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import project.Rules.Constants;

/*
Service Usage

MailService mail = MailService.getInstance()
                .setMessageType(MailConstants.MSG_TYPE_REGISTRATION)
                .setMailTo("eugene.bagaev@gmail.com")
                .setMailFrom(MailConstants.SERVICE_EMAIL_ADRESS)
                .setSubject("Mail Subject");

You can ignore params above. Mail will send to service box.

Thread thread = new Thread(mail);
thread.start();

 */

public class MailService implements Runnable {

    private static MailService mail_instance = null;

    // parameters for mail
    private Properties props;
    private Session session;

    // service mail creds
    private String username     = "denis.brfufu@gmail.com";
    private String password     = "VRP_Task";

    // default mail params for send errors or logs
    private String defaultFrom      = Constants.FROM_EMAIL_ADDRESS;
    private String defaultTo        = Constants.TO_EMAIL_ADDRESS;
    private String defaultSubject   = "Chat Error Log";

    // parameters for populating for custom sending
    private String mailTo   = "";
    private String mailFrom = "";
    private String subject  = "";
    private String body = "";

    // message type for run()
    private String messageType = null;

    public MailService() {
        initProperties();
        initSession();
    }

    public void sendMail() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public static MailService getInstance() {
        if (mail_instance == null) {
            return new MailService();
        }
        return mail_instance;
    }

    public MailService setMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public MailService setMailTo(String mailTo) {
        this.mailTo = mailTo;
        return this;
    }

    public MailService setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
        return this;
    }

    public MailService setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MailService setBody(String body) {
        this.body = body;
        return this;
    }

    public void run() {
        sendChatRegistration(
                defaultFrom,
                defaultTo,
                body
        );

    }

    public void sendChatRegistration(String fromEmail, String toEmail, String body) {
        System.out.println("Send Chat Registration Email");

        long start = System.nanoTime();

        try {

            // Get String template

            // FileServlet.getTemplateFromDiskAsStringByName("reg-material");
            // replace the following expressions
            // {login} -    by user login
            // {password} - by user password
            // {name} -     by user name
            // then send it
            System.out.println(body);

            Message message = getMimeMessage(
                    fromEmail,
                    toEmail,
                    subject,
                    body,
                    true
            );

            Transport.send(message);

        } catch (MessagingException e) {

            System.out.println("Email error: " + e.getMessage());
            throw new RuntimeException(e);

        }

        long end = System.nanoTime();
        System.out.println("Email time: " + ((end - start)) / 1000000);
    }

    private void sendErrorLogs(String text) {
        System.out.println("Send Error Logs");

        try {

            Message message = getMimeMessage(
                    defaultFrom,
                    defaultTo,
                    defaultSubject,
                    text,
                    true
            );

            Transport.send(message);

        } catch (MessagingException ex) {

            System.out.println("Email logs error: " + ex.getMessage());
            System.out.println("Email logs st: " + ex.getStackTrace());
            throw new RuntimeException();

        }
    }

    private Message getMimeMessage(String from, String to, String subject, String body, Boolean isText) {

        Message message = new MimeMessage(session);

        try {

            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            if (isText) {
                message.setText(body);
            } else {
                message.setContent(body, "text/html");
            }

        } catch (MessagingException ex) {

            System.out.println("Email logs error: " + ex.getMessage());
            System.out.println("Email logs st: " + ex.getStackTrace());
            throw new RuntimeException();

        }

        return message;
    }

    private void initSession() {
        session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        username,
                        password
                );
            }
        });
    }

    // gmail mail service props
    private void initProperties() {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

}
