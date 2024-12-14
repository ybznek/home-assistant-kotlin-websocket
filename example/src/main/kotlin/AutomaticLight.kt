import com.ybznek.ha.core.EntityId
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.dispatcher.StateChanged
import com.ybznek.ha.entitytypes.light.Light
import com.ybznek.ha.entitytypes.light.RgbColor
import com.ybznek.ha.entitytypes.light.turnOff
import com.ybznek.ha.entitytypes.light.turnOn
import com.ybznek.ha.entitytypes.occupancy.OccupancySensor
import com.ybznek.ha.entitytypes.occupancy.occupancy
import kotlinx.coroutines.runBlocking

class AutomaticLight(val client: HaClient) {
    object Lights {
        val groups = EntityIds.workroomLights
    }

    object Sensor {
        val occupancyId = EntityId<OccupancySensor>("sensor.occupancy_sensor_battery")
    }

    val lightState = client.getState(Lights.groups)

    var sensorContext: String? = null

    suspend fun stateChanged(stateChanged: StateChanged<TypedEntity>) {
        when (stateChanged.entity) {
            Sensor.occupancyId -> {
                val typedChange = stateChanged.typed<OccupancySensor>()
                val old = typedChange.oldState?.occupancy
                val new = typedChange.newState.occupancy
                if (old != new) {
                    occupancyChanged(new)
                }
            }

            Lights.groups -> {
                val typedChange = stateChanged.typed<Light>()
                println(typedChange.newState.context)
            }
        }
    }

    suspend fun setLight(target: Boolean) {
        sensorContext = Lights.groups
            .run {
                when (target) {
                    true -> turnOn(client, rgbColor = RgbColor(r = 255))
                    false -> turnOff(client)
                }
            }.parsed.result?.context?.id
    }

    init {
        runBlocking { setLight(true) }
    }
    private suspend fun occupancyChanged(new: Boolean) {
        if (new) {
            setLight(true)
        } else {
            if (lightState.state.context.id == sensorContext) {
                setLight(false)
            }
        }
    }
}