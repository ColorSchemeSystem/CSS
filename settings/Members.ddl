create table Members (
member_id                 bigint not null,
create_ts                 date,
update_ts                 date,
versions                  integer,
member_name               varchar(255),
password                  varchar(255),
mail                      varchar(255),
constraint pk_Members primary key (member_id))
;