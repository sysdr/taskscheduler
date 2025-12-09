#!/bin/bash
cd "$(dirname "$0")/.."
mvn clean install -DskipTests
