package org.checkerframework.checker.dividebyzero;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.source.Result;

import javax.lang.model.type.TypeKind;
import java.lang.annotation.Annotation;
import com.sun.source.tree.*;

import java.util.Set;
import java.util.EnumSet;

import org.checkerframework.checker.dividebyzero.qual.*;

public class DivByZeroVisitor extends BaseTypeVisitor<DivByZeroAnnotatedTypeFactory> {

    /** Set of operators we care about */
    private static final Set<Tree.Kind> DIVISION_OPERATORS = EnumSet.of(
        /* x /  y */ Tree.Kind.DIVIDE,
        /* x /= y */ Tree.Kind.DIVIDE_ASSIGNMENT,
        /* x %  y */ Tree.Kind.REMAINDER,
        /* x %= y */ Tree.Kind.REMAINDER_ASSIGNMENT);

    /**
     * Determine whether to report an error at the given binary AST node.
     * The error text is defined in the messages.properties file.
     * @param node the AST node to inspect
     * @return true if an error should be reported, false otherwise
     */
    private boolean errorAt(BinaryTree node) {
        // A BinaryTree represents a binary operator, like + or -.
        if (node.getKind() == Tree.Kind.DIVIDE || node.getKind() == Tree.Kind.REMAINDER) {
            if(hasAnnotation(node.getRightOperand(), Zero.class)) {
                return true;
            }

            if(hasAnnotation(node.getRightOperand(), Top.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether to report an error at the given compound assignment
     * AST node. The error text is defined in the messages.properties file.
     * @param node the AST node to inspect
     * @return true if an error should be reported, false otherwise
     */
    private boolean errorAt(CompoundAssignmentTree node) {
        // A CompoundAssignmentTree represents a binary operator plus assignment,
        // like "x += 10".
        if (node.getKind() == Tree.Kind.DIVIDE_ASSIGNMENT || node.getKind() == Tree.Kind.REMAINDER_ASSIGNMENT) {
            if(hasAnnotation(node.getExpression(), Zero.class)) {
                return true;
            }

            if(hasAnnotation(node.getExpression(), Top.class)) {
                return true;
            }
        }
        return false;
    }

    // ========================================================================
    // Useful helpers

    private static final Set<TypeKind> INT_TYPES = EnumSet.of(
        TypeKind.INT,
        TypeKind.LONG);

    private boolean isInt(Tree node) {
        return INT_TYPES.contains(atypeFactory.getAnnotatedType(node).getKind());
    }

    private boolean hasAnnotation(Tree node, Class<? extends Annotation> c) {
        return atypeFactory.getAnnotatedType(node).hasAnnotation(c);
    }

    // ========================================================================
    // Checker Framework plumbing

    public DivByZeroVisitor(BaseTypeChecker c) {
        super(c);
    }

    @Override
    public Void visitBinary(BinaryTree node, Void p) {
        if (isInt(node)) {
            if (errorAt(node)) {
                checker.report(Result.failure("divide.by.zero"), node);
            }
        }
        return super.visitBinary(node, p);
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree node, Void p) {
        if (isInt(node.getExpression())) {
            if (errorAt(node)) {
                checker.report(Result.failure("divide.by.zero"), node);
            }
        }
        return super.visitCompoundAssignment(node, p);
    }

}
