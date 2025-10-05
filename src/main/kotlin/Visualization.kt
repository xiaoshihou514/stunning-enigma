package main

private fun Node.vizName(): String = when (this) {
    is Node.Condition -> "${this.hashCode()}: if ${this.cond.pretty()}"
    is Node.Goto -> "LOOP ${this.label} end"
    is Node.Assign -> "${this.hashCode()}: ${this.variable.pretty()} = ${this.value.pretty()}"
    is Node.Label -> "LOOP ${this.label} start"
    Node.Quit -> "${this.hashCode()}: pass"
    is Node.Return -> "${this.hashCode()}: return ${this.result.pretty()}"
}

private fun link(from: String, to: String, label: String? = null) =
    if (label == null)
        "\t\"$from\" -> \"$to\"\n"
    else "\t\"$from\" -> \"$to\" [label=\"$label\"]\n"

private fun Node.emitGraphViz(sb: StringBuilder, id: Int) {
    val name = this.vizName()
    when (this) {
        is Node.Condition -> {
            sb.append(link(name, this.nextIfTrue.vizName(), "T"))
            this.nextIfTrue.emitGraphViz(sb, id + 1)

            if (this.nextIfFalse !is Node.Quit) {
                sb.append(link(name, this.nextIfFalse.vizName(), "F"))
                this.nextIfFalse.emitGraphViz(sb, id + 1)
            }
        }

        is Node.Goto ->
            sb.append(link(name, Node.Label(this.label, Node.Quit).vizName()))

        is Node.Assign -> {
            sb.append(link(name, this.next.vizName()))
            this.next.emitGraphViz(sb, id + 1)
        }

        is Node.Label -> {
            sb.append(link(name, this.next.vizName()))
            this.next.emitGraphViz(sb, id + 1)
        }

        else -> {}
    }
}

fun Node.emitGraphViz(): String {
    val sb = StringBuilder()
    sb.append("digraph {\n")
    this.emitGraphViz(sb, 0)
    sb.append("}")
    return sb.toString()
        .split("\n")
        .distinct()
        .joinToString("\n")
}