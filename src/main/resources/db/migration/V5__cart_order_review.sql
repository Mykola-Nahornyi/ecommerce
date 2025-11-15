CREATE TABLE carts (
  id serial PRIMARY KEY,
  user_id bigint UNIQUE REFERENCES users(id)
);

CREATE TABLE cart_items (
  id serial PRIMARY KEY,
  cart_id bigint REFERENCES carts(id) ON DELETE CASCADE,
  product_id bigint REFERENCES products(id),
  quantity integer NOT NULL
);

CREATE TABLE orders (
  id serial PRIMARY KEY,
  user_id bigint REFERENCES users(id),
  created_at timestamp,
  total numeric(12,2),
  status varchar(50)
);

CREATE TABLE order_items (
  id serial PRIMARY KEY,
  order_id bigint REFERENCES orders(id) ON DELETE CASCADE,
  product_id bigint REFERENCES products(id),
  quantity integer,
  price numeric(12,2)
);

CREATE TABLE reviews (
  id serial PRIMARY KEY,
  product_id bigint REFERENCES products(id),
  user_id bigint REFERENCES users(id),
  rating integer,
  comment text,
  created_at timestamp,
  CONSTRAINT uniq_user_product UNIQUE (product_id, user_id)
);
