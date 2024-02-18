#!/bin/bash

initial_dir=$(pwd)

cd .. && mvn clean install

cd "$initial_dir"

mvn clean package
