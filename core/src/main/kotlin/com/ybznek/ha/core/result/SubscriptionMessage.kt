package com.ybznek.ha.core.result

import com.ybznek.ha.core.data.StateChangedEvent

data class SubscriptionMessage(
    override val id: Int,
    override val type: String,
    val event: StateChangedEvent
) : ServerMessage