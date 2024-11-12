package com.pattern_desings.demo_observer_pattern.controller;


import com.pattern_desings.demo_observer_pattern.domain.IObserver;
import com.pattern_desings.demo_observer_pattern.domain.Inventario;
import com.pattern_desings.demo_observer_pattern.domain.Producto;
import com.pattern_desings.demo_observer_pattern.domain.Sucursal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventario")
public class NotificationController {

    private final Inventario inventario;

    public NotificationController() {
        this.inventario = new Inventario();
        inicializarDatos();
    }

    private void inicializarDatos() {
        Producto leche = new Producto("Leche", 10, 2.5);
        Producto pan = new Producto("Pan", 10, 1.2);
        Producto arroz = new Producto("Arroz", 10, 3.0);

        inventario.agregarProducto(leche);
        inventario.agregarProducto(pan);
        inventario.agregarProducto(arroz);

        Sucursal sucursal1 = new Sucursal("Sucursal 1");

        inventario.agregarObserver(sucursal1);

        verificarYNotificarInventario();
    }

    private void verificarYNotificarInventario() {
        inventario.verificarInventario();
    }

    @PostMapping("/productos")
    public ResponseEntity<String> agregarOActualizarProducto(@RequestBody Producto producto) {
        inventario.agregarProducto(producto);
        verificarYNotificarInventario();
        return new ResponseEntity<>("Producto agregado o actualizado correctamente: " + producto.getNombre(), HttpStatus.OK);
    }

    @PutMapping("/productos/{nombre}")
    public ResponseEntity<String> modificarStock(@PathVariable String nombre, @RequestParam int stock) {
        Producto producto = inventario.obtenerProducto(nombre);
        if (producto != null) {
            producto.setStock(stock);
            verificarYNotificarInventario();
            return new ResponseEntity<>("Stock actualizado para el producto: " + nombre, HttpStatus.OK);
        }
        return new ResponseEntity<>("Producto no encontrado.", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/productos/{nombre}")
    public ResponseEntity<String> eliminarProducto(@PathVariable String nombre) {
        Producto producto = inventario.obtenerProducto(nombre);
        if (producto != null) {
            inventario.eliminarProducto(nombre);
            verificarYNotificarInventario();
            return new ResponseEntity<>("Producto eliminado: " + nombre, HttpStatus.OK);
        }
        return new ResponseEntity<>("Producto no encontrado.", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/sucursales/{nombre}")
    public ResponseEntity<String> eliminarSucursal(@PathVariable String nombre) {
        // Buscar la sucursal por su nombre entre los observadores
        IObserver sucursalAEliminar = inventario.getObservadores().stream()
                .filter(observer -> observer instanceof Sucursal)
                .filter(observer -> ((Sucursal) observer).getNombre().equals(nombre))
                .findFirst()
                .orElse(null);

        if (sucursalAEliminar != null) {
            // Eliminar la sucursal de los observadores
            inventario.eliminarObserver(sucursalAEliminar);
            return new ResponseEntity<>("Sucursal eliminada de las notificaciones: " + nombre, HttpStatus.OK);
        }

        return new ResponseEntity<>("Sucursal no encontrada.", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/sucursales")
    public ResponseEntity<String> agregarSucursal(@RequestBody String nombreSucursal) {
        Sucursal nuevaSucursal = new Sucursal(nombreSucursal);
        inventario.agregarObserver(nuevaSucursal);
        return new ResponseEntity<>("Sucursal agregada correctamente: " + nombreSucursal, HttpStatus.CREATED);
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<String> verNotificaciones() {
        // Obtener las notificaciones de todas las sucursales
        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("<html><head><style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f9; color: #333; }")
                .append("h2 { color: #0066cc; text-align: center; }")
                .append("div.notification { border: 1px solid #ddd; padding: 15px; margin: 10px 0; background-color: #fff; border-radius: 5px; }")
                .append("div.notification h3 { margin: 0; color: #ff5722; }")
                .append("div.notification p { font-size: 14px; margin: 5px 0; color: #555; }")
                .append("</style></head><body>")
                .append("<h2>Notificaciones de Productos con Stock Bajo</h2>");

        // Verifica si hay notificaciones
        boolean hayNotificaciones = false;

        for (IObserver observer : inventario.getObservadores()) {
            if (observer instanceof Sucursal) {
                Sucursal sucursal = (Sucursal) observer;

                // Verificar si hay notificaciones para la sucursal
                List<String> notificaciones = sucursal.getNotificaciones();
                if (!notificaciones.isEmpty()) {
                    hayNotificaciones = true;
                    htmlResponse.append("<div class='notification'>")
                            .append("<h3>").append(sucursal.getNombre()).append("</h3>");

                    for (String notificacion : notificaciones) {
                        htmlResponse.append("<p>").append(notificacion).append("</p>");
                    }

                    htmlResponse.append("</div>");
                }
            }
        }

        // Si no hay notificaciones
        if (!hayNotificaciones) {
            htmlResponse.append("<p>No hay notificaciones disponibles.</p>");
        }

        htmlResponse.append("</body></html>");

        // Devolver las notificaciones como una respuesta HTML
        return new ResponseEntity<>(htmlResponse.toString(), HttpStatus.OK);
    }

    //
    @GetMapping("/stock")
    public ResponseEntity<String> verProductos() {
        // Crear la estructura HTML inicial
        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("<html><head><style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f9; color: #333; }")
                .append("h2 { color: #0066cc; text-align: center; }")
                .append("div.product { border: 1px solid #ddd; padding: 15px; margin: 10px 0; background-color: #fff; border-radius: 5px; }")
                .append("div.product h3 { margin: 0; color: #4caf50; }")
                .append("div.product p { font-size: 14px; margin: 5px 0; color: #555; }")
                .append("</style></head><body>")
                .append("<h2>Lista de Productos en Inventario</h2>");

        // Verificar si hay productos en el inventario
        boolean hayProductos = !inventario.getProductos().isEmpty();

        // Generar HTML para cada producto
        if (hayProductos) {
            for (Producto producto : inventario.getProductos()) {
                htmlResponse.append("<div class='product'>")
                        .append("<h3>").append(producto.getNombre()).append("</h3>")
                        .append("<p><strong>Stock:</strong> ").append(producto.getStock()).append("</p>")
                        .append("<p><strong>Precio:</strong> $").append(producto.getPrecio()).append("</p>")
                        .append("</div>");
            }
        } else {
            htmlResponse.append("<p>No hay productos disponibles en el inventario.</p>");
        }

        htmlResponse.append("</body></html>");

        // Devolver la lista de productos como una respuesta HTML
        return new ResponseEntity<>(htmlResponse.toString(), HttpStatus.OK);
    }

}
