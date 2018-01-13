Stanford Lexicalized Parser v3.8.0 - 2017-06-09
-----------------------------------------------

Copyright (c) 2002-2015 The Board of Trustees of The Leland Stanford Junior
University. All Rights Reserved.

Original core parser code by Dan Klein.  Support code, additional
modules, languages, features, internationalization, compaction, typed
dependencies, etc. by Christopher Manning, Roger Levy, Teg Grenager,
Galen Andrew, Marie-Catherine de Marneffe, Jenny Finkel, Spence Green,
Bill MacCartney, Anna Rafferty, Huihsin Tseng, Pi-Chuan Chang,
Wolfgang Maier, Richard Eckart, Richard Socher, John Bauer,
Sebastian Schuster, and Jon Gauthier.

This release was prepared by Jason Bolton.

This package contains 4 parsers: a high-accuracy unlexicalized PCFG; a
lexicalized dependency parser; a factored model, where the estimates
of dependencies and an unlexicalized PCFG are jointly optimized to
give a lexicalized PCFG treebank parser; and an RNN parser, where
recursive neural networks trained with semantic word vectors are used
to score parse trees.  Also included are grammars for various
languages for use with these parsers.

For more information about the parser API, point a web browser at the
included javadoc directory (use the browser's Open File command to open
the index.html file inside the javadoc folder).  Start by looking at the
Package page for the edu.stanford.nlp.parser.lexparser package, and then
look at the page for the LexicalizedParser class documentation therein,
particularly documentation of the main method.

Secondly, you should also look at the Parser FAQ on the web:

    http://nlp.stanford.edu/software/parser-faq.shtml

This software requires Java 8 (JDK 1.8.0+).  (You must have installed it
separately. Check that the command "java -version" works and gives 1.8+.)

[QUICK START]
## GUI
```sh
$ ./lexparser-gui.sh
```
## Complie
```sh
$ javac -classpath ./stanford-parser.jar:./stanford-parser-3.8.0-sources.jar:./stanford-parser-3.8.0-models.jar ParserDemo.java
```

## Execution
```sh
$ java -classpath 'stanford-parser.jar:stanford-parser-3.8.0-models.jar:slf4j-api.jar:ejml-0.23.jar:' ParserDemo edu/stanford/nlp/models/lexparser/englishRNN.ser.gz data/test.txt
```
