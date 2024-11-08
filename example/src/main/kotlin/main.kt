import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.data.EntityState
import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap

interface OccupancySensor : TypedEntity

val EntityState<OccupancySensor>.occupancy get() = getAttribute<Boolean>("occupancy")

val service = ConcurrentHashMap<String, String>()

fun main() {

    // setup connection
    val cli = HaClient(
        host = "ha.local", port = 80, path = "/api/websocket", token = File("/tmp/token.txt").readText().trim()
    )



    runBlocking {
        CoroutineScope(Dispatchers.Default).launch {
            cli.start()
        }

        delay(1000)
        val automaticLight = AutomaticLight(cli)

        // watch & print changed attributes
        cli.changeListener += { haClient, stateChanged ->
            automaticLight.stateChanged(stateChanged)
        }


        while (true) {
            delay(1000)
        }


    }
}

