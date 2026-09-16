package com.tpc.tpcgestpaie.localapp.service.absence;

import com.tpc.tpcgestpaie.localapp.model.util.EmailConfig;
import com.tpc.tpcgestpaie.localapp.repository.util.EmailConfigRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final EmailConfigRepository repository;
    private final TemplateEngine templateEngine;

    private EmailConfig getConfig() {
        return repository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("⚠️ Aucune configuration email trouvée en base"));
    }

    private JavaMailSenderImpl getMailSender(EmailConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(Integer.parseInt(config.getPort()));
        mailSender.setUsername(config.getMailFrom());
        mailSender.setPassword(config.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false"); // Mettre à true pour debug
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        return mailSender;
    }

    /**
     * ✅ MÉTHODE PRINCIPALE - Envoyer un email simple
     */
    public void envoyerNotificationDemande(String to, String subject, String message) {
        try {
            EmailConfig config = getConfig();
            JavaMailSenderImpl sender = getMailSender(config);

            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(config.getMailFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, false); // false = texte brut, true = HTML

            sender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email à " + to + ": " + e.getMessage(), e);
        }
    }

    /**
     * Variante avec template Thymeleaf (optionnel)
     */
    public void envoyerNotificationAvecTemplate(
            String to,
            String subject,
            Map<String, Object> variables,
            String templateName) {

        try {
            EmailConfig config = getConfig();
            JavaMailSenderImpl sender = getMailSender(config);

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(config.getMailFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            sender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email à " + to + ": " + e.getMessage(), e);
        }
    }
}