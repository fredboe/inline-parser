package org.parser.tree;

import org.parser.Consumable;

import java.util.*;

/**
 * Abstract Syntax Tree
 * @param <TYPE> Type/Token
 */
public class AST<TYPE> {
    /**
     * The type of the current node
     */
    private final TYPE type;
    /**
     * The match object in the CharSequence (can be null).
     */
    private Consumable.Match match;
    /**
     * The child nodes
     */
    private List<AST<TYPE>> children;
    /**
     * Ignore bit. If the bit is set, this AST (with children) should be ignored.
     */
    private boolean ignore;

    public AST(TYPE type) {
        this.type = type;
        this.match = null;
        this.children = new ArrayList<>();
        this.ignore = false;
    }

    public AST(TYPE type, Consumable.Match match) {
        this(type);
        this.match = match;
    }

    public AST(TYPE type, Consumable.Match match, List<AST<TYPE>> children) {
        this(type, match);
        this.children = children;
    }

    /**
     * Adds a child to this node (another AST)
     * @param ast AST
     * @return Returns the AST on which the method was called
     */
    public AST<TYPE> addChild(AST<TYPE> ast) {
        children.add(ast);
        return this;
    }

    /**
     * Inserts multiple children to this node (another AST).
     * @param ASTs Collections from ASTs
     * @return Returns the AST on which the method was called
     */
    public AST<TYPE> addChildren(Collection<AST<TYPE>> ASTs) {
        children.addAll(ASTs);
        return this;
    }

    /**
     * Sets the ignore bit
     * @return Returns the AST on which the method was called
     */
    public AST<TYPE> setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    /**
     * Indicates whether this AST should be ignored.
     * @return Returns the ignore bit
     */
    public boolean shouldIgnore() {
        return ignore;
    }

    /**
     *
     * @return Returns the type/token, of the AST
     */
    public TYPE getType() {
        return type;
    }

    /**
     *
     * @return Returns the match as optional (empty if match is null)
     */
    public Optional<Consumable.Match> getMatch() {
        return Optional.ofNullable(match);
    }

    /**
     *
     * @return Returns the list of child nodes
     */
    public List<AST<TYPE>> getChildren() {
        return children;
    }

    /**
     *
     * @param i Index of the node
     * @return Returns the i-th child node
     * @throws IndexOutOfBoundsException, if the index is too big or too small
     */
    public AST<TYPE> getChild(int i) {
        return children.get(i);
    }

    /**
     *
     * @return Returns the number of children
     */
    public int numChildren(){
        return children.size();
    }

    public boolean isType(TYPE type) {
        return this.type == type;
    }

    /**
     *
     * @return Returns the AST as string
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        toStringRec(builder, "", "");
        return builder.toString();
    }

    /**
     * Converts the AST to a string by recursively expanding the builder from the children.
     * @param builder StringBuilder
     * @param prefix String prefix to be inserted before the current node.
     */
    public void toStringRec(StringBuilder builder, String prefix, String childrenPrefix) {
        builder.append(prefix);
        builder.append(getType());
        if (match != null) {
            builder.append(" ");
            builder.append(match.matched());
        }
        builder.append('\n');
        for (Iterator<AST<TYPE>> it = children.iterator(); it.hasNext();) {
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

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;

        if (obj instanceof AST ast) {
            return Objects.equals(type, ast.type) && Objects.equals(match, ast.match)
                    && Objects.equals(children, ast.getChildren());
        }
        return false;
    }
}
