import com.ybznek.ha.core.EntityId
import com.ybznek.ha.entitytypes.light.LidlLight
import com.ybznek.ha.entitytypes.light.Light
import com.ybznek.ha.entitytypes.occupancy.IkeaOccupancySensor
import com.ybznek.ha.entitytypes.occupancy.LidlOccupancySensor
import com.ybznek.ha.entitytypes.switchtype.IkeaSwitch

object EntityIds {
    val ikeaSwitchForLidl = EntityId<IkeaSwitch>("button.switch_2_identify")
    val extraIkeaSwitch = EntityId<IkeaSwitch>("button.switch_3_identify")
    val bedSwitch = EntityId<IkeaSwitch>("button.switch_4_identify")
    val doorSwitch = EntityId<IkeaSwitch>("button.switch_1_identify")
    val lidlLamp = EntityId<LidlLight>("light.zarovka_lidl_lampa")
    val workroomLights = EntityId<Light>("light.workroom_lights")

    val hallLight = EntityId<Light>("light.ikea_light_hall")
    val hallOccupancySensor = EntityId<IkeaOccupancySensor>("binary_sensor.ikea_motion_sensor_occupancy")

    val kitchenLight = EntityId<Light>("light.zarovka_ikea_kuchyne")
    val kitchenOccupancySensor = EntityId<LidlOccupancySensor>("binary_sensor.occupancy_sensor_occupancy")
}