package com.upc.careme.config;

public class CuidadorInactivoException extends RuntimeException {

    private final String tipo;
    private final String observaciones;
    private final Integer idUsuario;

    public CuidadorInactivoException(String tipo, String mensaje, String observaciones, Integer idUsuario) {
        super(mensaje);
        this.tipo = tipo;
        this.observaciones = observaciones;
        this.idUsuario = idUsuario;
    }

    public String getTipo() { return tipo; }
    public String getObservaciones() { return observaciones; }
    public Integer getIdUsuario() { return idUsuario; }
}
