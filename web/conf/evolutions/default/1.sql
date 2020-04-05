-- !Ups

create table "people" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "name" varchar not null,
  "age" int not null
);

create table "food" (
    "id" bigint generated by default as identity(start with 1) not null primary key,
    "name" varchar not null,
    "calories" int,
    "type" varchar default 'other',
    "created" timestamp
);

-- !Downs

drop table people cascade;
drop table food cascade;
