package rule

@DslMarker
annotation class RuleMarker

class Rule<T> constructor(
        val id: String = "",
        val name: String?,
        var description: String?,
        val condition: Predicate<T>,
        val action: (T) -> Unit
) {

    fun fire(t: T): Boolean {
        if (condition(t)) {
            action(t)
            return true
        }
        return false
    }

    @RuleMarker
    class Builder<T>(val id: String) {
        var name: String? = null
        var description: String? = null
        var condition: Predicate<T> = Condition<T>{false}
        var action: (T) -> Unit = {} //made change here so that action has access to the what we passed in as predicate

        fun setName(name: String?) = apply { this.name = name }
        fun setDescription(description: String?) = apply { this.description = description }
        fun setCondition(block: Predicate<T>) = apply { this.condition = block}
        fun setAction(block: (T) -> Unit) = apply { this.action = block }
        fun build(): Rule<T> {
            return Rule(id, name, description, condition, action)
        }
    }
}

fun <T> rule(id: String, fn: Rule.Builder<T>.() -> Unit): Rule<T> {
    return Rule.Builder<T>(id).apply(fn).build()
}
