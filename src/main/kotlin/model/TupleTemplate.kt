package com.github.enteraname74.model

import kotlin.reflect.KClass

/**
 * Base of a tuple.
 * It is primarily represented by a name and the type linked to it
 */
sealed class TupleTemplate(
    val name: String,
    val type: KClass<out Any>
) {
    /**
     * Check if the name and the type is the same as the compared object.
     */
    fun isSame(other: Any?): Boolean {
        if (other == this) return true

        if (other !is TupleTemplate) return false

        val sameName = (name == other.name) || (name.isEmpty() || other.name.isEmpty())
        val sameType = type == other.type

        val sameElement = sameType && sameName

        return sameElement
    }

    override fun toString(): String {
        return "TupleTemplate(name = $name, type = $type)"
    }
}

/**
 * Tuple used for read operation that return a variable.
 */
class ReadVariableTupleTemplate(
    name: String,
    val variableName: String,
    type: KClass<out Any>
): TupleTemplate(
    name = name,
    type = type
) {
    override fun toString(): String {
        return "ReadVariableTupleTemplate(name = $name, variableName = $variableName, type = $type)"
    }
}

/**
 * Tuple used for simple read operation.
 */
class ReadSimpleTupleTemplate(
    name: String,
    type: KClass<out Any>
): TupleTemplate(
    name = name,
    type = type
) {
    override fun toString(): String {
        return "ReadSimpleTupleTemplate(name = $name, type = $type)"
    }
}

/**
 * Tuple used to store a value.
 */
class ValueTupleTemplate(
    name: String,
    val value: Any,
    type: KClass<out Any>
): TupleTemplate(
    name = name,
    type = type
) {
    override fun toString(): String {
        return "ValueTupleTemplate(name = $name, value = $value, type = $type)"
    }
}


/**
 * Utility method for creating a ReadVariableTupleTemplate.
 */
fun r(name: String, variableName: String, type: KClass<out Any>) = ReadVariableTupleTemplate(
    name = name,
    variableName = variableName,
    type = type
)

/**
 * Utility method for creating a ReadVariableTupleTemplate.
 */
fun r(variableName: String, type: KClass<out Any>) = ReadVariableTupleTemplate(
    name = "",
    variableName = variableName,
    type = type
)

/**
 * Utility method for creating a ReadSimpleTupleTemplate.
 */
fun s(name: String, type: KClass<out Any>) = ReadSimpleTupleTemplate(
    name = name,
    type = type
)

/**
 * Utility method for creating a ValueTupleTemplate.
 */
fun v(name: String, value: Any, type: KClass<out Any>) = ValueTupleTemplate(
    name = name,
    value = value,
    type = type
)

/**
 * Utility method for creating a ValueTupleTemplate.
 */
fun v(value: Any, type: KClass<out Any>) = ValueTupleTemplate(
    name = "",
    value = value,
    type = type
)