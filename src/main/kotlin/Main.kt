package main

fun Node.then(next: Node): Node = when (this) {
    is Node.Assign -> Node.Assign(this.variable, this.value, this.next.then(next))
    is Node.Condition -> Node.Condition(this.cond, this.nextIfTrue.then(next), this.nextIfFalse.then(next))
    Node.Quit -> next
    is Node.Return -> this
    is Node.Label -> Node.Label(this.label, this.next.then(next))
    is Node.Goto -> this
}

private fun Stmt.produceCFG(counter: Int): Pair<Node, Int> = when (this) {
    is Stmt.Assign -> Pair(Node.Assign(this.variable, this.value, Node.Quit), counter)
    is Stmt.If -> Pair(
        Node.Condition(
            this.cond,
            this.thenStmt.produceCFG(counter).first,
            this.elseStmt?.produceCFG(counter)?.first ?: Node.Quit
        ), counter
    )

    is Stmt.Return -> Pair(Node.Return(this.result), counter)
    is Stmt.Block -> Pair(this.stmt.foldRight<_, Pair<Node, Int>>(Pair(Node.Quit, counter)) { stmt, acc ->
        // then is constant time since only the last element is modified
        val (next, c) = acc
        val (curr, newCounter) = stmt.produceCFG(c)
        Pair(curr.then(next), newCounter)
    }.first, counter)

    is Stmt.While -> {
        val (body, newCounter) = this.body.produceCFG(counter + 1)
        Pair(
            Node.Label(
                counter,
                Node.Condition(
                    this.cond, body.then(Node.Goto(counter)), Node.Quit
                )
            ),
            newCounter
        )
    }
}

fun Stmt.produceCFG(): Node = this.produceCFG(0).first

fun main() {
    println("Hello World!")
}