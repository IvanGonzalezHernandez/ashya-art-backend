package com.ashyaart.ashya_art_backend.model;

public class CarritoClienteDto {
    private CarritoDto carrito;
    private ClienteDto cliente;
    private String codigoTarjeta;
    private Double totalConDescuento;

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
    
	public String getCodigoTarjeta() {
		return codigoTarjeta;
	}
	
	public void setCodigoTarjeta(String codigoTarjeta) {
		this.codigoTarjeta = codigoTarjeta;
	}
	
	public Double getTotalConDescuento() {
		return totalConDescuento;
	}
	
	public void setTotalConDescuento(Double totalConDescuento) {
		this.totalConDescuento = totalConDescuento;
	}
}
