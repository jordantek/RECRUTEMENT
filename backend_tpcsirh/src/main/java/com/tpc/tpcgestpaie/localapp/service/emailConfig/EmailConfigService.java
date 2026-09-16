package com.tpc.tpcgestpaie.localapp.service.emailConfig;

import com.tpc.tpcgestpaie.localapp.model.util.EmailConfig;
import com.tpc.tpcgestpaie.localapp.repository.util.EmailConfigRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailConfigService {

    private final EmailConfigRepository repository;
    private final TemplateEngine templateEngine;

    public EmailConfigService(EmailConfigRepository repository, TemplateEngine templateEngine) {
        this.repository = repository;
        this.templateEngine = templateEngine;
    }

    // Récupérer la config unique
    public EmailConfig getConfig() {
        return repository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("⚠️ Aucune configuration email trouvée en base"));
    }

    // Créer un JavaMailSender à partir de la config

    @Value("${app.email.host}")
    private String emailHost;

    @Value("${app.email.port}")
    private String emailPort;

    @Value("${app.email.mail-from}")
    private String emailFrom;

    @Value("${app.email.password}")
    private String emailPassword;

    @Value("${app.email.protocol:smtp}")
    private String emailProtocol;

    @Value("${app.email.auth:true}")
    private String emailAuth;

    @Value("${app.email.starttls.enable:true}")
    private String emailStarttls;

    @Value("${app.email.debug:true}")
    private String emailDebug;

    @Value("${app.email.ssl.protocols:TLSv1.2}")
    private String emailSslProtocols;

    private JavaMailSenderImpl getMailSender(EmailConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailHost);
        mailSender.setPort(Integer.parseInt(emailPort));
        mailSender.setUsername(emailFrom);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", emailProtocol);
        props.put("mail.smtp.auth", emailAuth);
        props.put("mail.smtp.starttls.enable", emailStarttls);
        props.put("mail.debug", emailDebug);
        props.put("mail.smtp.ssl.protocols", emailSslProtocols);

        return mailSender;
    }

    // Ajouter cette méthode à EmailConfigService

    // Dans EmailConfigService
    public void sendEmailWithTemplate(String to, String subject, Map<String, Object> variables,
                                      String attachmentPath, String templateName)
            throws MessagingException {

        EmailConfig config = getConfig();
        JavaMailSenderImpl sender = getMailSender(config);

        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process(templateName, context);

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("no-reply@gestpaie.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Logo inline
        if (variables.containsKey("logoPath")) {
            String logoPath = (String) variables.get("logoPath");
            if (logoPath != null && !logoPath.isEmpty()) {
                File logoFile = new File(logoPath);
                if (logoFile.exists()) {
                    helper.addInline("companyLogo", new FileSystemResource(logoFile));
                }
            }
        }

        // Pièce jointe
        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            File file = new File(attachmentPath);
            if (file.exists()) {
                FileSystemResource resource = new FileSystemResource(file);
                helper.addAttachment(file.getName(), resource);
            }
        }

        sender.send(message);
    }


    // Envoi d’email avec template et pièce jointe
    public void sendEmail(String to, String subject, Map<String, Object> variables, String attachmentPath)
            throws MessagingException {

        EmailConfig config = getConfig();
        JavaMailSenderImpl sender = getMailSender(config);

        // Préparer le contenu du template
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process("email/welcome-email", context);

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("no-reply@gestpaie.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        // Ajouter le logo inline si on stocke un chemin local
        if (variables.containsKey("logoPath")) {
            String logoPath = (variables.get("logoPath") == null)
                    ? ""   // valeur par défaut
                    : variables.get("logoPath").toString();
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                helper.addInline("companyLogo", new FileSystemResource(logoFile));
            }
        }
        // Ajouter pièce jointe si fournie
        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            File file = new File(attachmentPath);
            if (file.exists()) {
                FileSystemResource resource = new FileSystemResource(file);
                helper.addAttachment(file.getName(), resource);
            }
        }
        sender.send(message);
    }

    public void sendBulletin(String to, String subject, Map<String, Object> variables, String attachmentPath)
            throws MessagingException {

        EmailConfig config = getConfig();
        JavaMailSenderImpl sender = getMailSender(config);

        // Préparer le contenu du template
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process("email/bulletin-email", context);

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("no-reply@gestpaie.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        // Ajouter le logo inline si on stocke un chemin local
        if (variables.containsKey("logoPath")) {
            String logoPath = variables.get("logoPath").toString();
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                helper.addInline("companyLogo", new FileSystemResource(logoFile));
            }
        }
        // Ajouter pièce jointe si fournie
        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            File file = new File(attachmentPath);
            if (file.exists()) {
                FileSystemResource resource = new FileSystemResource(file);
                helper.addAttachment(file.getName(), resource);
            }
        }
        sender.send(message);
    }

}
