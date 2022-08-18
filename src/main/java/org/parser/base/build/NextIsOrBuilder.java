package org.parser.base.build;

/**
 * Der NextIsOrBuilder dient nur dazu, dass der Benutzer dazu gezwungen wird, als Nächstes die or-Methode aufzurufen,
 * oder die end-Methode, welche die ganze Rule beendet.
 * @param <TYPE> Typ des AST
 * @param <ANNOTATION> Annotation des AST
 */
public class NextIsOrBuilder<TYPE, ANNOTATION> {
    /**
     * Der zugrundeliegende ParserBuilder. Dieser wird bei der end-Methode benötigt, damit die entstandene
     * Rule zu ihm hinzugefügt werden kann.
     */
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    /**
     * Der zugrundeliegende RuleBuilder. Dieser wird benötigt, damit bei Aufruf der or-Methode die Regel
     * ergänzt werden kann.
     */
    private final RuleBuilder<TYPE, ANNOTATION> ruleBuilder;

    public NextIsOrBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder, RuleBuilder<TYPE, ANNOTATION> ruleBuilder) {
        this.parserBuilder = parserBuilder;
        this.ruleBuilder = ruleBuilder;
    }

    /**
     *
     * @return Gibt einfach den zugrundeliegenden RuleBuilder zurück.
     */
    public RuleBuilder<TYPE, ANNOTATION> or() {
        return ruleBuilder;
    }

    /**
     * Beendet den RuleBuilder, sodass ein Methoden-Aufruf auf diesem nichts mehr bewirkt.
     * Die entstandene Rule wird dann dem ParserBuilder hinzugefügt.
     */
    public void end() {
        parserBuilder.addParser(ruleBuilder.getName(), ruleBuilder.freeze());
    }
}
