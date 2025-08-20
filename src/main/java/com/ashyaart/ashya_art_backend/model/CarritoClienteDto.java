package com.ashyaart.ashya_art_backend.model;

public class CarritoClienteDto {
    private CarritoDto carrito;
    private ClienteDto cliente;

    // Getters y setters
    public CarritoDto getCarrito() {
        return carrito;
    }

    public void setCarrito(CarritoDto carrito) {
        this.carrito = carrito;
    }

    public ClienteDto getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDto cliente) {
        this.cliente = cliente;
    }
}
