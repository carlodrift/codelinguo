#!/bin/bash

cd ~/IdeaProjects/sae_s5 && mvn clean package && jpackage --input target/ --name CodeLinguo --main-jar codelinguo-1.1.jar --main-class fr.unilim.saes5.MainKt --icon src/main/resources/logo.icns --type dmg
