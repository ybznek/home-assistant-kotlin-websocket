import EntityIds.bedSwitch
import EntityIds.doorSwitch
import EntityIds.ikeaSwitch
import EntityIds.lidlLamp
import EntityIds.workroomLights
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.dispatcher.StateChanged
import com.ybznek.ha.entitytypes.light.RgbColor.Companion.PURPLE
import com.ybznek.ha.entitytypes.light.RgbColor.Companion.RED
import com.ybznek.ha.entitytypes.light.RgbColor.Companion.WHITE
import com.ybznek.ha.entitytypes.light.turnOn
import com.ybznek.ha.entitytypes.switchtype.IkeaButtonState
import com.ybznek.ha.entitytypes.switchtype.IkeaButtonState.ARROW_LEFT_CLICK
import com.ybznek.ha.entitytypes.switchtype.IkeaButtonState.ARROW_RIGHT_CLICK
import com.ybznek.ha.entitytypes.switchtype.IkeaButtonState.OFF
import com.ybznek.ha.entitytypes.switchtype.IkeaButtonState.ON
import com.ybznek.ha.entitytypes.switchtype.IkeaSwitch
import com.ybznek.ha.entitytypes.switchtype.ikeaSwitchState

suspend fun onChange(haClient: HaClient, stateChanged: StateChanged<TypedEntity>) {
    when (stateChanged.entity) {
        ikeaSwitch -> haClient.onChange(stateChanged.typed<IkeaSwitch>().newState.ikeaSwitchState)
        bedSwitch,
        doorSwitch -> haClient.processBed(stateChanged.typed<IkeaSwitch>().newState.ikeaSwitchState)
    }
}

private suspend fun HaClient.processBed(state: IkeaButtonState) =
    when (state) {
        ON -> workroomLights.turnOn(this, WHITE)
        ARROW_LEFT_CLICK -> workroomLights.turnOn(this, RED)
        ARROW_RIGHT_CLICK -> workroomLights.turnOn(this, PURPLE)
        OFF -> workroomLights.turnOn(this)
        else -> Unit
    }

private suspend fun HaClient.onChange(state: IkeaButtonState) =
    when (state) {
        ON -> lidlLamp.turnOnLidl(this, WHITE)
        ARROW_LEFT_CLICK -> lidlLamp.turnOnLidl(this, RED)
        ARROW_RIGHT_CLICK -> lidlLamp.turnOnLidl(this, PURPLE)
        OFF -> lidlLamp.turnOffLidl(this)
        else -> Unit
    }
