import com.ybznek.ha.core.EntityId
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.entitytypes.light.*

suspend fun EntityId<LidlLight>.turnOnLidl(cli: HaClient, color: RgbColor) {
    turnOn(cli, color)
    turnOn(cli, brightness = Brightness(255))
}

suspend fun EntityId<LidlLight>.turnOffLidl(cli: HaClient) {
    turnOn(cli, RgbColor(0, 0, 0), Brightness(0))
    turnOff(cli)
}

