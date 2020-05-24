package io.ruin.model.skills.agility;

import io.ruin.model.map.object.actions.ObjectAction;
import io.ruin.model.skills.agility.shortcut.CrumblingWall;
import io.ruin.model.skills.agility.shortcut.LooseRailing;
import io.ruin.model.skills.agility.shortcut.Stile;
import io.ruin.model.skills.agility.shortcut.UnderwallTunnel;

public class Shortcuts {
    static {
        // Stile at Fred the Farmer's sheep field and the stile at Falador cabbage patch

        // Stile at Taverly which is required for the clue scroll south of the long house
        // Stile into the beahive in Camelot
        ObjectAction.register(993, "climb-over", (p, obj) -> Stile.shortcut(p, obj, 1));

        // Draynor Stile into cabbage field
        ObjectAction.register(7527, "climb-over", (p, obj) -> Stile.shortcut(p, obj, 1));

        // Lumbridge Stile into sheep farm
        ObjectAction.register(12892, "climb-over", (p, obj) -> Stile.shortcut(p, obj, 1));

        // Falador Agility Shortcut
        ObjectAction.register(24222, "climb-over", (p, obj) -> CrumblingWall.shortcut(p, obj, 5));

        // Camelot loose railing
        ObjectAction.register(51, 2662, 3500, 0, "squeeze-through", (p, obj) -> LooseRailing.shortcut(p, obj, 1));

        // (Grapple) Over the River Lum to Al Kharid
        // Rope swing to Moss Giant Island
        // (Grapple) Scale Falador wall
        // Stepping stones in Brimhaven Dungeon
        // Varrock south fence jump
        // Scale Goblin Village wall
        // Monkey bars under Edgeville
        // Yanille Agility Shortcut
        // Watchtower wall climb
        // Coal Truck log balance

        // Grand Exchange Agility Shortcut
        ObjectAction.register(16529, "climb-into", (p, obj) -> UnderwallTunnel.shortcut(p, obj, 21));
        ObjectAction.register(16530, "climb-into", (p, obj) -> UnderwallTunnel.shortcut(p, obj, 21));

        // Pipe contortion in Brimhaven Dungeon
        // Eagles' Peak Agility Shortcut
        // Underwall tunnel	Falador Agility Shortcut
        // Stepping stones in Brimhaven Dungeon
        // Draynor Manor stones to Champions' Guild
        // (Grapple) Scale Catherby cliffside
        // Cairn Isle rock slide climb
        // Ardougne log balance shortcut
        // Pipe contortion in Brimhaven Dungeon
        // Trollweiss/Rellekka Hunter area cliff scramble
        // (Grapple) Escape from the Water Obelisk Island
        // Gnome Stronghold Shortcut

        // Al Kharid mining pit cliffside scramble


        // (Grapple) Scale Yanille wall
        // Yanille Agility dungeon balance ledge
        // Kourend lake isle jump
        // Trollheim easy cliffside scramble
        // Dwarven Mine narrow crevice
        // Draynor narrow tunnel
        // Trollheim medium cliffside scramble
        // Trollheim advanced cliffside scramble
        // Kourend river jump
        // Tirannwn log balance
        // Cosmic Temple - medium narrow walkway
        // Deep Wilderness Dungeon narrow shortcut
        // Trollheim hard cliffside scramble
        // Log balance to Fremennik Province
        // Contortion in Yanille Dungeon small room
        // Arceuus essence mine boulder leap
        // Stepping stone into Morytania near the Nature Grotto
        // Pipe from Edgeville dungeon to Varrock Sewers
        // Arceuus essence mine eastern scramble
        // (Grapple) Karamja Volcano
        // Motherlode Mine wall shortcut
        // Stepping stone by Miscellania docks
        // Monkey bars under Yanille
        // Stepping stones in the Cave Kraken lake
        // Rellekka east fence shortcut
        // Port Phasmatys Ectopool Shortcut
        // Elven Overpass (Arandar) easy cliffside scramble
        // Wilderness from God Wars Dungeon area climb
        // Squeeze through to God Wars Dungeon surface access
        // Estuary crossing on Mos Le'Harmless
        // Slayer Tower medium spiked chain climb
        // Fremennik Slayer Dungeon narrow crevice
        // Taverley Dungeon lesser demon fence
        // Trollheim Wilderness Route
        // Temple on the Salve to Morytania shortcut
        // Cosmic Temple advanced narrow walkway
        // Lumbridge Swamp to Al Kharid narrow crevice
        // Heroes' Guild tunnel
        // Yanille Dungeon's rubble climb
        // Elven Overpass (Arandar) medium cliffside scramble
        // Arceuus essence mine northern scramble
        // Taverley Dungeon pipe squeeze to Blue dragon lair
        // (Grapple) Cross cave, south of Dorgesh-Kaan
        // Rope descent to Saradomin's Encampment
        // Slayer Tower advanced spiked chain climb
        // Stronghold Slayer Cave wall-climb
        // Troll Stronghold wall-climb
        // Arceuus essence mine western descent
        // Lava Dragon Isle jump
        // Island crossing near Zul-Andra
        // Shilo Village stepping stones over the river
        // Kharazi Jungle vine climb
        // Cave crossing south of Dorgesh-Kaan
        // Taverley Dungeon spiked blades jump
        // Fremennik Slayer Dungeon chasm jump
        // Lava Maze northern jump
        // Brimhaven Dungeon eastern stepping stones
        // Elven Overpass (Arandar) advanced cliffside scramble
        // Kalphite Lair wall shortcut
        // Brimhaven Dungeon vine to baby green dragons
    }
}
