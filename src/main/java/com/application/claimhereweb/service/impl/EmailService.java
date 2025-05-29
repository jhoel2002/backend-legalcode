package com.application.claimhereweb.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("stringTemplateEngine")
    private TemplateEngine templateEngine;

    public void sendEmailUsingTemplate(String templateName, Map<String, Object> variables, String to) {
        // 1. Obtener plantilla desde la base de datos
        String sql = "SELECT subject, body FROM email_templates WHERE name = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, templateName);

        String subject = (String) result.get("subject");
        String rawTemplate = (String) result.get("body");

        // 2. Procesar plantilla
        Context context = new Context();
        context.setVariables(variables);
        String processedHtml = templateEngine.process(rawTemplate, context);

        try {
            // 3. Crear y enviar correo
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(processedHtml, true); // true = HTML
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage(), e);
        }
    }
}