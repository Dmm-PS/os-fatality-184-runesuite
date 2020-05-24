package io.ruin.content.areas.ver_sinhaza

import io.ruin.api.chat
import io.ruin.api.event
import io.ruin.api.openMainInterface
import io.ruin.api.whenObjClick
import io.ruin.content.areas.ver_sinhaza.MysteriousStranger.talkedAboutVerzik
import io.ruin.model.entity.player.Player

/**
 * The interaction script for the Notice board in the centre of Ver Sinhaza.
 * @author Heaven
 */
object NoticeBoard {

    private const val ID = 32655

    init {
        whenObjClick(ID, 1) { player, _ ->
            player.read()
        }
    }

    private fun Player.read() = event {
        if (!talkedAboutVerzik()) {
            chat("Hey. Over here.", MysteriousStranger.ID)
            return@event
        }

        openMainInterface(364)
    }
}