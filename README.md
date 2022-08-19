# inline-parser
Bibliothek zum Erstellen von Parsern im Java-Code.

### Grundsätzliches
inline-parser ist eine Bibliothek zum Erstellen von Parsern, ohne einen Parser-Generator
zu verwenden. Die Grammatik wird also direkt im Code angeben und anschließend
wird daraus ein rekursiver Parser erstellt.
Die Bibliothek versucht vor allem eine einfache Lesbarkeit und Implementierbarkeit zu ermöglichen;
weniger im Vordergrund steht die Möglichkeit high-performance Parser erstellen zu können.

### Beispiel
In diesem Abschnitt möchte ich in sehr knapper Form die Funktionsweise dieser
Bibliothek vorstellen. Wenn sie ausführliche Beispiele sehen wollen, schauen sie einfach im
`org.parser.examples` Package nach.

Als einführendes Beispiel in die Bibliothek, möchte ich zeigen, wie ein if-Parser
erstellt werden kann. Zunächst muss hierfür ein `ParserBuilder` Objekt erstellt werden:
```java
ParserBuilder<TYPE, ANNOTATION> builder = new ParserBuilder<>();
```
Nun können wir in diesem Builder Regeln erstellen, die die Grammatik repräsentieren.
Eine Regel kann man sich als eine Zeile in der Backus-Naur-Form vorstellen. Ein
If-Statement besteht aus `if`, `(`, `condition`, `)` und dann `statements`.
Eine solche Regel kann man so konstruieren:
```java
builder.newRule("IF").consistsOf()
        .concat(TYPE.IF).hide("if").hide("\\(").rule("CONDITION").hide("\\)").rule("BLOCK")
        .end();
```
Nun muss man noch die Regeln "CONDITION" und "BLOCK" definieren. Exemplarisch zeigen
wir hier, wie eine "CONDITION"-Regel erstellt werden kann mit den Vergleichsoperatoren
<= und >=:
```java
builder.newRule("COND").consistsOf()
        .concat(TYPE.LEQ).rule("LITERAL").hide("<=").rule("LITERAL")
        .or()
        .concat(TYPE.GEQ).rule("LITERAL").hide(">=").rule("LITERAL")
        .end();
```
Würde man nun noch die Regeln "BLOCK" und "LITERAL" erstellen, könnte man am Schluss
einen `ParserPool` für diese Grammatik folgendermaßen erstellen:
```java
ParserPool<TYPE, ANNOTATION> pool = builder.build();
```

Hierbei ist `TYPE` ein Enum mit den Werten `IF, LEQ, GEQ, ...` und 
`ANNOTATION` ist eine Klasse (diese müssen sie selber definieren), mit der sie beim entstehenden AST 
Anmerkungen an die Knoten schreiben können. Außerdem kommen die Backslashes in den hide-Methoden
daher, dass dort eine Regular Expression angegeben werden muss.

### Autor
Frederik Böcker

