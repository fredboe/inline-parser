package org.parser.tree;

import org.parser.Consumable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class AST<TYPE, ANNOTATION> {
    private final TYPE type;
    private Consumable.Match match;
    private List<AST<TYPE, ANNOTATION>> children;
    private final List<ANNOTATION> annotations;
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

    public AST<TYPE, ANNOTATION> addAnnotation(ANNOTATION annotation) {
        annotations.add(annotation);
        return this;
    }

    public AST<TYPE, ANNOTATION> addChild(AST<TYPE, ANNOTATION> ast) {
        children.add(ast);
        return this;
    }

    public AST<TYPE, ANNOTATION> ignore() {
        this.ignore = true;
        return this;
    }

    public AST<TYPE, ANNOTATION> unignore() {
        this.ignore = false;
        return this;
    }

    public boolean shouldIgnore() {
        return ignore;
    }

    public TYPE getType() {
        return type;
    }

    public Optional<Consumable.Match> getMatch() {
        return match != null ? Optional.of(match) : Optional.empty();
    }

    public List<AST<TYPE, ANNOTATION>> getChildren() {
        return children;
    }

    public AST<TYPE, ANNOTATION> getChild(int i) {
        return children.get(i);
    }

    public int numChildren(){
        return children.size();
    }

    public List<ANNOTATION> getAnnotations() {
        return annotations;
    }
}
