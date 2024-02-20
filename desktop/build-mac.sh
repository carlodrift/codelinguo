#!/bin/bash

initial_dir=$(pwd)

cd .. && mvn clean install

cd "$initial_dir"

jpackage --input target/ --name CodeLinguo --main-jar CodeLinguo.jar --main-class fr.unilim.codelinguo.desktop.MainKt --icon src/main/resources/logo/logo.icns --type dmg
