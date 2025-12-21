package com.ashyaart.ashya_art_backend.model;

public class OpenStudioSolicitudDto {
	  private String option;
	  private String nombre;
	  private String email;
	  private String telefono;
	  private String preguntasAdicionales;

	  public String getOption() { return option; }
	  public void setOption(String option) { this.option = option; }

	  public String getNombre() { return nombre; }
	  public void setNombre(String nombre) { this.nombre = nombre; }

	  public String getEmail() { return email; }
	  public void setEmail(String email) { this.email = email; }

	  public String getTelefono() { return telefono; }
	  public void setTelefono(String telefono) { this.telefono = telefono; }

	  public String getPreguntasAdicionales() { return preguntasAdicionales; }
	  public void setPreguntasAdicionales(String preguntasAdicionales) { this.preguntasAdicionales = preguntasAdicionales; }
	}