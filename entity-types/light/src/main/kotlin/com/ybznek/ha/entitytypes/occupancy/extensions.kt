package com.ybznek.ha.entitytypes.occupancy

import com.ybznek.ha.core.data.EntityState

val EntityState<OccupancySensor>.occupancy get() = getAttribute<Boolean>("occupancy")