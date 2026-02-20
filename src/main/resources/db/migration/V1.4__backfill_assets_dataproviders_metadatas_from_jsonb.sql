insert into public.assets_dataproviders_metadatas (
  asset_id,
  data_provider_id,
  provider_asset_id,
  symbol,
  name,
  primary_class,
  extra_datas,
  tool_box,
  created_at
)
select
  a.id,
  nullif(coalesce(m ->> 'dataProviderId', m ->> 'data_provider_id'), '')::uuid as data_provider_id,
  nullif(coalesce(m -> 'asset' ->> 'id', m ->> 'id'), '') as provider_asset_id,
  coalesce(m -> 'asset' ->> 'symbol', m ->> 'symbol', a.symbol) as symbol,
  coalesce(m -> 'asset' ->> 'name', m ->> 'name', a.name) as name,
  coalesce(m ->> 'primaryClass', m ->> 'primary_class', a.primary_class) as primary_class,
  coalesce(m -> 'extraDatas', m -> 'extra_datas', '{}'::jsonb) as extra_datas,
  coalesce(m -> 'toolBox', m -> 'tool_box', '{}'::jsonb) as tool_box,
  a.created_at
from public.assets a
cross join lateral jsonb_array_elements(coalesce(a.data_providers_meta_datas, '[]'::jsonb)) as m
where nullif(coalesce(m ->> 'dataProviderId', m ->> 'data_provider_id'), '') is not null
  and nullif(coalesce(m -> 'asset' ->> 'id', m ->> 'id'), '') is not null
on conflict do nothing;
