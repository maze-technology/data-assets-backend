alter table if exists public.assets
    add column if not exists blacklisted boolean not null default false;
