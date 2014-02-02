#!/bin/bash

user=$1
password=$2

for SQL in *.sql
do
	DB=${SQL/\.sql/}
	mysqladmin -u $user --password=$password --force drop $DB
done