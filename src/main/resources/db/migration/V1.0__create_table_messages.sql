create table public.messages
(
  id          uuid primary key default gen_random_uuid(),
  content     varchar(255) not null,
  created_at  timestamp with time zone not null default now()
);
