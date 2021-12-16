CREATE TABLE student
(
    student           uuid primary key,
    name              varchar   not null,
    created_utc_date  timestamp not null,
    modified_utc_date timestamp not null
);