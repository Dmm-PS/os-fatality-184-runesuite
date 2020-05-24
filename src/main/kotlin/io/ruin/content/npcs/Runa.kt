package io.ruin.content.npcs

import io.ruin.api.chat
import io.ruin.api.event
import io.ruin.api.options
import io.ruin.api.whenNpcClick
import io.ruin.model.entity.npc.NPC
import io.ruin.model.entity.player.Player

/**
 * @author Leviticus
 */
object Runa {

    private const val RUNA = 1078

    init {
        whenNpcClick(RUNA, 1) { player, npc ->
            player.talk(npc)
        }
    }

    private fun Player.talk(woman: NPC) = event {
        woman.chat("Hello there, Are you interested in looking at the Tournament Cosmetic Shop?")
        if (options("Yes", "No") == 1) {
            woman.def.shop.open(player)
        } else {
            woman.chat("Very well.")
        }
    }
}