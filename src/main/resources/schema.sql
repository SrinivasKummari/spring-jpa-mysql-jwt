create database spring_boot_jwt;

create table user (
	user_id bigint auto_increment primary key,
	name varchar(100) not null, 
	email varchar(300) not null unique key,
	user_name varchar(300) not null unique key, 
	password varchar(100) not null  
); 

create table role (
	role_id bigint auto_increment primary key,
	role_name varchar(100) not null 
); 

create table user_role (
	user_role_id bigint auto_increment primary key,
	user_id bigint not null,
    role_id bigint not null,
    foreign key (user_id) references user(user_id),
    foreign key(role_id) references role(role_id)
);


INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_PM');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');