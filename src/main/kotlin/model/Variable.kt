package com.github.enteraname74.model

/**
 * Represent a variable to use in read operations.
 */
data class Variable(
    val name: String,
    val value: Any,
) {
    val type = value::class

    override fun toString(): String {
        return "$value"
    }
}

/**
 *
 */
operator fun Variable?.compareTo(other: Variable): Int {
    if (this == null) return -1

    if (this.type != other.type) return -1

    return when(this.type) {
        integer -> (this.value as Int).compareTo((other.value as Int))
        string -> (this.value as String).compareTo((other.value as String))
        float -> (this.value as Float).compareTo((other.value as Float))
        else -> -1
    }
}
