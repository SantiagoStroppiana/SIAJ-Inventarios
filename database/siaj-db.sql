-- Script SQL SIAJ - Versión Simplificada con Pagos Únicos
-- Fecha: 2025-06-30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

-- --------------------------------------------------------
-- Tabla: categorias
CREATE TABLE `categorias` (
  `id` int(11) NOT NULL,
  `nombre` varchar(45) NOT NULL,
  `descripcion` varchar(120) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: categorias_productos
CREATE TABLE `categorias_productos` (
  `id` int(11) NOT NULL,
  `productos_id` int(11) NOT NULL,
  `categoria_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: detalle_entrada
CREATE TABLE `detalle_entrada` (
  `id` int(11) NOT NULL,
  `entrada_id` int(11) NOT NULL,
  `productos_id` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: detalle_orden_compra
CREATE TABLE `detalle_orden_compra` (
  `id` int(11) NOT NULL,
  `productos_id` int(11) NOT NULL,
  `orden_compra_id` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: detalle_venta
CREATE TABLE `detalle_venta` (
  `id` int(11) NOT NULL,
  `ventas_id` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `productos_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: entrada
CREATE TABLE `entrada` (
  `id` int(11) NOT NULL,
  `orden_compra_id` int(11) NOT NULL,
  `fecha` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: medio_pago
CREATE TABLE `medio_pago` (
  `id` int(11) NOT NULL,
  `tipo` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: orden_compra (SIMPLIFICADA - un solo medio de pago)
CREATE TABLE `orden_compra` (
  `id` int(11) NOT NULL,
  `estado` enum('pendiente','parcial','completada','cancelada') NOT NULL DEFAULT 'pendiente',
  `proveedores_id` int(11) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `medio_pago_id` int(11) NOT NULL,
  `fecha_pago` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: productos
CREATE TABLE `productos` (
  `id` int(11) NOT NULL,
  `nombre` varchar(60) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `precio_costo` decimal(10,2) DEFAULT NULL,
  `sku` varchar(100) NOT NULL,
  `activo` bit(1) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `stock_minimo` int(11) DEFAULT 5,
  `fecha_alta` datetime(6) NOT NULL,
  `proveedores_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: proveedores
CREATE TABLE `proveedores` (
  `id` int(11) NOT NULL,
  `razon_social` varchar(65) NOT NULL,
  `email` varchar(100) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  `direccion` varchar(60) NOT NULL,
  `cuit` varchar(15) NOT NULL,
  `activo` bit(1) NOT NULL,
  `fecha_alta` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: roles
CREATE TABLE `roles` (
  `id` int(11) NOT NULL,
  `nombre_rol` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: usuarios
CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nombre` varchar(60) NOT NULL,
  `apellido` varchar(60) NOT NULL,
  `email` varchar(70) NOT NULL,
  `password` varchar(80) NOT NULL,
  `roles_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: ventas (SIMPLIFICADA - un solo medio de pago)
CREATE TABLE `ventas` (
  `id` int(11) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `usuarios_id` int(11) NOT NULL,
  `estado` enum('pendiente','completada','cancelada') NOT NULL DEFAULT 'pendiente',
  `medio_pago_id` int(11) NOT NULL,
  `fecha_pago` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- Tabla: historial_precios
CREATE TABLE `historial_precios` (
  `id` int(11) NOT NULL,
  `producto_id` int(11) NOT NULL,
  `precio_anterior` decimal(10,2) NOT NULL,
  `precio_nuevo` decimal(10,2) NOT NULL,
  `fecha_cambio` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------
-- ÍNDICES Y CLAVES PRIMARIAS

ALTER TABLE `categorias` ADD PRIMARY KEY (`id`);
ALTER TABLE `categorias_productos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_productos_has_categoria_productos_categoria_idx` (`categoria_id`),
  ADD KEY `fk_productos_has_categoria_productos_productos1_idx` (`productos_id`);
ALTER TABLE `detalle_entrada`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_detalle_entrada_entrada1_idx` (`entrada_id`),
  ADD KEY `fk_detalle_entrada_productos1_idx` (`productos_id`);
ALTER TABLE `detalle_orden_compra`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_detalle_orden_compra_productos1_idx` (`productos_id`),
  ADD KEY `fk_detalle_orden_compra_orden_compra1_idx` (`orden_compra_id`);
ALTER TABLE `detalle_venta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_detalle_venta_ventas1_idx` (`ventas_id`),
  ADD KEY `fk_detalle_venta_productos1_idx` (`productos_id`);
ALTER TABLE `entrada`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_entradas_orden_compra1_idx` (`orden_compra_id`);
ALTER TABLE `medio_pago` ADD PRIMARY KEY (`id`);
ALTER TABLE `orden_compra`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_orden_compra_proveedores1_idx` (`proveedores_id`),
  ADD KEY `fk_orden_compra_medio_pago1_idx` (`medio_pago_id`);
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `sku_UNIQUE` (`sku`),
  ADD KEY `fk_productos_proveedores1_idx` (`proveedores_id`);
ALTER TABLE `proveedores` 
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `cuit_UNIQUE` (`cuit`),
  ADD UNIQUE KEY `email_UNIQUE` (`email`);
ALTER TABLE `roles` ADD PRIMARY KEY (`id`);
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email_UNIQUE` (`email`),
  ADD KEY `fk_usuarios_roles1_idx` (`roles_id`);
ALTER TABLE `ventas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_ventas_usuarios1_idx` (`usuarios_id`),
  ADD KEY `fk_ventas_medio_pago1_idx` (`medio_pago_id`);
ALTER TABLE `historial_precios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_historial_precios_productos1_idx` (`producto_id`);

-- --------------------------------------------------------
-- AUTO_INCREMENT

ALTER TABLE `categorias` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `categorias_productos` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `detalle_entrada` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `detalle_orden_compra` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `detalle_venta` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `entrada` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `medio_pago` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `orden_compra` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `productos` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `proveedores` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `roles` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `usuarios` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `ventas` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `historial_precios` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------
-- CLAVES FORÁNEAS

ALTER TABLE `categorias_productos`
  ADD CONSTRAINT `fk_productos_has_categoria_productos_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_productos_has_categoria_productos_productos1` FOREIGN KEY (`productos_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `detalle_entrada`
  ADD CONSTRAINT `fk_detalle_entrada_entrada1` FOREIGN KEY (`entrada_id`) REFERENCES `entrada` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_detalle_entrada_productos1` FOREIGN KEY (`productos_id`) REFERENCES `productos` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `detalle_orden_compra`
  ADD CONSTRAINT `fk_detalle_orden_compra_orden_compra1` FOREIGN KEY (`orden_compra_id`) REFERENCES `orden_compra` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_detalle_orden_compra_productos1` FOREIGN KEY (`productos_id`) REFERENCES `productos` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `detalle_venta`
  ADD CONSTRAINT `fk_detalle_venta_productos1` FOREIGN KEY (`productos_id`) REFERENCES `productos` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_detalle_venta_ventas1` FOREIGN KEY (`ventas_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `entrada`
  ADD CONSTRAINT `fk_entradas_orden_compra1` FOREIGN KEY (`orden_compra_id`) REFERENCES `orden_compra` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `orden_compra`
  ADD CONSTRAINT `fk_orden_compra_proveedores1` FOREIGN KEY (`proveedores_id`) REFERENCES `proveedores` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_orden_compra_medio_pago1` FOREIGN KEY (`medio_pago_id`) REFERENCES `medio_pago` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `productos`
  ADD CONSTRAINT `fk_productos_proveedores1` FOREIGN KEY (`proveedores_id`) REFERENCES `proveedores` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `usuarios`
  ADD CONSTRAINT `fk_usuarios_roles1` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `ventas`
  ADD CONSTRAINT `fk_ventas_usuarios1` FOREIGN KEY (`usuarios_id`) REFERENCES `usuarios` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_ventas_medio_pago1` FOREIGN KEY (`medio_pago_id`) REFERENCES `medio_pago` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `historial_precios`
  ADD CONSTRAINT `fk_historial_precios_productos1` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- --------------------------------------------------------
-- TRIGGERS

DELIMITER //

CREATE TRIGGER `after_producto_update`
AFTER UPDATE ON `productos`
FOR EACH ROW
BEGIN
  IF OLD.precio <> NEW.precio THEN
    INSERT INTO historial_precios (`producto_id`, `precio_anterior`, `precio_nuevo`)
    VALUES (OLD.id, OLD.precio, NEW.precio);
  END IF;
END;
//

DELIMITER ;

-- --------------------------------------------------------
-- VISTAS

CREATE VIEW `vista_resumen_ventas` AS
SELECT 
  p.id AS producto_id,
  p.nombre,
  SUM(dv.cantidad) AS total_vendido,
  SUM(dv.cantidad * dv.precio_unitario) AS ingresos_totales
FROM productos p
JOIN detalle_venta dv ON p.id = dv.productos_id
GROUP BY p.id, p.nombre;

-- --------------------------------------------------------
-- STORED PROCEDURES

DELIMITER //

CREATE PROCEDURE `obtener_compras_proveedor`(IN proveedorId INT)
BEGIN
  SELECT oc.id, oc.fecha, oc.total, oc.estado, mp.tipo as medio_pago
  FROM orden_compra oc
  JOIN medio_pago mp ON oc.medio_pago_id = mp.id
  WHERE oc.proveedores_id = proveedorId
  ORDER BY oc.fecha DESC;
END;
//

DELIMITER ;

-- --------------------------------------------------------
-- DATOS INICIALES

INSERT INTO `medio_pago` (`id`, `tipo`) VALUES
(1, 'Efectivo'),
(2, 'Tarjeta de Crédito'),
(3, 'Tarjeta de Débito'),
(4, 'Transferencia Bancaria'),
(5, 'Billetera Virtual');

INSERT INTO `roles` (`id`, `nombre_rol`) VALUES
(1, 'Administrador'),
(2, 'Vendedor'),
(3, 'Encargado de Stock');

-- Inserts para la tabla 'proveedores'
INSERT INTO `proveedores` (`razon_social`, `email`, `telefono`, `direccion`, `cuit`, `activo`, `fecha_alta`) VALUES
('TechWholesale S.A.', 'contacto@techwholesale.com', '01145678901', 'Av. Rivadavia 1234, CABA', '30-11223344-5', b'1', '2025-01-15 10:00:00.000000'),
('ElectroSupply SRL', 'ventas@electrosupply.com', '01123456789', 'Calle Falsa 123, GBA', '30-55667788-9', b'1', '2025-02-20 11:30:00.000000'),
('GlobalComponents S.A.', 'info@globalcomponents.net', '01178901234', 'Bernardo de Irigoyen 500, CABA', '30-99001122-3', b'1', '2025-03-01 09:15:00.000000'),
('DigitalDistro Ltd.', 'support@digitaldistro.co', '01187654321', 'Libertador 4567, CABA', '30-33445566-7', b'1', '2025-04-05 14:00:00.000000'),
('HardwareHub Inc.', 'sales@hardwarehub.com', '01165432109', 'Corrientes 987, GBA', '30-77889900-1', b'1', '2025-05-10 10:45:00.000000'),
('Componentes Premium', 'contact@componentespremium.com', '01112345678', 'Santa Fe 2000, CABA', '30-22334455-6', b'1', '2025-06-01 08:00:00.000000'),
('PowerTech Solutions', 'info@powertech.com.ar', '01198765432', 'Córdoba 1500, CABA', '30-66778899-0', b'1', '2025-06-15 13:00:00.000000'),
('MegaChips Distributors', 'ventas@megachips.net', '01155554444', 'Entre Ríos 800, CABA', '30-10203040-5', b'1', '2025-07-01 09:00:00.000000'),
('Future Electronics', 'contact@future-elec.com', '01177778888', 'San Juan 300, GBA', '30-50607080-9', b'1', '2025-07-10 11:00:00.000000'),
('Innovatech Parts', 'support@innovatech.com', '01199990000', 'Lavalle 100, CABA', '30-90807060-3', b'1', '2025-07-20 15:00:00.000000');

-- Inserts para la tabla 'categorias'
INSERT INTO `categorias` (`nombre`, `descripcion`) VALUES
('Monitores', 'Pantallas para computadoras y consolas'),
('Teclados', 'Dispositivos de entrada para escritura'),
('Mouses', 'Dispositivos de entrada para navegación'),
('Auriculares', 'Dispositivos de audio para escuchar y comunicar'),
('Almacenamiento SSD', 'Unidades de estado sólido para almacenamiento de datos'),
('Fuentes de Poder', 'Unidades de suministro de energía para PCs'),
('Gabinetes', 'Cajas para ensamblar componentes de PC'),
('Tarjetas Madre', 'Placas principales para conectar componentes'),
('Refrigeración', 'Sistemas de enfriamiento para componentes de PC'),
('Cables y Adaptadores', 'Conectores y adaptadores para dispositivos electrónicos');

-- Inserts para la tabla 'productos' (referenciando proveedores existentes)
INSERT INTO `productos` (`nombre`, `precio`, `precio_costo`, `sku`, `activo`, `stock`, `stock_minimo`, `fecha_alta`, `proveedores_id`) VALUES
('Monitor Curvo 27" 144Hz', 350.00, 280.00, 'MON-003', b'1', 20, 5, '2025-05-01 10:00:00.000000', 1),
('Teclado Mecánico RGB', 90.00, 65.00, 'TEC-001', b'1', 50, 10, '2025-05-05 11:00:00.000000', 2),
('Mouse Gaming Inalámbrico', 60.00, 40.00, 'MOU-001', b'1', 40, 8, '2025-05-10 12:00:00.000000', 3),
('Auriculares Gamer 7.1', 120.00, 90.00, 'AUD-001', b'1', 30, 6, '2025-05-15 13:00:00.000000', 4),
('SSD NVMe 1TB Gen4', 150.00, 110.00, 'SSD-001', b'1', 25, 5, '2025-05-20 14:00:00.000000', 5),
('Fuente de Poder 750W 80+ Gold', 100.00, 75.00, 'FUE-001', b'1', 18, 4, '2025-06-01 09:00:00.000000', 6),
('Gabinete Mid-Tower Vidrio Templado', 85.00, 60.00, 'GAB-001', b'1', 15, 3, '2025-06-05 10:30:00.000000', 7),
('Tarjeta Madre B650 AM5', 220.00, 170.00, 'TMA-001', b'1', 12, 3, '2025-06-10 11:45:00.000000', 8),
('Refrigeración Líquida AIO 240mm', 110.00, 80.00, 'REF-001', b'1', 10, 2, '2025-06-15 12:30:00.000000', 9),
('Cable HDMI 2.1 2 Metros', 15.00, 8.00, 'CAB-001', b'1', 100, 20, '2025-06-20 15:00:00.000000', 10);

-- Inserts aleatorios para la tabla 'categorias_productos'
INSERT INTO `categorias_productos` (`productos_id`, `categoria_id`) VALUES
(3, 2), -- Mouse Gaming Inalámbrico en Teclados (ejemplo de relación no obvia)
(1, 1), -- Monitor Curvo en Monitores
(4, 4), -- Auriculares Gamer en Auriculares
(5, 5), -- SSD NVMe en Almacenamiento SSD
(6, 6), -- Fuente de Poder en Fuentes de Poder
(7, 7), -- Gabinete Mid-Tower en Gabinetes
(8, 8), -- Tarjeta Madre en Tarjetas Madre
(9, 9), 
(10, 10), 
(2, 2); 

COMMIT;