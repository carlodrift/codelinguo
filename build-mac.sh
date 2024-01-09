#!/bin/bash

cd ~/IdeaProjects/sae_s5 && mvn clean package && jpackage --input target/ --name SaeS5 --main-jar sae_s5-1.0.jar --main-class fr.unilim.saes5.MainKt --type dmg
