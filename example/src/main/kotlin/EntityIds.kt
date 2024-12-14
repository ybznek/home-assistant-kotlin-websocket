import com.ybznek.ha.core.EntityId
import com.ybznek.ha.entitytypes.light.LidlLight
import com.ybznek.ha.entitytypes.light.Light
import com.ybznek.ha.entitytypes.switchtype.IkeaSwitch

object EntityIds {
    val ikeaSwitch = EntityId<IkeaSwitch>("button.switch_2_identify")
    val bedSwitch = EntityId<IkeaSwitch>("button.switch_4_identify")
    val doorSwitch = EntityId<IkeaSwitch>("button.switch_1_identify")
    val lidlLamp = EntityId<LidlLight>("light.zarovka_lidl_lampa")
    val workroomLights = EntityId<Light>("light.workroom_lights")
}