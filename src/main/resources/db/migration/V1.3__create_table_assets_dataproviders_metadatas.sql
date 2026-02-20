create table if not exists public.assets_dataproviders_metadatas
(
  id                bigserial primary key,
  asset_id          uuid not null,
  data_provider_id  uuid not null,
  provider_asset_id varchar(255) not null,
  symbol            varchar(255) not null,
  name              varchar(255) not null,
  primary_class     varchar(255) not null,
  extra_datas       jsonb,
  tool_box          jsonb,
  created_at        timestamp with time zone not null default now(),
  constraint fk_assets_dataproviders_metadatas_asset
    foreign key (asset_id)
    references public.assets(id)
    on delete cascade
);

create unique index if not exists index_unique_assets_dataproviders_metadatas_asset_provider
  on public.assets_dataproviders_metadatas(asset_id, data_provider_id);

create unique index if not exists index_unique_assets_dataproviders_metadatas_provider_asset
  on public.assets_dataproviders_metadatas(data_provider_id, provider_asset_id);

create index if not exists index_assets_dataproviders_metadatas_provider_symbol
  on public.assets_dataproviders_metadatas(data_provider_id, upper(symbol));
