package main

fun Node.copy(next: Node): Node = when (this) {
    is Node.Assign -> Node.Assign(this.variable, this.value, this.next.copy(next))
    is Node.Condition -> Node.Condition(this.cond, this.nextIfTrue.copy(next), this.nextIfFalse.copy(next))
    Node.Quit -> next
    is Node.Return -> this
}

fun Stmt.produceCFG(): Node = when (this) {
    is Stmt.Assign -> Node.Assign(this.variable, this.value, Node.Quit)
    is Stmt.If -> Node.Condition(
        this.cond, this.thenStmt.produceCFG(), this.elseStmt?.produceCFG() ?: Node.Quit
    )
    is Stmt.Return -> Node.Return(this.result)
    is Stmt.Block -> this.stmt.foldRight<_, Node>(Node.Quit, { stmt, acc ->
        // copy is constant time since only the last element is modified
        stmt.produceCFG().copy(next = acc)
    })
}

fun main() {
    println("Hello World!")
}