create table if not exists public.assets
(
  id            uuid primary key default gen_random_uuid(),
  symbol        varchar(255) not null,
  name          varchar(255) not null,
  primary_class varchar(255) not null,
  blacklisted   boolean not null,
  created_at    timestamp with time zone not null default now()
);

alter table if exists public.assets
  add column if not exists blacklisted boolean not null;

create unique index if not exists index_unique_assets
  on public.assets (upper(symbol), upper(name), primary_class);
