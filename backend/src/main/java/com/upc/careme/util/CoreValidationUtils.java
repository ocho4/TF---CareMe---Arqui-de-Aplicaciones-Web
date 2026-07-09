package com.upc.careme.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public final class CoreValidationUtils {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_PERU_REGEX = "^9\\d{8}$";
    private static final String DNI_REGEX = "^\\d{8}$";

    private CoreValidationUtils() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable");
    }

    public static boolean esCorreoValido(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }
        return Pattern.compile(EMAIL_REGEX).matcher(correo).matches();
    }

    public static boolean esTelefonoPeruValido(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return false;
        }
        return Pattern.compile(PHONE_PERU_REGEX).matcher(telefono).matches();
    }

    public static boolean esDniValido(String dni) {
        if (dni == null || dni.isBlank()) {
            return false;
        }
        return Pattern.compile(DNI_REGEX).matcher(dni).matches();
    }

    public static int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public static String formatearFechaEspanol(LocalDate fecha) {
        if (fecha == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
        return fecha.format(formatter);
    }

    public static boolean esFechaValida(String fechaStr, String formato) {
        if (fechaStr == null || formato == null) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
            LocalDate.parse(fechaStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static String enmascararCorreo(String correo) {
        if (!esCorreoValido(correo)) {
            return correo;
        }
        int arrobaIndex = correo.indexOf("@");
        String usuario = correo.substring(0, arrobaIndex);
        String dominio = correo.substring(arrobaIndex);

        if (usuario.length() <= 3) {
            return "***" + dominio;
        }
        return usuario.substring(0, 3) + "****" + dominio;
    }
}