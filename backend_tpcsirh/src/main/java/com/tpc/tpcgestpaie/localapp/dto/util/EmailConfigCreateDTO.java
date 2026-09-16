package com.tpc.tpcgestpaie.localapp.dto.util;

import com.tpc.tpcgestpaie.localapp.model.util.EmailConfig;

public class EmailConfigCreateDTO {

    private String host;
    private String port;
    private String protocole;
    private String mailFrom;
    private String password;


    // Getters & Setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocole() {
        return protocole;
    }

    public void setProtocole(String protocole) {
        this.protocole = protocole;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmailConfig fromCreateDTO(EmailConfigCreateDTO dto) {
        EmailConfig config = new EmailConfig();
        config.setHost(dto.getHost());
        config.setPort(dto.getPort());
        config.setProtocole(dto.getProtocole());
        config.setMailFrom(dto.getMailFrom());
        config.setPassword(dto.getPassword());
        return config;
    }

}
