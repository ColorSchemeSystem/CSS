create table Templates (
template_id               bigint not null,
create_ts                 date,
update_ts                 date,
versions                  integer,
member_id                 bigint,
template_name             varchar(255),
message                   varchar(255),
access_flag               integer,
constraint pk_Templates primary key (template_id))
;