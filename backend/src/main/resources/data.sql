INSERT INTO categorias (eliminado, created_at, updated_at, version, nombre, descripcion) VALUES
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Hamburguesas', 'Hamburguesas artesanales y smash'),
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Pizzas', 'Pizzas de masa madre'),
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Bebidas', 'Bebidas frías y calientes');

INSERT INTO productos (eliminado, created_at, updated_at, version, nombre, descripcion, precio, stock, imagen, disponible, categoria_id) VALUES
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Hamburguesa Triple', 'Hamburguesa con triple carne, cheddar y salsa especial', 12500.00, 25, 'producto-1.jpg', true, 1),
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Hamburguesa BBQ', 'Hamburguesa con cebolla crispy y salsa BBQ', 11800.00, 18, 'producto-2.jpg', true, 1),
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Hamburguesa Chicken', 'Hamburguesa de pollo crispy con mayonesa de ajo', 10900.00, 20, 'producto-3.jpg', true, 1),
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Pizza Muzzarella', 'Pizza clásica con abundante muzzarella', 9900.00, 30, 'producto-4.jpg', true, 2),
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Pizza Napolitana', 'Pizza con tomate, ajo y albahaca fresca', 10400.00, 15, 'producto-5.jpg', true, 2),
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Pizza Pepperoni', 'Pizza con pepperoni y queso extra', 11250.00, 12, 'producto-6.jpg', true, 2),
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Limonada', 'Limonada fresca con menta', 2500.00, 40, 'producto-7.jpg', true, 3),
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Gaseosa Cola', 'Bebida cola bien fría', 2200.00, 50, 'producto-8.jpg', true, 3),
  (true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Combo Antiguo', 'Producto dado de baja del catálogo', 15000.00, 0, 'producto-9.jpg', true, 1),
  (false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'Pizza 4 Quesos', 'Pizza con mezcla de cuatro quesos', 12800.00, 8, 'producto-10.jpg', false, 2);
