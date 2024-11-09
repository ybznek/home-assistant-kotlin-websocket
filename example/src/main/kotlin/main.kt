import com.ybznek.ha.core.EntityId
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.data.EntityState
import com.ybznek.ha.core.dispatcher.StateChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

        cli.waitForStart()
        val automaticLight = AutomaticLight(cli)
        val printerControl = PrinterControl(cli)

        // watch & print changed attributes
        cli.changeListener += { haClient, stateChanged ->
            automaticLight.stateChanged(stateChanged)
            printerControl.stateChanged(stateChanged)
        }


        Channel<Unit>(1).receive()
    }
}

interface MeasuringSwitch : TypedEntity {}
class PrinterControl(private val cli: HaClient) {
    val id = EntityId<MeasuringSwitch>("switch.power_switch_printer")

    fun stateChanged(stateChanged: StateChanged<TypedEntity>) {
        if (stateChanged.entity == id) {
            println(stateChanged.changedAttributes)
        }
    }

}

