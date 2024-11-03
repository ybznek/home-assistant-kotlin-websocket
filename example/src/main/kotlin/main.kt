import com.ybznek.ha.core.EntityId
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.HassServiceTarget
import com.ybznek.ha.entitytypes.light.Light
import com.ybznek.ha.entitytypes.light.isOn
import com.ybznek.ha.entitytypes.light.turnOn
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

        /*if (stateChanged.entity.startsWith("sensor.") && stateChanged.oldState != null) {
            val changedAttributes = stateChanged.changedAttributes
            if (changedAttributes.isNotEmpty()) {
                println(stateChanged.entity)
                for ((k, v) in changedAttributes) {
                    println(" $k    $v")
                }
                println()
            }
        }*/
    }

    runBlocking {
        val entity = EntityId<Light>("light.workroom_1")

        cli.start()
        delay(2000)
        println(cli.getUser().parsed.result)
        println(cli.version)

        while (true) {
            val lightRoom = cli.states[entity] ?: error("not found")
            val curentState = lightRoom.state
            println(curentState.attributes)
            delay(1000)
        }

        // read light specific attribute
            entity.turnOn(cli, HassServiceTarget.entity(entity))
    }
}

