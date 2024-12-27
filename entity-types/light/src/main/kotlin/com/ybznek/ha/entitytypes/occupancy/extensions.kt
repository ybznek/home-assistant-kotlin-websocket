package com.ybznek.ha.entitytypes.occupancy

import com.ybznek.ha.core.data.EntityState

@get:JvmName("occupancyIkea")
val EntityState<IkeaOccupancySensor>.occupancy get() = getAttribute<Boolean>("occupancy")

@get:JvmName("occupancyLidl")
val EntityState<LidlOccupancySensor>.occupancy get() = state == "on"