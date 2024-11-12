package com.pattern_desings.demo_observer_pattern.domain;

public interface IObservable {
    void agregarObserver(IObserver observer);  // Método para agregar un observador
    void eliminarObserver(IObserver observer);  // Método para eliminar un observador
    void notificarObservers(String mensaje);  // Método para notificar a los observadores
}
