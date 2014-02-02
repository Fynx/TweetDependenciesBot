#!/bin/bash

user=$1
password=$2

for SQL in *.sql
do
	DB=${SQL/\.sql/}
	echo Importing $DB
	mysqladmin -u $user --password=$password create $DB
	mysql -u $user --password=$password $DB < $SQL
done