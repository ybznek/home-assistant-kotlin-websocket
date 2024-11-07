import com.ybznek.ha.core.EntityId
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.data.EntityState
import com.ybznek.ha.entitytypes.light.Light
import com.ybznek.ha.entitytypes.light.turnOff
import com.ybznek.ha.entitytypes.light.turnOn
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import java.io.File

interface OccupancySensor : TypedEntity

val EntityState<OccupancySensor>.occupancy get() = getAttribute<Boolean>("occupancy")


fun main() {

    // setup connection
    val cli = HaClient(
        host = "ha.local", port = 80, path = "/api/websocket", token = File("/tmp/token.txt").readText().trim()
    )

    val entity = EntityId<Light>("light.workroom_1")

    suspend fun occupancyChanged(id: String, from: Boolean?, to: Boolean) {
        println("OCCUPANCY ${Clock.System.now()}: $id $from $to")
        if (to) {
            println(entity.turnOn(cli))
        } else {
            println(entity.turnOff(cli))
        }
        println("returned")
    }

    // watch & print changed attributes
    cli.changeListener += { haClient, stateChanged ->
        if (stateChanged.entity == "sensor.occupancy_sensor_battery") {
            val typedChange = stateChanged.typed<OccupancySensor>()
            val old = typedChange.oldState?.occupancy
            val new = typedChange.newState.occupancy
            if (old != new) {
                occupancyChanged(stateChanged.entity, old, new)
            }
        } else if ("workroom_1" in stateChanged.entity) {
            println(stateChanged)
        }
    }

    runBlocking {


        CoroutineScope(Dispatchers.Default).launch {
            cli.start()
        }
        delay(2000)
        println(cli.getConfig().parsed.result)
        println(cli.getUser().parsed.result)
        val services = cli.getServices()
        val message = services.parsed.result
        println(message)
        println(services)
        println(cli.version)

        delay(5000)
        // read light specific attribute
        entity.turnOn(cli)

        while (true) {
            delay(1000)
        }


    }
}

