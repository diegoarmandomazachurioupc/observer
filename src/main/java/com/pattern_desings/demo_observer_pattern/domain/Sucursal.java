package com.pattern_desings.demo_observer_pattern.domain;

import java.util.ArrayList;
import java.util.List;

public class Sucursal implements IObserver{

    private String nombre;
    private List<String> notificaciones;

    public Sucursal(String nombre) {
        this.nombre = nombre;
        this.notificaciones = new ArrayList<>();
    }

    @Override
    public void actualizar(String mensaje) {
        notificaciones.add(mensaje);  // Al recibir la notificación, se agrega a la lista
    }

    public String getNombre() {
        return nombre;
    }

    public List<String> getNotificaciones() {
        return notificaciones;
    }

}
