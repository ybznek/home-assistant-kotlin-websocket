import com.ybznek.ha.core.HaClient
import com.ybznek.ha.entitytypes.light.Light
import com.ybznek.ha.entitytypes.light.isOn
import com.ybznek.ha.entitytypes.light.turnOn
import com.ybznek.ha.typed.EntityId
import com.ybznek.ha.typed.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {

    // setup connection
    val cli = HaClient(
        host = "ha.local",
        port = 80,
        path = "/api/websocket",
        token = File("/tmp/token.txt").readText().trim()
    )

    // watch & print changed attributes
    cli.changeListener += { haClient, stateChanged ->
        if (stateChanged.entity.startsWith("sensor.") && stateChanged.oldState != null) {
            val changedAttributes = stateChanged.changedAttributes
            if (changedAttributes.isNotEmpty()) {
                println(stateChanged.entity)
                for ((k, v) in changedAttributes) {
                    println(" $k    $v")
                }
                println()
            }
        }
    }

    runBlocking {
        val entity = EntityId<Light>("light.workroom_1")

        launch { cli.start() }
        delay(2000)

        val lightRoom = cli.states[entity] ?: error("not found")
        val curentState = lightRoom.state

        // read light specific attribute
        if (!curentState.isOn) {
            entity.turnOn(cli)
        }
    }
}

