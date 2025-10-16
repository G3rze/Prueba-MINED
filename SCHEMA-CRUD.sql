-- ================================================
-- SCHEMA: public
-- Proyecto: CRUD de Productos y Stock
-- ================================================

-- Usar el esquema público
SET search_path TO public;

-- ========================
-- TABLA: productos
-- ========================
CREATE TABLE IF NOT EXISTS public.productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(12, 2) NOT NULL CHECK (precio >= 0),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- TABLA: stock
-- ========================
CREATE TABLE IF NOT EXISTS public.stock (
    id SERIAL PRIMARY KEY,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad >= 0),
    ubicacion VARCHAR(100),
    ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_producto FOREIGN KEY (producto_id)
        REFERENCES public.productos (id)
        ON DELETE CASCADE,
    CONSTRAINT unq_stock_producto UNIQUE (producto_id)
);

-- ========================
-- ÍNDICES
-- ========================
CREATE INDEX IF NOT EXISTS idx_productos_nombre ON public.productos (nombre);
CREATE INDEX IF NOT EXISTS idx_stock_producto_id ON public.stock (producto_id);

-- ========================
-- DATOS DE EJEMPLO (Opcional)
-- ========================
INSERT INTO public.productos (nombre, descripcion, precio)
VALUES 
    ('Teclado Mecánico', 'Teclado retroiluminado con switches azules', 45.99),
    ('Mouse Inalámbrico', 'Mouse ergonómico con batería recargable', 25.50),
    ('Monitor 24"', 'Monitor Full HD con entrada HDMI', 120.00)
ON CONFLICT DO NOTHING;

INSERT INTO public.stock (producto_id, cantidad, ubicacion)
VALUES 
    (1, 10, 'Bodega Central'),
    (2, 20, 'Sucursal Norte'),
    (3, 5, 'Bodega Central')
ON CONFLICT DO NOTHING;

