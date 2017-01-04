-- directory.sql
-- Written by Tyler Ford for Mp3 Player Application
-- It stores the strings of directory path

CONNECT 'jdbc:derby:directory;create=true';

CREATE TABLE Directory (
	Id int,
	DPath varchar(200),
	PRIMARY KEY(Id)
);