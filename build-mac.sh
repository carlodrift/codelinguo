#!/bin/bash

mvn clean package && jpackage --input target/ --name CodeLinguo --main-jar CodeLinguo.jar --main-class fr.unilim.codelinguo.MainKt --icon src/main/resources/logo/logo.icns --type dmg
