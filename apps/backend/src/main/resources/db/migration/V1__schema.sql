-- Esquema base del e-commerce: catálogo (categorías, productos, detalle, reseñas),
-- cupones, clientes (sin autenticación), y órdenes con su desglose e historial de estados.
-- Ver docs/modelo-datos.md.

-- ============================ Catálogo ============================

create table categories (
    id            bigint generated always as identity primary key,
    name          varchar(60)   not null unique,
    discount_rate numeric(5, 4) not null default 0
                  check (discount_rate >= 0 and discount_rate < 1)
);

create table products (
    id          bigint generated always as identity primary key,
    sku         varchar(40)    not null unique,
    name        varchar(120)   not null,
    unit_price  numeric(12, 2) not null check (unit_price >= 0),
    category_id bigint         not null references categories (id),
    stock       integer        not null check (stock >= 0),
    active      boolean        not null default true,
    created_at  timestamptz    not null default now()
);
create index idx_products_category_id on products (category_id);

-- Detalle 1:1: la PK es la FK a products (garantiza el 1:1 estricto).
create table product_details (
    product_id   bigint primary key references products (id) on delete cascade,
    display_name varchar(160) not null,
    description  text,
    brand        varchar(80),
    image_url    varchar(500),
    updated_at   timestamptz  not null default now()
);

create table product_reviews (
    id            bigint generated always as identity primary key,
    product_id    bigint        not null references products (id) on delete cascade,
    rating        smallint      not null check (rating between 1 and 5),
    title         varchar(120),
    comment       varchar(1000),
    reviewer_name varchar(120)  not null,
    created_at    timestamptz   not null default now()
);
create index idx_product_reviews_product_id on product_reviews (product_id);

-- ============================ Cupones ============================

create table coupons (
    code          varchar(40)   primary key,
    description   varchar(160),
    discount_rate numeric(5, 4) not null check (discount_rate > 0 and discount_rate <= 1),
    active        boolean       not null default true,
    valid_from    timestamptz,
    valid_until   timestamptz
);

-- ==================== Clientes (sin autenticación) ====================

create table customers (
    id         bigint generated always as identity primary key,
    username   varchar(40)  not null unique
               check (username ~ '^[a-z0-9_.-]{3,40}$'),
    full_name  varchar(120),
    email      varchar(160),
    created_at timestamptz  not null default now()
);
create unique index uq_customers_email on customers (email) where email is not null;

-- ==================== Estados de la orden (catálogo) ====================

create table order_statuses (
    code       varchar(16) primary key,
    label      varchar(40) not null,
    sort_order smallint    not null unique,
    is_final   boolean     not null default false
);

-- ============================ Órdenes ============================

create table orders (
    id             bigint generated always as identity primary key,
    radicado       varchar(24)    not null unique
                   check (radicado ~ '^[A-Z]{2,5}-[0-9]{14,17}$'),
    customer_id    bigint         references customers (id),
    status_code    varchar(16)    not null default 'COMPRADO' references order_statuses (code),
    created_at     timestamptz    not null default now(),
    coupon_code    varchar(40)    references coupons (code),
    original_total numeric(12, 2) not null check (original_total >= 0),
    total_discount numeric(12, 2) not null check (total_discount >= 0),
    effective_rate numeric(6, 4)  not null check (effective_rate >= 0),
    final_total    numeric(12, 2) not null check (final_total >= 0),
    cap_reached    boolean        not null
);
create index idx_orders_customer_id on orders (customer_id);
create index idx_orders_status_code on orders (status_code);

create table order_lines (
    id            bigint generated always as identity primary key,
    order_id      bigint         not null references orders (id) on delete cascade,
    product_id    bigint         not null references products (id),
    product_name  varchar(160)   not null,
    unit_price    numeric(12, 2) not null check (unit_price >= 0),
    category_name varchar(60)    not null,
    quantity      integer        not null check (quantity > 0),
    line_total    numeric(12, 2) not null check (line_total >= 0)
);
create index idx_order_lines_order_id on order_lines (order_id);

-- Desglose: descuento CRUDO por regla (antes del tope del 35%).
create table order_discounts (
    id       bigint generated always as identity primary key,
    order_id bigint         not null references orders (id) on delete cascade,
    type     varchar(16)    not null check (type in ('CATEGORY', 'VOLUME', 'COUPON')),
    rate     numeric(5, 4)  not null check (rate >= 0),
    amount   numeric(12, 2) not null check (amount >= 0),
    unique (order_id, type)
);
create index idx_order_discounts_order_id on order_discounts (order_id);

create table order_status_history (
    id          bigint generated always as identity primary key,
    order_id    bigint      not null references orders (id) on delete cascade,
    status_code varchar(16) not null references order_statuses (code),
    changed_at  timestamptz not null default now(),
    note        varchar(200)
);
create index idx_order_status_history_order_id on order_status_history (order_id);
