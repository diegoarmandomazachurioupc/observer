package com.pattern_desings.demo_observer_pattern.domain;

import java.util.ArrayList;
import java.util.List;

public class Inventario implements IObservable{

    private List<Producto> productos;
    private List<IObserver> observadores;

    public Inventario() {
        productos = new ArrayList<>();
        observadores = new ArrayList<>();
    }

    // Agregar un observador
    @Override
    public void agregarObserver(IObserver observer) {
        observadores.add(observer);
    }

    // Eliminar un observador
    @Override
    public void eliminarObserver(IObserver observer) {
        observadores.remove(observer);  // Eliminar el observador de la lista
    }

    // Notificar a todos los observadores
    @Override
    public void notificarObservers(String mensaje) {
        for (IObserver observer : observadores) {
            observer.actualizar(mensaje);  // Notificar a cada observador
        }
    }

    // Verificar inventario y notificar a los observadores si algún producto tiene stock bajo
    public void verificarInventario() {
        // Limpiar las notificaciones de todas las sucursales antes de enviar nuevas
        for (IObserver observer : observadores) {
            if (observer instanceof Sucursal) {
                Sucursal sucursal = (Sucursal) observer;
                sucursal.getNotificaciones().clear();  // Limpiar las notificaciones anteriores
            }
        }

        // Verificar el stock de los productos y notificar a los observadores
        for (Producto producto : productos) {
            if (producto.getStock() < 5) {  // Condición para stock bajo
                for (IObserver observer : observadores) {
                    if (observer instanceof Sucursal) {
                        Sucursal sucursal = (Sucursal) observer;
                        sucursal.actualizar("El producto " + producto.getNombre() + " tiene stock bajo.");
                    }
                }
            }
        }
    }

    // Agregar un producto al inventario
    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    // Obtener un producto por nombre
    public Producto obtenerProducto(String nombre) {
        for (Producto producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
            }
        }
        return null;
    }

    // Eliminar un producto del inventario
    public void eliminarProducto(String nombre) {
        productos.removeIf(producto -> producto.getNombre().equalsIgnoreCase(nombre));
    }

    public List<IObserver> getObservadores() {
        return observadores;
    }

    public List<Producto> getProductos() {
        return productos;
    }

}
