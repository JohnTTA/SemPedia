#!/bin/sh

JAVA=/logiciels/java1.8/bin/java
LIB=/projets/melodi/RelExt/
LOCAL_LIB=/users/melodi/aghamnia/developpement/MLHypernymExtractor/lib
JAR=/users/melodi/aghamnia/developpement/MLHypernymExtractor/MLHypernymExtractor.jar
CONFIG=/projets/melodi/RelExt/config
GATE_BIN=/users/melodi/aghamnia/gate-8.1-build5169-BIN/bin
GATE_LIB=/users/melodi/aghamnia/gate-8.1-build5169-BIN/lib
CORPUS=/users/melodi/aghamnia/developpement/MLHypernymExtractor/PairesHomonymeNettoyees.txt
#$JAVA -jar /projets/melodi/RelExt/validation.jar /projets/melodi/RelExt/RelCandFR.txt rang

# BabelNet 3.7 : attention aux fichiers dans le repertoire config/ qui pointent vers la versao BabelNet 
#$JAVA -classpath $LIB/lib/*:$LIB/babelnet-api-3.7.1.jar:$LIB/validation.jar:config Evaluation /projets/melodi/RelExt/RelCandFR.txt 5

# BabelNet 3.7 : attention aux fichiers dans le repertoire config/ qui pointent vers la versao BabelNet 
$JAVA -classpath $GATE_LIB/*:$GATE_BIN/*:$LIB/BabelNet3.6/lib/*:$LIB/BabelNet3.6/babelnet-api-3.6.jar:$LOCAL_LIB/*:$CONFIG:$JAR org/mlhypernymextractor/core/Main -validation $CORPUS

