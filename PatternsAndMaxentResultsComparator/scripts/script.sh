#!/bin/sh

GATE_HOME=/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN
GATE_PLUGINS=/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN/plugins
ANNIE_HOME=/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN/plugins/ANNIE
TAGGER_FRAMEWORK_FOLDER=/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN/plugins/Tagger_Framework
TREE_TAGGER=/home/ghamnia/TreeTagger/tree-tagger-french
CORPUS_FOLDER=/home/ghamnia/developpement/corpus
JAPE_RULES_FOLDER=/home/ghamnia/developpement/jape
GAZETTER_LIST_DEF=/home/ghamnia/developpement/liste_termes/list.def

java -Xmx256g -Dgate.home=$GATE_HOME -Dgate.plugins.home=$GATE_PLUGINS -jar PatternsAndMaxentResultsComparator.jar $ANNIE_HOME $TAGGER_FRAMEWORK_FOLDER $CORPUS_FOLDER $JAPE_RULES_FOLDER $TREE_TAGGER $GAZETTER_LIST_DEF