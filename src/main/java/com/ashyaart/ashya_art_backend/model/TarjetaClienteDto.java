package com.ashyaart.ashya_art_backend.model;

public class TarjetaClienteDto {
	
	private String tarjeta;
	private boolean canjeada;
	private String fechaCompra;
	private String fechaCaducidad;
	
	public String getTarjeta() {
		return tarjeta;
	}
	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}
	public boolean isCanjeada() {
		return canjeada;
	}
	public void setCanjeada(boolean canjeada) {
		this.canjeada = canjeada;
	}
	public String getFechaCompra() {
		return fechaCompra;
	}
	public void setFechaCompra(String fechaCompra) {
		this.fechaCompra = fechaCompra;
	}
	public String getFechaCaducidad() {
		return fechaCaducidad;
	}
	public void setFechaCaducidad(String fechaCaducidad) {
		this.fechaCaducidad = fechaCaducidad;
	}
	
	

}
