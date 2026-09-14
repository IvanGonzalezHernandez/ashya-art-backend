package com.ashyaart.ashya_art_backend.model;

import java.util.List;

public class NewsletterCampanaDto {

    private String asunto;
    private String mensaje;

    /** Si viene informado, el envio se hace solo a esta direccion (modo prueba). */
    private String testEmail;

    /** Si viene informado (y no es modo prueba), se envia solo a estos emails (filtrados
     *  contra los suscriptores activos). Si viene vacio o nulo, se envia a todos los activos. */
    private List<String> destinatarios;

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

    public List<String> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(List<String> destinatarios) {
        this.destinatarios = destinatarios;
    }
}
