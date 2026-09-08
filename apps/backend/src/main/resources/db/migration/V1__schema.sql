-- Esquema base: catálogo de productos y órdenes con su desglose de descuentos.

create table products (
    id         bigint generated always as identity primary key,
    name       varchar(120)   not null,
    unit_price numeric(12, 2) not null check (unit_price >= 0),
    category   varchar(60)    not null,
    stock      integer        not null check (stock >= 0)
);

create table orders (
    id             bigint generated always as identity primary key,
    created_at     timestamptz    not null default now(),
    coupon_code    varchar(40),
    original_total numeric(12, 2) not null,
    total_discount numeric(12, 2) not null,
    effective_rate numeric(6, 4)  not null,
    final_total    numeric(12, 2) not null,
    cap_reached    boolean        not null
);

create table order_lines (
    id           bigint generated always as identity primary key,
    order_id     bigint         not null references orders (id),
    product_id   bigint         not null,
    product_name varchar(120)   not null,
    unit_price   numeric(12, 2) not null,
    quantity     integer        not null check (quantity > 0),
    category     varchar(60)    not null
);

create index idx_order_lines_order_id on order_lines (order_id);
