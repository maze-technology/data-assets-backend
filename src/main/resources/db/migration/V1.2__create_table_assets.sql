create table if not exists public.assets
(
  id                        uuid primary key default gen_random_uuid(),
  symbol                    varchar(255) not null,
  name                      varchar(255) not null,
  primary_class             varchar(255) not null,
  data_providers_meta_datas jsonb not null default '[]'::jsonb,
  blacklisted               boolean not null default false,
  created_at                timestamp with time zone not null default now()
);

alter table if exists public.assets
  add column if not exists data_providers_meta_datas jsonb not null default '[]'::jsonb;

alter table if exists public.assets
  add column if not exists blacklisted boolean not null default false;

create unique index if not exists index_unique_assets
  on public.assets (upper(symbol), upper(name), primary_class);

create index if not exists index_assets_data_providers_meta_datas_gin
  on public.assets using gin (data_providers_meta_datas);
