#!/bin/bash

mvn clean compile
sudo chmod +x target/classes/driver/linux/amd64/chromedriver
sudo chmod +x target/classes/driver/linux/x86/chromedriver
mvn verify -Djp.skip=false