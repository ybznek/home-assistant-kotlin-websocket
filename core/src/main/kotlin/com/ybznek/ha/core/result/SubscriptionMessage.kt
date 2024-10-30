package com.ybznek.ha.core.result

import com.ybznek.ha.core.data.EventMessage

data class SubscriptionMessage(
    override val id: Int,
    override val type: String,
    val event: EventMessage
) : ServerMessage