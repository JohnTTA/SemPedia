#!/bin/sh

ANNIE_HOME=/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN/plugins/ANNIE
TAGGER_FRAMEWORK_FOLDER=/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN/plugins/Tagger_Framework
TREE_TAGGER=/home/ghamnia/textToBabelnetValidation/TextToBabelNetValidation/TreeTagger2/bin
CORPUS_FOLDER=$1
JAPE_RULES_FOLDER=$2

java -Xmx256g -jar PatternsAndMaxentResultsComparator.jar $ANNIE_HOME $TAGGER_FRAMEWORK_FOLDER $TREE_TAGGER $CORPUS_FOLDER $JAPE_RULES_FOLDER