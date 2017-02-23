#!/bin/sh

GATE_HOME=/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN
GATE_PLUGINS=/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN/plugins
ANNIE_HOME=/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN/plugins/ANNIE
TAGGER_FRAMEWORK_FOLDER=/home/ghamnia/textToBabelnetValidation/gate-8.1-build5169-BIN/plugins/Tagger_Framework
TREE_TAGGER=/home/ghamnia/TreeTagger/tree-tagger-french
CORPUS_FOLDER=/home/ghamnia/developpement/corpus
JAPE_RULES_FOLDER=/home/ghamnia/developpement/jape
GAZETTER_LIST_DEF=/home/ghamnia/developpement/liste_termes

java -Xmx256g -Dgate.home=$GATE_HOME -Dgate.plugins.home=$GATE_PLUGINS -jar MLHypernymExtractor.jar -generate-multiple-term-combination $CORPUS_FOLDER $ANNIE_HOME $GAZETTER_LIST_DEF