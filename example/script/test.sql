
create table if not exists simple_sql_test
(
     id serial primary key,
	 name varchar(100)
);

insert into simple_sql_test (name) values ('kimmy leo');
insert into simple_sql_test (name) values ('bill gates');
insert into simple_sql_test (name) values ('steve jobs');
insert into simple_sql_test (name) values ('jackie chan');
insert into simple_sql_test (name) values ('jet lee');
insert into simple_sql_test (name) values ('jay chou');
insert into simple_sql_test (name) values ('joey leo');
