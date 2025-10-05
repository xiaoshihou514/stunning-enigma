package main

sealed interface Stmt {
    class Block(vararg val stmt: Stmt) : Stmt {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Block

            return stmt.contentEquals(other.stmt)
        }

        override fun hashCode(): Int {
            return stmt.contentHashCode()
        }
    }

    data class Assign(val variable: Expr.Var, val value: Expr) : Stmt
    data class If(
        val cond: Expr,
        val thenStmt: Stmt,
        val elseStmt: Stmt? = null
    ) : Stmt
    data class Return(val result: Expr) : Stmt
}

sealed interface Expr {
    data class Const(val value: Int) : Expr
    data class Var(val name: String) : Expr
    data class Eq(val left: Expr, val right: Expr) : Expr
    data class NEq(val left: Expr, val right: Expr) : Expr
    data class Lt(val left: Expr, val right: Expr) : Expr

    data class Plus(val left: Expr, val right: Expr) : Expr
    data class Minus(val left: Expr, val right: Expr) : Expr
    data class Mul(val left: Expr, val right: Expr) : Expr
}