# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table choosers (
  chooser_id                bigint not null,
  create_ts                 date,
  update_ts                 date,
  versions                  bigint,
  placement                 varchar(255),
  hsvpanel                  boolean,
  slider                    boolean,
  swatche                   boolean,
  constraint pk_choosers primary key (chooser_id))
;

create table ColorSchemes (
  color_scheme_id           bigint not null,
  create_ts                 date,
  update_ts                 date,
  versions                  bigint,
  template_id               varchar(255),
  class_name                varchar(255),
  color_hex                 varchar(255),
  display_name              varchar(255),
  constraint pk_ColorSchemes primary key (color_scheme_id))
;

create table Images (
  image_id                  bigint not null,
  create_ts                 date,
  update_ts                 date,
  versions                  bigint,
  member_member_id          bigint,
  image_name                varchar(255),
  image_message             varchar(255),
  image_type                varchar(255),
  constraint pk_Images primary key (image_id))
;

create table Members (
  member_id                 bigint not null,
  create_ts                 date,
  update_ts                 date,
  versions                  bigint,
  member_name               varchar(255),
  nick_name                 varchar(255),
  password                  varchar(255),
  mail                      varchar(255),
  chooser_chooser_id        bigint,
  last_login                timestamp,
  constraint pk_Members primary key (member_id))
;

create table Templates (
  template_id               bigint not null,
  create_ts                 date,
  update_ts                 date,
  versions                  bigint,
  member_member_id          bigint,
  template_name             varchar(255),
  template_message          varchar(255),
  access_flag               integer,
  html                      blob,
  constraint pk_Templates primary key (template_id))
;

create sequence choosers_seq;

create sequence ColorSchemes_seq;

create sequence Images_seq;

create sequence Members_seq;

create sequence Templates_seq;

alter table Images add constraint fk_Images_member_1 foreign key (member_member_id) references Members (member_id) on delete restrict on update restrict;
create index ix_Images_member_1 on Images (member_member_id);
alter table Members add constraint fk_Members_chooser_2 foreign key (chooser_chooser_id) references choosers (chooser_id) on delete restrict on update restrict;
create index ix_Members_chooser_2 on Members (chooser_chooser_id);
alter table Templates add constraint fk_Templates_member_3 foreign key (member_member_id) references Members (member_id) on delete restrict on update restrict;
create index ix_Templates_member_3 on Templates (member_member_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists choosers;

drop table if exists ColorSchemes;

drop table if exists Images;

drop table if exists Members;

drop table if exists Templates;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists choosers_seq;

drop sequence if exists ColorSchemes_seq;

drop sequence if exists Images_seq;

drop sequence if exists Members_seq;

drop sequence if exists Templates_seq;

