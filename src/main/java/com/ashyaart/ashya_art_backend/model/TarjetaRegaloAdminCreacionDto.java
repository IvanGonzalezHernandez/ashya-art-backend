package com.ashyaart.ashya_art_backend.model;

public class TarjetaRegaloAdminCreacionDto {

    private Long idCliente;

    private ClienteDto clienteNuevo;

    private Long idTarjetaRegalo;

    private String destinatario;

    public TarjetaRegaloAdminCreacionDto() {}

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public ClienteDto getClienteNuevo() {
        return clienteNuevo;
    }

    public void setClienteNuevo(ClienteDto clienteNuevo) {
        this.clienteNuevo = clienteNuevo;
    }

    public Long getIdTarjetaRegalo() {
        return idTarjetaRegalo;
    }

    public void setIdTarjetaRegalo(Long idTarjetaRegalo) {
        this.idTarjetaRegalo = idTarjetaRegalo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
}
