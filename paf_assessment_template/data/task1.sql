-- Write your Task 1 answers in this file
create database bedandbreakfast;

use bedandbreakfast;

create table users(
    email varchar(128) not null,
    name varchar(128) not null,

    constraint pk_email primary key(email)
);

create table bookings(
    booking_id char(8) not null,
    listing_id varchar(20) not null,
    duration int not null,
    email varchar(128) not null,

    constraint pk_booking_id primary key(booking_id),
    constraint fk_email foreign key (email) references users(email)
);

create table reviews(
    id int auto_increment,
    date timestamp not null,
    listing_id varchar(20) not null,
    reviewer_name varchar(64) not null,
    comments text not null,

    constraint pk_id primary key(id),
    constraint fk_listing_id foreign key(listing_id) references bookings(listing_id)
);

insert into users (email, name) values
("fred@gmail.com", "Fred Flintstone"),
("barney@gmail.com", "Barney Rubble"),
("fry@planetexpress.com", "Philip J Fry"),
("hlmer@gmail.com", "Homer Simpson");