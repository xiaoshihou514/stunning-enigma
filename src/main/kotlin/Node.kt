package main

sealed interface Node {
    data class Assign(
        val variable: Expr.Var,
        val value: Expr,
        val next: Node
    ) : Node

    data class Return(val result: Expr) : Node

    data class Condition(
        val cond: Expr,
        val nextIfTrue: Node,
        val nextIfFalse: Node
    ) : Node

    data object Quit : Node

    data class Label(val label: Int, val next: Node) : Node
    data class Goto(val label: Int) : Node
}