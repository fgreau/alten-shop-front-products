-- regular products, one of each inventory status and category
INSERT INTO product (id, code, name, price, quantity, inventory_status, category, deleted)
VALUES (1, 'product1', 'Product 1', 25, 20, 'INSTOCK', 'ACCESSORIES', false),
       (2, 'product2', 'Product 2', 50, 8, 'LOWSTOCK', 'FITNESS', false),
       (3, 'productspec3', 'Product spec 3', 12, 5, 'LOWSTOCK', 'ELECTRONICS', false),
       (4, 'productspec4', 'Product spec 4', 36.5, 0, 'OUTOFSTOCK', 'CLOTHING', false);

-- deleted product
INSERT INTO product (id, code, name, price, quantity, inventory_status, category, deleted)
VALUES (5, 'product_deleted','Deleted product', 100, 12, 'INSTOCK', 'FITNESS', true);