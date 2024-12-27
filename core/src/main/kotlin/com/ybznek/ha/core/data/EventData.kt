package com.ybznek.ha.core.data

import com.ybznek.ha.core.EntityIdString

data class EventData(
    val entityId: EntityIdString,
    val oldState: State?,
    val newState: State?,
)