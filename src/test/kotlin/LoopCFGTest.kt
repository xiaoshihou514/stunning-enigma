package test

import main.*
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class WhileLoopTests {

    @Test
    fun `simple while loop`() {
        val stmt = Stmt.While(
            Expr.Lt(Expr.Var("x"), Expr.Const(10)),
            Stmt.Assign(Expr.Var("x"), Expr.Plus(Expr.Var("x"), Expr.Const(1)))
        )
        val expected = Node.Label(
            0,
            Node.Condition(
                Expr.Lt(Expr.Var("x"), Expr.Const(10)),
                Node.Assign(
                    Expr.Var("x"),
                    Expr.Plus(Expr.Var("x"), Expr.Const(1)),
                    Node.Goto(0)
                ),
                Node.Quit
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `while loop with block body`() {
        val stmt = Stmt.While(
            Expr.Var("condition"),
            Stmt.Block(
                Stmt.Assign(Expr.Var("x"), Expr.Const(1)),
                Stmt.Assign(Expr.Var("y"), Expr.Const(2))
            )
        )
        val expected = Node.Label(
            0,
            Node.Condition(
                Expr.Var("condition"),
                Node.Assign(
                    Expr.Var("x"),
                    Expr.Const(1),
                    Node.Assign(
                        Expr.Var("y"),
                        Expr.Const(2),
                        Node.Goto(0)
                    )
                ),
                Node.Quit
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `while loop followed by statement`() {
        val stmt = Stmt.Block(
            Stmt.While(
                Expr.Lt(Expr.Var("i"), Expr.Const(5)),
                Stmt.Assign(Expr.Var("i"), Expr.Plus(Expr.Var("i"), Expr.Const(1)))
            ),
            Stmt.Assign(Expr.Var("done"), Expr.Const(1))
        )
        val expected = Node.Label(
            0,
            Node.Condition(
                Expr.Lt(Expr.Var("i"), Expr.Const(5)),
                Node.Assign(
                    Expr.Var("i"),
                    Expr.Plus(Expr.Var("i"), Expr.Const(1)),
                    Node.Goto(0)
                ),
                Node.Assign(Expr.Var("done"), Expr.Const(1), Node.Quit)
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `statement followed by while loop`() {
        val stmt = Stmt.Block(
            Stmt.Assign(Expr.Var("x"), Expr.Const(0)),
            Stmt.While(
                Expr.Lt(Expr.Var("x"), Expr.Const(10)),
                Stmt.Assign(Expr.Var("x"), Expr.Plus(Expr.Var("x"), Expr.Const(1)))
            )
        )
        val expected = Node.Assign(
            Expr.Var("x"),
            Expr.Const(0),
            Node.Label(
                0,
                Node.Condition(
                    Expr.Lt(Expr.Var("x"), Expr.Const(10)),
                    Node.Assign(
                        Expr.Var("x"),
                        Expr.Plus(Expr.Var("x"), Expr.Const(1)),
                        Node.Goto(0)
                    ),
                    Node.Quit
                )
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `while loop with return in body`() {
        val stmt = Stmt.While(
            Expr.Var("condition"),
            Stmt.Block(
                Stmt.If(
                    Expr.Var("found"),
                    Stmt.Return(Expr.Const(1))
                ),
                Stmt.Assign(Expr.Var("x"), Expr.Plus(Expr.Var("x"), Expr.Const(1)))
            )
        )
        val expected = Node.Label(
            0,
            Node.Condition(
                Expr.Var("condition"),
                Node.Condition(
                    Expr.Var("found"),
                    Node.Return(Expr.Const(1)),
                    Node.Assign(
                        Expr.Var("x"),
                        Expr.Plus(Expr.Var("x"), Expr.Const(1)),
                        Node.Goto(0)
                    )
                ),
                Node.Quit
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `nested while loops`() {
        val stmt = Stmt.While(
            Expr.Lt(Expr.Var("i"), Expr.Const(10)),
            Stmt.While(
                Expr.Lt(Expr.Var("j"), Expr.Const(5)),
                Stmt.Assign(Expr.Var("j"), Expr.Plus(Expr.Var("j"), Expr.Const(1)))
            )
        )
        val expected = Node.Label(
            0,
            Node.Condition(
                Expr.Lt(Expr.Var("i"), Expr.Const(10)),
                Node.Label(
                    1,
                    Node.Condition(
                        Expr.Lt(Expr.Var("j"), Expr.Const(5)),
                        Node.Assign(
                            Expr.Var("j"),
                            Expr.Plus(Expr.Var("j"), Expr.Const(1)),
                            Node.Goto(1)
                        ),
                        Node.Goto(0)
                    )
                ),
                Node.Quit
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `two sequential while loops`() {
        println("two sequential while loops")
        val stmt = Stmt.Block(
            Stmt.While(
                Expr.Lt(Expr.Var("x"), Expr.Const(5)),
                Stmt.Assign(Expr.Var("x"), Expr.Plus(Expr.Var("x"), Expr.Const(1)))
            ),
            Stmt.While(
                Expr.Lt(Expr.Var("y"), Expr.Const(3)),
                Stmt.Assign(Expr.Var("y"), Expr.Plus(Expr.Var("y"), Expr.Const(1)))
            )
        )
        val expected = Node.Label(
            1,
            Node.Condition(
                Expr.Lt(Expr.Var("x"), Expr.Const(5)),
                Node.Assign(
                    Expr.Var("x"),
                    Expr.Plus(Expr.Var("x"), Expr.Const(1)),
                    Node.Goto(1)
                ),
                Node.Label(
                    0,
                    Node.Condition(
                        Expr.Lt(Expr.Var("y"), Expr.Const(3)),
                        Node.Assign(
                            Expr.Var("y"),
                            Expr.Plus(Expr.Var("y"), Expr.Const(1)),
                            Node.Goto(0)
                        ),
                        Node.Quit
                    )
                )
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `while loop with if inside`() {
        val stmt = Stmt.While(
            Expr.Lt(Expr.Var("i"), Expr.Const(10)),
            Stmt.If(
                Expr.Eq(Expr.Var("i"), Expr.Const(5)),
                Stmt.Assign(Expr.Var("x"), Expr.Const(1)),
                Stmt.Assign(Expr.Var("x"), Expr.Const(0))
            )
        )
        val expected = Node.Label(
            0,
            Node.Condition(
                Expr.Lt(Expr.Var("i"), Expr.Const(10)),
                Node.Condition(
                    Expr.Eq(Expr.Var("i"), Expr.Const(5)),
                    Node.Assign(Expr.Var("x"), Expr.Const(1), Node.Goto(0)),
                    Node.Assign(Expr.Var("x"), Expr.Const(0), Node.Goto(0))
                ),
                Node.Quit
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `complex while loop scenario`() {
        val stmt = Stmt.Block(
            Stmt.Assign(Expr.Var("sum"), Expr.Const(0)),
            Stmt.Assign(Expr.Var("i"), Expr.Const(0)),
            Stmt.While(
                Expr.Lt(Expr.Var("i"), Expr.Const(10)),
                Stmt.Block(
                    Stmt.Assign(
                        Expr.Var("sum"),
                        Expr.Plus(Expr.Var("sum"), Expr.Var("i"))
                    ),
                    Stmt.Assign(
                        Expr.Var("i"),
                        Expr.Plus(Expr.Var("i"), Expr.Const(1))
                    )
                )
            ),
            Stmt.Return(Expr.Var("sum"))
        )
        val expected = Node.Assign(
            Expr.Var("sum"),
            Expr.Const(0),
            Node.Assign(
                Expr.Var("i"),
                Expr.Const(0),
                Node.Label(
                    0,
                    Node.Condition(
                        Expr.Lt(Expr.Var("i"), Expr.Const(10)),
                        Node.Assign(
                            Expr.Var("sum"),
                            Expr.Plus(Expr.Var("sum"), Expr.Var("i")),
                            Node.Assign(
                                Expr.Var("i"),
                                Expr.Plus(Expr.Var("i"), Expr.Const(1)),
                                Node.Goto(0)
                            )
                        ),
                        Node.Return(Expr.Var("sum"))
                    )
                )
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `while loop with empty body`() {
        val stmt = Stmt.While(
            Expr.Var("condition"),
            Stmt.Block()
        )
        val expected = Node.Label(
            0,
            Node.Condition(
                Expr.Var("condition"),
                Node.Goto(0),
                Node.Quit
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `triple nested while loops with different label counters`() {
        val stmt = Stmt.While(
            Expr.Var("a"),
            Stmt.While(
                Expr.Var("b"),
                Stmt.While(
                    Expr.Var("c"),
                    Stmt.Assign(Expr.Var("x"), Expr.Const(1))
                )
            )
        )
        val expected = Node.Label(
            0,
            Node.Condition(
                Expr.Var("a"),
                Node.Label(
                    1,
                    Node.Condition(
                        Expr.Var("b"),
                        Node.Label(
                            2,
                            Node.Condition(
                                Expr.Var("c"),
                                Node.Assign(Expr.Var("x"), Expr.Const(1), Node.Goto(2)),
                                Node.Goto(1)
                            )
                        ),
                        Node.Goto(0)
                    )
                ),
                Node.Quit
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `while with if containing return`() {
        val stmt = Stmt.Block(
            Stmt.While(
                Expr.Lt(Expr.Var("x"), Expr.Const(100)),
                Stmt.Block(
                    Stmt.If(
                        Expr.Eq(Expr.Var("x"), Expr.Const(42)),
                        Stmt.Return(Expr.Var("x"))
                    ),
                    Stmt.Assign(Expr.Var("x"), Expr.Plus(Expr.Var("x"), Expr.Const(1)))
                )
            ),
            Stmt.Return(Expr.Const(-1))
        )
        val expected = Node.Label(
            0,
            Node.Condition(
                Expr.Lt(Expr.Var("x"), Expr.Const(100)),
                Node.Condition(
                    Expr.Eq(Expr.Var("x"), Expr.Const(42)),
                    Node.Return(Expr.Var("x")),
                    Node.Assign(
                        Expr.Var("x"),
                        Expr.Plus(Expr.Var("x"), Expr.Const(1)),
                        Node.Goto(0)
                    )
                ),
                Node.Return(Expr.Const(-1))
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }
}