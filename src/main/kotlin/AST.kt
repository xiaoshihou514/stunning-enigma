package main

sealed interface Stmt {
    fun pretty(): String

    // can't be a data class cause of vararg
    class Block(vararg val stmt: Stmt) : Stmt {
        override fun pretty(): String = stmt.joinToString("\n") { it.pretty() }

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

    data class Assign(val variable: Expr.Var, val value: Expr) : Stmt {
        override fun pretty(): String = "${variable.pretty()} = ${value.pretty()}"
    }

    data class If(
        val cond: Expr,
        val thenStmt: Stmt,
        val elseStmt: Stmt? = null
    ) : Stmt {
        override fun pretty(): String =
            "if ${cond.pretty()} then ${thenStmt.pretty()} else ${elseStmt?.pretty() ?: "pass"}"
    }

    data class Return(val result: Expr) : Stmt {
        override fun pretty(): String  = "return ${result.pretty()}"
    }

    data class While(val cond: Expr, val body: Stmt) : Stmt {
        override fun pretty(): String =
            "while ${cond.pretty()} { ${body.pretty()} }"
    }
}

sealed interface Expr {
    fun pretty(): String

    data class Const(val value: Int) : Expr {
        override fun pretty(): String = value.toString()
    }

    data class Var(val name: String) : Expr {
        override fun pretty(): String = name
    }

    data class Eq(val left: Expr, val right: Expr) : Expr {
        override fun pretty(): String = "(${left.pretty()} = ${right.pretty()})"
    }

    data class NEq(val left: Expr, val right: Expr) : Expr {
        override fun pretty(): String = "(${left.pretty()} ≠ ${right.pretty()})"
    }

    data class Lt(val left: Expr, val right: Expr) : Expr {
        override fun pretty(): String = "(${left.pretty()} < ${right.pretty()})"
    }

    data class Plus(val left: Expr, val right: Expr) : Expr {
        override fun pretty(): String = "(${left.pretty()} + ${right.pretty()})"
    }

    data class Minus(val left: Expr, val right: Expr) : Expr {
        override fun pretty(): String = "(${left.pretty()} - ${right.pretty()})"
    }

    data class Mul(val left: Expr, val right: Expr) : Expr {
        override fun pretty(): String = "(${left.pretty()} * ${right.pretty()})"
    }
}
