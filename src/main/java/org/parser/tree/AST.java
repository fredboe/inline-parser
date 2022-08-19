package org.parser.tree;

import org.parser.Consumable;

import java.util.*;

/**
 * Abstract Syntax Tree
 * @param <TYPE> Type/Token
 * @param <ANNOTATION> Annotation-Class
 */
public class AST<TYPE, ANNOTATION> {
    /**
     * Der Typ des aktuellen Knotens
     */
    private final TYPE type;
    /**
     * Das Match-Objekt in der CharSequence (kann null sein)
     */
    private Consumable.Match match;
    /**
     * Die Kind-Knoten
     */
    private List<AST<TYPE, ANNOTATION>> children;
    /**
     * Liste von Annotations, für diesen Knoten
     */
    private final List<ANNOTATION> annotations;
    /**
     * Ignore-Bit. Wenn das bit gesetzt ist, soll dieser AST (mit Kindern) ignoriert werden
     */
    private boolean ignore;

    public AST(TYPE type) {
        this.type = type;
        this.match = null;
        this.children = new ArrayList<>();
        this.annotations = new ArrayList<>();
        this.ignore = false;
    }

    public AST(TYPE type, Consumable.Match match) {
        this(type);
        this.match = match;
    }

    public AST(TYPE type, Consumable.Match match, List<AST<TYPE, ANNOTATION>> children) {
        this(type, match);
        this.children = children;
    }

    /**
     * Fügt diesem Knoten eine Annotation ein
     * @param annotation Annotation
     * @return Gibt den AST auf dem die Methode aufgerufen wurde zurück
     */
    public AST<TYPE, ANNOTATION> addAnnotation(ANNOTATION annotation) {
        annotations.add(annotation);
        return this;
    }

    /**
     * Fügt diesem Knoten ein Kind ein (einen anderen AST)
     * @param ast AST
     * @return Gibt den AST auf dem die Methode aufgerufen wurde zurück
     */
    public AST<TYPE, ANNOTATION> addChild(AST<TYPE, ANNOTATION> ast) {
        children.add(ast);
        return this;
    }

    /**
     * Fügt diesem Knoten mehrere Kinder ein (einen anderen AST)
     * @param ASTs Collections aus ASTs
     * @return Gibt den AST auf dem die Methode aufgerufen wurde zurück
     */
    public AST<TYPE, ANNOTATION> addChildren(Collection<AST<TYPE, ANNOTATION>> ASTs) {
        children.addAll(ASTs);
        return this;
    }

    /**
     * Setzt das Ignore-Bit
     * @return Gibt den AST auf dem die Methode aufgerufen wurde zurück
     */
    public AST<TYPE, ANNOTATION> setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    /**
     * Gibt an, ob dieser AST ignoriert werden sollte
     * @return Gibt das Ignore-Bit zurück
     */
    public boolean shouldIgnore() {
        return ignore;
    }

    /**
     *
     * @return Gibt den Typen/Token, des AST zurück
     */
    public TYPE getType() {
        return type;
    }

    /**
     *
     * @return Gibt das Match als Optional gewrappt zurück (empty falls Match null ist)
     */
    public Optional<Consumable.Match> getMatch() {
        return Optional.ofNullable(match);
    }

    /**
     *
     * @return Gibt die Liste der Kind-Knoten zurück
     */
    public List<AST<TYPE, ANNOTATION>> getChildren() {
        return children;
    }

    /**
     *
     * @param i Index des Knotens
     * @return Gibt den i-ten Kind-Knoten zurück
     * @throws IndexOutOfBoundsException, falls der Index zu groß bzw. zu klein ist
     */
    public AST<TYPE, ANNOTATION> getChild(int i) {
        return children.get(i);
    }

    /**
     *
     * @return Gibt die Anzahl an Kindern zurück
     */
    public int numChildren(){
        return children.size();
    }

    /**
     *
     * @return Gibt die Liste der Annotations zurück
     */
    public List<ANNOTATION> getAnnotations() {
        return annotations;
    }

    /**
     *
     * @return Gibt den AST als String zurück
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        toStringRec(builder, "", "");
        return builder.toString();
    }

    /**
     * Wandelt den AST in einen String um, indem der Builder rekursiv von den Kindern erweitert wird.
     * @param builder StringBuilder
     * @param prefix String-Prefix, der vor dem aktuellen Knoten eingefügt werden soll
     */
    public void toStringRec(StringBuilder builder, String prefix, String childrenPrefix) {
        builder.append(prefix);
        builder.append(getType());
        if (match != null) {
            builder.append(" ");
            builder.append(match.matched());
        }
        builder.append('\n');
        for (Iterator<AST<TYPE, ANNOTATION>> it = children.iterator(); it.hasNext();) {
            var next = it.next();
            if (next != null) {
                if (it.hasNext()) {
                    next.toStringRec(builder, childrenPrefix + "├── ", childrenPrefix + "│   ");
                } else {
                    next.toStringRec(builder, childrenPrefix + "└── ", childrenPrefix + "    ");
                }
            }
        }
    }
}
