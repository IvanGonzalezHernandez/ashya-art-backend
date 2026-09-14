package com.ashyaart.ashya_art_backend.model;

public class NewsletterCampanaDto {

    private String asunto;
    private String mensaje;

    /** Si viene informado, el envio se hace solo a esta direccion (modo prueba). */
    private String testEmail;

    public NewsletterCampanaDto() {
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTestEmail() {
        return testEmail;
    }

    public void setTestEmail(String testEmail) {
        this.testEmail = testEmail;
    }
}
