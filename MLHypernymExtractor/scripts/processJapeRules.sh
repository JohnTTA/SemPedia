#!/bin/sh
ANNIE_HOME="/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN/plugins/ANNIE"
GATE_HOME="/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN"
CORPUS_FOLDER="/home/ghamnia/developpement/patrons/corpus"
JAPE_RULES_FOLDER="/home/ghamnia/developpement/patrons/patrons"
TAGGER_FRAMEWORK_FOLDER="/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN/plugins/Tagger_Framework"
TTG_SCRIPT="/home/ghamnia/TreeTagger/tree-tagger-french"
LIST_GAZETTER="/home/ghamnia/developpement/patrons/termes/termes_man.def"

java -Xmx200g -Dgate.home=$GATE_HOME -jar ProcessJapeRules.jar $ANNIE_HOME $CORPUS_FOLDER $JAPE_RULES_FOLDER $TAGGER_FRAMEWORK_FOLDER $TTG_SCRIPT $LIST_GAZETTER