package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.Parser;
import org.parser.tree.AST;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Der ConcatRuleBuilder ermöglicht das Bauen einer Concat-Subrule (also mehreren RegEx oder Rule-Parsern hintereinander)
 * @param <TYPE> Typ des AST
 * @param <ANNOTATION> Annotation des AST
 */
public class ConcatRuleBuilder<TYPE, ANNOTATION> {
    /**
     * ParserBuilder, im ConcatRuleBuilder wird dieser verwendet für die rule-Parser, um einen
     * Placeholder-Parser der Placeholder-Map hinzuzufügen, damit dieser später gebaut werden kann und bei
     * der end-Methode, damit die zugrundeliegende Rule dem ParserBuilder hinzugefügt werden kann.
     */
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    /**
     * RuleBuilder, im ConcatRuleBuilder wird dieser verwendet, um bei or() oder end() die subrule als neue
     * Klausel hinzuzufügen.
     */
    private final RuleBuilder<TYPE, ANNOTATION> ruleBuilder;

    /**
     * Der manyBuilder (dieser kann immer wieder verwendet werden und muss nicht immer neu erzeugt werden
     * oder zurückgesetzt werden)
     */
    private final ManyBuilder<TYPE, ANNOTATION> manyBuilder;
    /**
     * Der someBuilder (dieser kann immer wieder verwendet werden und muss nicht immer neu erzeugt werden,
     * denn er kann einfach zurückgesetzt werden)
     */
    private final SomeBuilder<TYPE, ANNOTATION> someBuilder;

    /**
     * Subrule (ConcatParser der einzelnen RegEx-Parser. Dieser wird später als Klausel dem RuleBuilder hinzugefügt.)
     */
    private ConcatParser<TYPE, ANNOTATION> subrule;
    /**
     * Gibt an, ob der ConcatRuleBuilder noch weitere Methodenaufrufe erlaubt
     */
    private boolean frozen;

    public ConcatRuleBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder, RuleBuilder<TYPE, ANNOTATION> ruleBuilder) {
        if (parserBuilder == null || ruleBuilder == null)
            throw new RuntimeException("Parser-Builder und Rule-Builder dürfen nicht null sein");
        this.parserBuilder = parserBuilder;
        this.ruleBuilder = ruleBuilder;
        this.subrule = null;
        this.manyBuilder = new ManyBuilder<>(parserBuilder, this);
        this.someBuilder = new SomeBuilder<>(parserBuilder, this);
        this.frozen = true;
    }

    /**
     * Erzeugt eine neue Subrule (ConcatParser) mit der übergebenen atSuccess-Methode
     * @param atSuccess Wird beim Erfolg des ConcatParsers aufgerufen
     */
    void newSubrule(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess) {
        if (frozen) {
            this.subrule = new ConcatParser<>(atSuccess);
            frozen = false;
        }
    }

    /**
     * Erzeugt eine neue Subrule (ConcatParser) mithilfe der Parser.basicConcatAtSuccess Methode.
     * @param type Typ, zu dem die Concatenation zusammengefasst wird
     */
    void newSubrule(TYPE type) {
        newSubrule(Parser.basicConcatAtSuccess(type));
    }

    /**
     * Fügt der aktuellen Subrule als neuen Schritt einen Hide-Parser ein.
     * @param pattern Pattern
     * @return Der zugrundeliegende ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> match(Pattern pattern) {
        return addStep(Parser.hide(pattern));
    }

    /**
     * Fügt der aktuellen Subrule als neuen Schritt einen Hide-Parser ein.
     * @param regex RegEx
     * @return Der zugrundeliegende ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> match(String regex) {
        return match(parserBuilder.getPattern(regex));
    }

    /**
     * Fügt der aktuellen Subrule als neuen Schritt einen Placeholder-Parser, der die Regel mit dem übergebenen
     * Namen abbilden wird, ein.
     * @param name Name der Rule
     * @return Der zugrundeliegende ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> rule(String name) {
        return addStep(parserBuilder.getPlaceholder(name));
    }

    /**
     * Fügt der aktuellen Subrule als neuen Schritt einen Many-Parser, der die Regel mit dem übergebenen
     * Namen abbilden wird, ein.
     * @param name Name der Rule
     * @return Der zugrundeliegende ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> many(String name) {
        return addStep(parserBuilder.getMany(null, name));
    }

    /**
     * Erzeugt einen neuen ManyBuilder, der dann an die Subrule angehängt wird
     * @return Ein neuer ManyBuilder
     */
    public ManyBuilder<TYPE, ANNOTATION> many() {
        manyBuilder.newManyRule();
        return manyBuilder;
    }

    /**
     * Fügt der aktuellen Subrule als neuen Schritt zuerst die Rule mit dem übergebenen Namen ein und dann
     * einen Many-Parser, der die Regel mit dem übergebenen Namen abbilden wird, ein.
     * @param name Name der Rule
     * @return Der zugrundeliegende ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> some(String name) {
        this.rule(name);
        return this.many(name);
    }

    /**
     * Erzeugt einen neuen Some, der dann an die Subrule angehängt wird
     * @return Ein neuer SomeBuilder
     */
    public SomeBuilder<TYPE, ANNOTATION> some() {
        someBuilder.newSomeRule();
        return someBuilder;
    }

    /**
     * Fügt der zugrundeliegenden Rule die aktuelle Subrule als Klausel ein und gibt den RuleBuilder wieder zurück.
     * Die or-Methode pausiert diesen ConcatRuleBuilder, sodass andere Methodenaufrufe als newSubrule keine
     * Auswirkungen auf diesen Builder haben.
     * @return Der zugrundeliegenden RuleBuilder.
     */
    public RuleBuilder<TYPE, ANNOTATION> or() {
        frozen = true;
        ruleBuilder.addClause(subrule);
        return ruleBuilder;
    }

    /**
     * Beendet diesen ConcatRuleBuilder und ebenso den zugrundeliegenden RuleBuilder. Die entstandene Rule
     * wird dann dem ParserBuilder hinzugefügt.
     */
    public void end() {
        if (!subrule.isEmpty()) {
            ruleBuilder.addClause(subrule);
            subrule = null;
        }
        frozen = true;
        parserBuilder.addParser(ruleBuilder.getName(), ruleBuilder.freeze());
    }

    /**
     * Fügt der aktuellen Subrule einen neuen Schritt ein, falls dieser ConcatRuleBuilder nicht pausiert ist.
     * @param parser Parser
     * @return Der zugrundeliegende ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> addStep(Parser<TYPE, ANNOTATION> parser) {
        if (!frozen && subrule != null) subrule.addSubparser(parser);
        return this;
    }
}
