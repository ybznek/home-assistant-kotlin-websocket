import EntityIds.bedSwitch
import EntityIds.doorSwitch
import EntityIds.extraIkeaSwitch
import EntityIds.hallLight
import EntityIds.hallOccupancySensor
import EntityIds.ikeaSwitchForLidl
import EntityIds.kitchenLight
import EntityIds.kitchenOccupancySensor
import EntityIds.lidlLamp
import EntityIds.workroomLights
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.dispatcher.StateChanged
import com.ybznek.ha.entitytypes.light.RgbColor.Companion.PURPLE
import com.ybznek.ha.entitytypes.light.RgbColor.Companion.RED
import com.ybznek.ha.entitytypes.light.RgbColor.Companion.WHITE
import com.ybznek.ha.entitytypes.light.turnOff
import com.ybznek.ha.entitytypes.light.turnOn
import com.ybznek.ha.entitytypes.occupancy.IkeaOccupancySensor
import com.ybznek.ha.entitytypes.occupancy.LidlOccupancySensor
import com.ybznek.ha.entitytypes.occupancy.occupancy
import com.ybznek.ha.entitytypes.switchtype.IkeaButtonState
import com.ybznek.ha.entitytypes.switchtype.IkeaButtonState.*
import com.ybznek.ha.entitytypes.switchtype.IkeaSwitch
import com.ybznek.ha.entitytypes.switchtype.ikeaSwitchState
import time.DayTimeUtil


val dayTimeUtil = DayTimeUtil()

suspend fun onChange(haClient: HaClient, stateChanged: StateChanged<TypedEntity>) {
    when (stateChanged.entity) {
        ikeaSwitchForLidl -> haClient.onLidlSwitchChanges(stateChanged.typed<IkeaSwitch>().newState.ikeaSwitchState)
        bedSwitch, extraIkeaSwitch, doorSwitch ->
            haClient.processBed(stateChanged.typed<IkeaSwitch>().newState.ikeaSwitchState)

        hallOccupancySensor -> haClient.processHall(stateChanged.typed())
        kitchenOccupancySensor -> haClient.processKitchen(stateChanged.typed())

    }
}

private suspend fun HaClient.processHall(typed: StateChanged<IkeaOccupancySensor>) {
    val old = typed.oldState?.occupancy
    val new = typed.newState?.occupancy == true
    if (new != old) {
        if (new) {
            hallLight.turnOn(this, colorTemp = (454 + 250) / 2, transition = 0)
        } else {
            hallLight.turnOff(this)
        }
    }
}


private suspend fun HaClient.processKitchen(typed: StateChanged<LidlOccupancySensor>) {
    val old = typed.oldState?.occupancy
    val new = typed.newState?.occupancy == true
    if (new != old) {
        if (new) {
            kitchenLight.turnOn(this, colorTemp = (454 + 250) / 2, transition = 0)
        } else {
            kitchenLight.turnOff(this)
        }
    }
}

// real 2000 // red // prog value 454
// real 6500 // blue // prog value 250
private suspend fun HaClient.processBed(state: IkeaButtonState) =
    when (state) {
        ON -> workroomLights.turnOn(this, colorTemp = (454 + 250) / 2, transition = 0)
        ARROW_LEFT_CLICK -> workroomLights.turnOn(this, RED, transition = 0)
        ARROW_RIGHT_CLICK -> workroomLights.turnOn(this, PURPLE, transition = 0)
        OFF -> workroomLights.turnOff(this)
        else -> Unit
    }

private suspend fun HaClient.onLidlSwitchChanges(state: IkeaButtonState) =
    when (state) {
        ON -> lidlLamp.turnOnLidl(this, WHITE)
        ARROW_LEFT_CLICK -> lidlLamp.turnOnLidl(this, RED)
        ARROW_RIGHT_CLICK -> lidlLamp.turnOnLidl(this, PURPLE)
        OFF -> lidlLamp.turnOffLidl(this)
        else -> Unit
    }