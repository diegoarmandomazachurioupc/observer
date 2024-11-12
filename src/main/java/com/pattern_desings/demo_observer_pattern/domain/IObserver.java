package com.pattern_desings.demo_observer_pattern.domain;

public interface IObserver {
    void actualizar(String mensaje);  // El método que se llamará para notificar cambios
}
