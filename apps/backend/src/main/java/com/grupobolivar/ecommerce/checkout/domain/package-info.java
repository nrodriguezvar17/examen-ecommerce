/**
 * Núcleo de dominio del checkout: modelo del carrito, motor de descuentos acumulativos
 * y cupones.
 *
 * <p><b>Regla de arquitectura:</b> este paquete y sus subpaquetes son Java puro. No
 * deben importar {@code org.springframework.*}, {@code jakarta.persistence.*} ni tipos
 * de la capa web. Las dependencias hacia el exterior (persistencia, catálogo de
 * cupones) se expresan como puertos (interfaces) que la infraestructura implementa.
 * Así las reglas matemáticas quedan aisladas de la persistencia y de los controladores
 * (requisito 4.1 del enunciado).
 */
package com.grupobolivar.ecommerce.checkout.domain;
