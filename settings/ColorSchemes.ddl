create table ColorSchemes (
color_scheme_id           bigint not null,
create_ts                 date,
update_ts                 date,
versions                  integer,
template_id               varchar(255),
class_name                varchar(255),
color_hex                 varchar(255),
display_name              varchar(255),
constraint pk_ColorSchemes primary key (color_scheme_id))
;