package com.ybznek.ha.core.dispatcher

import com.ybznek.ha.core.AttributeName
import com.ybznek.ha.core.EntityIdString
import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.data.EntityState
import com.ybznek.ha.core.result.Msg
import kotlinx.datetime.Instant

data class StateChanged<T : TypedEntity>(
    val entity: EntityIdString,
    val time: Instant,
    val raw: Msg<*>,
    val oldState: EntityState<T>?,
    val newState: EntityState<T>
) {
    val changedAttributes: Map<AttributeName, ValueChange<*>> by lazy {
        calcAttributeChange()
    }

    private fun calcAttributeChange(): Map<AttributeName, ValueChange<*>> {
        val oldAttributes = oldState?.attributes
        val newAttributes = newState.attributes

        if (oldAttributes == null) {
            return newAttributes.mapValuesTo(HashMap(newAttributes.size)) { (_, value) ->
                ValueChange(null, value)
            }
        }

        val result = HashMap<String, ValueChange<Any?>>()
        for ((k, v) in newAttributes) {
            val oldValue = oldAttributes[k]
            if (v != oldValue) {
                result[k] = ValueChange(oldValue, v)
            }
        }
        return result.ifEmpty { emptyMap() }
    }

    fun isChanged(name: AttributeName): Boolean {
        val old = oldState?.attributes?.get(name)
        val new = newState.attributes[name]
        return old != new
    }
}