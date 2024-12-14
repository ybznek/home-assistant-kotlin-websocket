package com.ybznek.ha.entitytypes.switchtype

import com.ybznek.ha.core.data.EntityState
import com.ybznek.ha.entitytypes.switchtype.IkeaButtonState.entries

enum class IkeaButtonState(val value: String) {
    NONE(""),
    ON("on"),
    OFF("off"),
    BRIGHTNESS_MOVE_UP("brightness_move_up"),
    BRIGHTNESS_MOVE_DOWN("brightness_move_down"),
    BRIGHTNESS_STOP("brightness_stop"),
    ARROW_RIGHT_CLICK("arrow_right_click"),
    ARROW_LEFT_CLICK("arrow_left_click"),
    ARROW_LEFT_HOLD("arrow_left_hold"),
    ARROW_RIGHT_HOLD("arrow_right_hold"),
    ARROW_LEFT_RELEASE("arrow_left_release"),
    ARROW_RIGHT_RELEASE("arrow_right_release");

    companion object {
        private val mapping: Map<String, IkeaButtonState> = entries.associateByTo(HashMap()) { it.value }

        fun fromString(str: String): IkeaButtonState =
            IkeaButtonState.mapping[str]
                ?: throw IllegalArgumentException("Unknown action $str")
    }
}


val EntityState<IkeaSwitch>.ikeaSwitchState get() = IkeaButtonState.fromString(getAttribute<String>("action"))

