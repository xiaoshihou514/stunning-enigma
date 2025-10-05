package test

import main.*
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class ExtendedCFGTest {

    @Test
    fun `simple assignment`() {
        val stmt = Stmt.Assign(Expr.Var("x"), Expr.Const(10))
        val expected = Node.Assign(Expr.Var("x"), Expr.Const(10), Node.Quit)
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `simple return`() {
        val stmt = Stmt.Return(Expr.Var("x"))
        val expected = Node.Return(Expr.Var("x"))
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `sequential assignments`() {
        val stmt = Stmt.Block(
            Stmt.Assign(Expr.Var("x"), Expr.Const(1)),
            Stmt.Assign(Expr.Var("y"), Expr.Const(2)),
            Stmt.Assign(Expr.Var("z"), Expr.Const(3))
        )
        val expected = Node.Assign(
            Expr.Var("x"), Expr.Const(1),
            Node.Assign(
                Expr.Var("y"), Expr.Const(2),
                Node.Assign(Expr.Var("z"), Expr.Const(3), Node.Quit)
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `if without else`() {
        val stmt = Stmt.If(
            Expr.Eq(Expr.Var("x"), Expr.Const(0)),
            Stmt.Assign(Expr.Var("y"), Expr.Const(1))
        )
        val expected = Node.Condition(
            Expr.Eq(Expr.Var("x"), Expr.Const(0)),
            Node.Assign(Expr.Var("y"), Expr.Const(1), Node.Quit),
            Node.Quit
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `if with else`() {
        val stmt = Stmt.If(
            Expr.Lt(Expr.Var("x"), Expr.Const(10)),
            Stmt.Assign(Expr.Var("result"), Expr.Const(1)),
            Stmt.Assign(Expr.Var("result"), Expr.Const(0))
        )
        val expected = Node.Condition(
            Expr.Lt(Expr.Var("x"), Expr.Const(10)),
            Node.Assign(Expr.Var("result"), Expr.Const(1), Node.Quit),
            Node.Assign(Expr.Var("result"), Expr.Const(0), Node.Quit)
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `nested if statements`() {
        val stmt = Stmt.If(
            Expr.Var("a"),
            Stmt.If(
                Expr.Var("b"),
                Stmt.Assign(Expr.Var("x"), Expr.Const(1)),
                Stmt.Assign(Expr.Var("x"), Expr.Const(2))
            ),
            Stmt.Assign(Expr.Var("x"), Expr.Const(3))
        )
        val expected = Node.Condition(
            Expr.Var("a"),
            Node.Condition(
                Expr.Var("b"),
                Node.Assign(Expr.Var("x"), Expr.Const(1), Node.Quit),
                Node.Assign(Expr.Var("x"), Expr.Const(2), Node.Quit)
            ),
            Node.Assign(Expr.Var("x"), Expr.Const(3), Node.Quit)
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `block with return`() {
        val stmt = Stmt.Block(
            Stmt.Assign(Expr.Var("x"), Expr.Const(5)),
            Stmt.Return(Expr.Var("x"))
        )
        val expected = Node.Assign(
            Expr.Var("x"), Expr.Const(5),
            Node.Return(Expr.Var("x"))
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `if with block branches`() {
        val stmt = Stmt.If(
            Expr.NEq(Expr.Var("x"), Expr.Const(0)),
            Stmt.Block(
                Stmt.Assign(Expr.Var("y"), Expr.Const(1)),
                Stmt.Assign(Expr.Var("z"), Expr.Const(2))
            ),
            Stmt.Block(
                Stmt.Assign(Expr.Var("y"), Expr.Const(3)),
                Stmt.Assign(Expr.Var("z"), Expr.Const(4))
            )
        )
        val expected = Node.Condition(
            Expr.NEq(Expr.Var("x"), Expr.Const(0)),
            Node.Assign(
                Expr.Var("y"), Expr.Const(1),
                Node.Assign(Expr.Var("z"), Expr.Const(2), Node.Quit)
            ),
            Node.Assign(
                Expr.Var("y"), Expr.Const(3),
                Node.Assign(Expr.Var("z"), Expr.Const(4), Node.Quit)
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `complex expressions`() {
        val stmt = Stmt.Block(
            Stmt.Assign(
                Expr.Var("result"),
                Expr.Plus(
                    Expr.Minus(Expr.Var("a"), Expr.Var("b")),
                    Expr.Mul(Expr.Var("c"), Expr.Const(2))
                )
            ),
            Stmt.Return(Expr.Var("result"))
        )
        val expected = Node.Assign(
            Expr.Var("result"),
            Expr.Plus(
                Expr.Minus(Expr.Var("a"), Expr.Var("b")),
                Expr.Mul(Expr.Var("c"), Expr.Const(2))
            ),
            Node.Return(Expr.Var("result"))
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `empty block`() {
        val stmt = Stmt.Block()
        val expected = Node.Quit
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `if after if in block`() {
        val stmt = Stmt.Block(
            Stmt.If(
                Expr.Var("a"),
                Stmt.Assign(Expr.Var("x"), Expr.Const(1))
            ),
            Stmt.If(
                Expr.Var("b"),
                Stmt.Assign(Expr.Var("y"), Expr.Const(2))
            )
        )
        val expected = Node.Condition(
            Expr.Var("a"),
            Node.Assign(
                Expr.Var("x"), Expr.Const(1),
                Node.Condition(
                    Expr.Var("b"),
                    Node.Assign(Expr.Var("y"), Expr.Const(2), Node.Quit),
                    Node.Quit
                )
            ),
            Node.Condition(
                Expr.Var("b"),
                Node.Assign(Expr.Var("y"), Expr.Const(2), Node.Quit),
                Node.Quit
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `early return`() {
        val stmt = Stmt.Block(
            Stmt.Assign(Expr.Var("x"), Expr.Const(10)),
            Stmt.If(
                Expr.Lt(Expr.Var("x"), Expr.Const(5)),
                Stmt.Return(Expr.Const(0))
            ),
            Stmt.Return(Expr.Const(1))
        )
        val expected = Node.Assign(
            Expr.Var("x"), Expr.Const(10),
            Node.Condition(
                Expr.Lt(Expr.Var("x"), Expr.Const(5)),
                Node.Return(Expr.Const(0)),
                Node.Return(Expr.Const(1))
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `deeply nested blocks`() {
        val stmt = Stmt.Block(
            Stmt.Block(
                Stmt.Block(
                    Stmt.Assign(Expr.Var("x"), Expr.Const(1))
                ),
                Stmt.Assign(Expr.Var("y"), Expr.Const(2))
            ),
            Stmt.Assign(Expr.Var("z"), Expr.Const(3))
        )
        val expected = Node.Assign(
            Expr.Var("x"), Expr.Const(1),
            Node.Assign(
                Expr.Var("y"), Expr.Const(2),
                Node.Assign(Expr.Var("z"), Expr.Const(3), Node.Quit)
            )
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `all comparison operators`() {
        val stmt = Stmt.Block(
            Stmt.If(
                Expr.Eq(Expr.Var("a"), Expr.Const(1)),
                Stmt.Assign(Expr.Var("x"), Expr.Const(1))
            ),
            Stmt.If(
                Expr.NEq(Expr.Var("b"), Expr.Const(2)),
                Stmt.Assign(Expr.Var("y"), Expr.Const(2))
            ),
            Stmt.If(
                Expr.Lt(Expr.Var("c"), Expr.Const(3)),
                Stmt.Assign(Expr.Var("z"), Expr.Const(3))
            )
        )
        // Building the expected CFG...
        val thirdIf = Node.Condition(
            Expr.Lt(Expr.Var("c"), Expr.Const(3)),
            Node.Assign(Expr.Var("z"), Expr.Const(3), Node.Quit),
            Node.Quit
        )
        val secondIf = Node.Condition(
            Expr.NEq(Expr.Var("b"), Expr.Const(2)),
            Node.Assign(Expr.Var("y"), Expr.Const(2), thirdIf),
            thirdIf
        )
        val expected = Node.Condition(
            Expr.Eq(Expr.Var("a"), Expr.Const(1)),
            Node.Assign(Expr.Var("x"), Expr.Const(1), secondIf),
            secondIf
        )
        assertEquals(expected, stmt.produceCFG())
    }

    @Test
    fun `cfg generation`() {
        val example1: Stmt = Stmt.Block(
            Stmt.Assign(Expr.Var("x"), Expr.Const(0)), Stmt.If(
                Expr.Var("a"), Stmt.Assign(Expr.Var("y"), Expr.Const(5)), Stmt.Assign(Expr.Var("y"), Expr.Const(1))
            ), Stmt.Return(
                Expr.Plus(
                    Expr.Mul(Expr.Var("x"), Expr.Const(2)), Expr.Var("y")
                )
            )
        )

        val example1CFG = Node.Assign(
            Expr.Var("x"), Expr.Const(0), Node.Condition(
                Expr.Var("a"), Node.Assign(
                    Expr.Var("y"), Expr.Const(5), Node.Return(
                        Expr.Plus(
                            Expr.Mul(Expr.Var("x"), Expr.Const(2)), Expr.Var("y")
                        )
                    )
                ), Node.Assign(
                    Expr.Var("y"), Expr.Const(1), Node.Return(
                        Expr.Plus(
                            Expr.Mul(Expr.Var("x"), Expr.Const(2)), Expr.Var("y")
                        )
                    )
                )
            )
        )

        assertEquals(
            example1.produceCFG(), example1CFG
        )
    }
}
