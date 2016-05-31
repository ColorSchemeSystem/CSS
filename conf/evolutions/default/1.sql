# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table choosers (
  chooser_id                bigint not null,
  create_ts                 date,
  update_ts                 date,
  versions                  bigint,
  member_id                 bigint,
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

create table Members (
  member_id                 bigint not null,
  create_ts                 date,
  update_ts                 date,
  versions                  bigint,
  member_name               varchar(255),
  password                  varchar(255),
  mail                      varchar(255),
  constraint pk_Members primary key (member_id))
;

create sequence choosers_seq;

create sequence ColorSchemes_seq;

create sequence Members_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists choosers;

drop table if exists ColorSchemes;

drop table if exists Members;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists choosers_seq;

drop sequence if exists ColorSchemes_seq;

drop sequence if exists Members_seq;

