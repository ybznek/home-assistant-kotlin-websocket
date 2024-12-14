import com.ybznek.ha.core.EntityId
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.dispatcher.StateChanged
import com.ybznek.ha.entitytypes.switchtype.meassuring.MeasuringSwitch

class PrinterControl(private val cli: HaClient) {
    val id = EntityId<MeasuringSwitch>("switch.power_switch_printer")

    fun stateChanged(stateChanged: StateChanged<TypedEntity>) {
        if (stateChanged.entity == id) {
            println(stateChanged.changedAttributes)
        }
    }

}