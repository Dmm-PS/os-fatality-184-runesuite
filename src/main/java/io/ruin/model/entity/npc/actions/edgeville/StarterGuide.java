package io.ruin.model.entity.npc.actions.edgeville;

import io.ruin.cache.NPCDef;
import io.ruin.data.impl.Help;
import io.ruin.model.World;
import io.ruin.model.entity.npc.NPC;
import io.ruin.model.entity.npc.NPCAction;
import io.ruin.model.entity.player.GameMode;
import io.ruin.model.entity.player.Player;
import io.ruin.model.entity.player.PlayerGroup;
import io.ruin.model.entity.shared.LockType;
import io.ruin.model.entity.shared.listeners.LoginListener;
import io.ruin.model.entity.shared.listeners.LogoutListener;
import io.ruin.model.entity.shared.listeners.SpawnListener;
import io.ruin.model.inter.Interface;
import io.ruin.model.inter.InterfaceType;
import io.ruin.model.inter.dialogue.MessageDialogue;
import io.ruin.model.inter.dialogue.NPCDialogue;
import io.ruin.model.inter.dialogue.OptionsDialogue;
import io.ruin.model.inter.handlers.XpCounter;
import io.ruin.model.inter.utils.Option;
import io.ruin.model.map.Direction;
import io.ruin.model.stat.StatType;
import io.ruin.network.central.CentralClient;
import io.ruin.services.LatestUpdate;
import io.ruin.utility.Broadcast;

import static io.ruin.cache.ItemID.COINS_995;

public class StarterGuide {

	private static final NPC GUIDE = SpawnListener.first(306);

	static {
		NPCDef.get(307).ignoreOccupiedTiles = true;
		NPCAction.register(GUIDE, "view-help", (player, npc) -> Help.open(player));
		NPCAction.register(GUIDE, "view-guide", (player, npc) -> player.dialogue(
                new OptionsDialogue("Watch the guide?",
                        new Option("Yes", () -> tutorial(player)),
                        new Option("No", player::closeDialogue))
        ));
		NPCAction.register(GUIDE, "talk-to", StarterGuide::optionsDialogue);

		LoginListener.register(player -> {
            if (player.newPlayer) {
                XpCounter.select(player, 1);
                CentralClient.sendClanRequest(player.getUserId(), "Help");
                ecoTutorial(player);
            } else {
                player.getPacketSender().sendMessage("Latest Update: " + LatestUpdate.LATEST_UPDATE_TITLE + "|" + LatestUpdate.LATEST_UPDATE_URL, "", 14);
            }
		});
	}

	private static void optionsDialogue(Player player, NPC npc) {
        player.dialogue(new NPCDialogue(npc, "Hello " + player.getName() + ", is there something I could assist you with?"),
                player.isPKMode() ?
                    new OptionsDialogue(
                        new Option("Remove PK Mode", () -> removePkMode(player)),
                        new Option("View help pages", () -> Help.open(player)),
                        new Option("Replay tutorial", () -> ecoTutorial(player))) :
                    new OptionsDialogue(
                        new Option("View help pages", () -> Help.open(player)),
                        new Option("Replay tutorial", () -> ecoTutorial(player))));
	}

    private static void removePkMode(Player player) {
        player.startEvent(event -> player.dialogue(new OptionsDialogue("Are you sure? Your combat stats will be reset!",
                new Option("Yes", () -> {
                    PlayerGroup.PK_MODE.removePKMode(player, "mode");
                    player.leave(PlayerGroup.PK_MODE);

                    // Remove the players PK stats
                    player.getStats().get(StatType.Attack).set(1);
                    player.getStats().get(StatType.Strength).set(1);
                    player.getStats().get(StatType.Defence).set(1);
                    player.getStats().get(StatType.Hitpoints).set(10);
                    player.getStats().get(StatType.Magic).set(1);
                    player.getStats().get(StatType.Ranged).set(1);
                    player.getStats().get(StatType.Prayer).set(1);
                    player.getCombat().updateLevel();
                }),
                new Option("No", player::closeDialogue)
        )));
    }
	private static void ecoTutorial(Player player) {
		boolean actuallyNew = player.newPlayer;
		player.inTutorial = true;
		player.startEvent(event -> {
			player.lock(LockType.FULL_ALLOW_LOGOUT);
			player.getMovement().teleport(3086, 3495, 0);
			if (actuallyNew) {
				player.openInterface(InterfaceType.MAIN, Interface.APPEARANCE_CUSTOMIZATION);
				while (player.isVisibleInterface(Interface.APPEARANCE_CUSTOMIZATION)) {
					event.delay(1);
				}
			}
			NPC guide = new NPC(307).spawn(3086, 3496, 0, Direction.SOUTH, 0); // 307 is a copy of 306 without options so it doesnt get in other people's way
			player.logoutListener = new LogoutListener().onLogout(p -> guide.remove());
			player.getPacketSender().sendHintIcon(guide);
			player.face(guide);
			player.dialogue(new NPCDialogue(guide, "Greetings, " + player.getName() + "! Welcome to " + World.type.getWorldName() + "."));
			if (actuallyNew) {
				player.dialogue(new NPCDialogue(guide, "Before I let you go, I need to ask you a question."),
						new NPCDialogue(guide, "Do you want to see the options for Iron Man modes?"),
						new OptionsDialogue("View Iron Man options?", new Option("Yes", () -> {
							GameMode.openSelection(player);
							player.unsafeDialogue(new MessageDialogue("Close the interface once you're happy with your selection.<br><br><col=ff0000>WARNING:</col> This is the ONLY chance to choose your Iron Man mode.").hideContinue());
							//its fine to use unsafe here
						}), new Option("No", player::closeDialogue)));
				event.waitForDialogue(player);
				String text = "You want to be a part of the economy, then? Great!";
				if (player.getGameMode() == GameMode.IRONMAN) {
					text = "Iron Man, huh? Self-sufficiency is quite a challenge, good luck!";
				} else if (player.getGameMode() == GameMode.HARDCORE_IRONMAN) {
					text = "Hardcore?! You only live once... make it count!";
				} else if (player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
					text = "Ultimate Iron Man... Up for quite the challenge, aren't you?";
				}
				if (!player.getGameMode().isIronMan()) {
					player.dialogue(new NPCDialogue(guide, "Not interested in any ironman modes? No problem! Are you interested in playing the PK mode? Players who play this mode are able to set their combat stats, but"),
							new NPCDialogue(guide, "as a result, have a lower chance of getting drops through PVM."),
							new OptionsDialogue("Play using the PK Mode?",
									new Option("Yes, play using the PK mode.", () -> {
										player.dialogue(new NPCDialogue(guide, "You're interested in PVP, hey? With this game mode you're able to set your stats and use a variety of preset builds which can be accessed in your player journal."),
												new NPCDialogue(guide, "You're also able to set your combat stats by typing ::setlevel, which will prompt you to select which skill you'd like to set."),
												new NPCDialogue(guide, "There you go, some basic stuff. If you need anything else, remember to check Sigmund's shop.") {
													@Override
													public void open(Player player) {
														PlayerGroup.PK_MODE.sync(player, "mode");
														player.join(PlayerGroup.PK_MODE);
														addPKModeItemToBank(player);
														player.newPlayer = false;
														super.open(player);
													}
												});
										Broadcast.WORLD.sendNews(player.getName() + " has just joined " + World.type.getWorldName() + " playing the PK Mode!");
									}),
									new Option("No, play as a regular player.", () -> {
										player.dialogue(new NPCDialogue(guide, "I see you're a fan of the grind! Excellent.. I'll give you a few items to help get you started and fill your bank with various PK items"),
												new NPCDialogue(guide, "There you go, some basic stuff. If you need anything else, remember to check Sigmund's shop.") {
													@Override
													public void open(Player player) {
														giveEcoStarter(player);
														player.newPlayer = false;
														super.open(player);
													}
												});
										Broadcast.WORLD.sendNews(player.getName() + " has just joined " + World.type.getWorldName() + "!");
									})
							)
					);
				} else {
					player.dialogue(new NPCDialogue(guide, text),
							new NPCDialogue(guide, "I'll give you a few items to help get you started..."),
							new NPCDialogue(guide, "There you go, some basic stuff. If you need anything else, remember to check Sigmund's shop.") {
								@Override
								public void open(Player player) {
									giveEcoStarter(player);
									player.newPlayer = false;
									super.open(player);
								}
							});
					Broadcast.WORLD.sendNews(player.getName() + " has just joined " + World.type.getWorldName() + "!");
				}
			}
			event.waitForDialogue(player);
			/*player.dialogue(new NPCDialogue(guide, "I Welcome you to " + World.type.getWorldName() + ", I'm going to give you some pointers to get started the right way!"),
					new NPCDialogue(guide, "The teleport wizard will be your go to. He will teleport you all over " + World.type.getWorldName() + " to your PVMing, PKing or skilling locations!") {
						@Override
						public void open(Player player) {
							player.getMovement().teleport(3088, 3505, 0);
							super.open(player);
						}
					},
					new NPCDialogue(guide, "Here we have our wonderful trading post in which you can list items for sale and wait for a player to contact you about purchasing it, or you can contact players yourself!") {
						@Override
						public void open(Player player) {
							player.getMovement().teleport(3091, 3495, 0);
							player.face(3090, 3495);
							super.open(player);
						}
					},
					new NPCDialogue(guide, "One of the easiest and safest ways for some quick cash is Thieving. Once you can steal from the level 50+ Stalls you start raking in the cash!") {
						@Override
						public void open(Player player) {
							player.getMovement().teleport(3084, 3492, 0);
							super.open(player);
						}
					},
					new NPCDialogue(guide, "Wow a good money maker is Last Man Standing! you can play this minigame without requirements and spend your winning tokens on good rewards!") {
						@Override
						public void open(Player player) {
							player.getMovement().teleport(3391, 3178, 0);
							player.face(3407, 3181);
							super.open(player);
						}
					},
					new NPCDialogue(guide, "Another good money maker is doing slayer. If you talk to krystillia you can choose between Wilderness or non- wilderness slayer tasks. You can also pick between an easy,medium or hard slayer task.") {
						@Override
						public void open(Player player) {
							player.getMovement().teleport(3092, 3505, 0);
							super.open(player);
						}
					},
					new NPCDialogue(guide, "At only level 40 slayer you can kill terror dogs in the wilderness. They are one of the best money makers in " + World.type.getWorldName() + "! They have a 100% chance of dropping cash and PK Tickets, and with that a chance of PVP Armours.") {
						@Override
						public void open(Player player) {
							super.open(player);
						}
					},
					new NPCDialogue(guide, "Sigmund will come in handy aswell. You can buy all the starter gear you need from him. He also has a right-click option where you can sell all your items for a good price.") {
						@Override
						public void open(Player player) {
							player.getMovement().teleport(3077, 3510, 0);
							super.open(player);
						}
					},
					new NPCDialogue(guide, "Last but not least " + World.type.getWorldName() + " is very rewarding to players who ::Vote and help us grow. If you ::Vote on all sites you can claim 9 vote tickets from the vote manager. You will also get 1 hour of 2x EXP bonus and a Vote Mystery Box, " + World.type.getWorldName() + " appreciates you voting.") {
						@Override
						public void open(Player player) {
							player.getMovement().teleport(3081, 3512, 0);
							super.open(player);
						}
					},
					new NPCDialogue(guide, "Be sure to join the ::discord to join our friendly community. I hope  you enjoy your stay on " + World.type.getWorldName() + "!"),
					new NPCDialogue(guide, "To learn more about " + World.type.getWorldName() + ", have a look at the Introductory achievements. They are basic tasks that will reward you with some gold and teach you more about this world.") {
						@Override
						public void open(Player player) {
							super.open(player);
							player.getPacketSender().sendClientScript(915, "i", 2);
							// Journal.ACHIEVEMENTS.send(player);
						}
					},
					new NPCDialogue(guide, "You can find me near the bank if you have more questions."));*/
			player.sendMessage("If you have any questions please join the clan chat '" + World.type.getWorldName() + "'.");
			//event.waitForDialogue(player);
			guide.animate(863);
			player.inTutorial = false;
			player.unlock();
			guide.addEvent(e -> {
				e.delay(2);
				World.sendGraphics(86, 50, 0, guide.getPosition());
				player.logoutListener = null;
				guide.remove();
			});
		});
	}

	private static void giveEcoStarter(Player player) {
        player.getInventory().add(COINS_995, 10000); // gp
        player.getInventory().add(558, 500); // Mind Rune
        player.getInventory().add(556, 1500); // Air Rune
        player.getInventory().add(554, 1000); // Fire Rune
        player.getInventory().add(555, 1000); // Water Rune
        player.getInventory().add(557, 1000); // Earth Rune
        player.getInventory().add(562, 1000); // Chaos Rune
        player.getInventory().add(560, 500); // Death Rune
        player.getInventory().add(1381, 1); // Air Staff
        player.getInventory().add(362, 50); // Tuna
        player.getInventory().add(863, 300); // Iron Knives
        player.getInventory().add(867, 150); // Adamant Knives
        player.getInventory().add(1169, 1); // Coif
        player.getInventory().add(1129, 1); // Leather body
        player.getInventory().add(1095, 1); // Leather Chaps
        player.getInventory().add(13385, 1); // Xeric Hat
        player.getInventory().add(12867, 1); // Blue d hide set
        player.getInventory().add(13024, 1); // Rune set
        player.getInventory().add(11978, 1); // Glory 6
        player.getInventory().add(13387, 1); // Xerican Top
        player.getInventory().add(1323, 1); // Iron scim
        player.getInventory().add(1333, 1); // Rune scim
        player.getInventory().add(4587, 1); // Dragon Scim
        switch (player.getGameMode()) {
            case IRONMAN:
                player.getInventory().add(12810, 1);
                player.getInventory().add(12811, 1);
                player.getInventory().add(12812, 1);
                break;
            case ULTIMATE_IRONMAN:
                player.getInventory().add(12813, 1);
                player.getInventory().add(12814, 1);
                player.getInventory().add(12815, 1);
                break;
            case HARDCORE_IRONMAN:
                player.getInventory().add(20792, 1);
                player.getInventory().add(20794, 1);
                player.getInventory().add(20796, 1);
                break;
            case STANDARD:
                player.getInventory().add(COINS_995, 1240000);
                break;
        }
    }

	private static NPC find(Player player, int id) {
		for (NPC n : player.localNpcs()) {
			if (n.getId() == id)
				return n;
		}
		throw new IllegalArgumentException();
	}

	private static void setDrag(Player player) {
		player.dialogue(
				new OptionsDialogue("What drag setting would you like to use?",
						new Option("5 (OSRS) (2007) Drag", () -> setDrag(player, 5)),
						new Option("10 (Pre-EoC) (2011) Drag", () -> setDrag(player, 10))
				)
		);
	}

	private static void setDrag(Player player, int drag) {
		player.dragSetting = drag;
	}

	private static void tutorial(Player player) {
        ecoTutorial(player);
	}

	private static void addPKModeItemToBank(Player player) {
        player.getBank().add(19625, 5); // Home teleport
        player.getBank().add(2550, 3); // Recoils
        player.getBank().add(385, 125); // Sharks
        player.getBank().add(3144, 50); // Karambwans
        player.getBank().add(2436, 5); // attk
        player.getBank().add(2440, 5); // str
        player.getBank().add(2444, 5); // range
        player.getBank().add(3024, 5); // restore
//Next Line
        player.getBank().add(6685, 10); // brew
        player.getBank().add(560, 2250); // Death runes
        player.getBank().add(565, 1000); // Blood runes
        player.getBank().add(561, 300); // Nature runes
        player.getBank().add(145, 1); // atk
        player.getBank().add(157, 1); // str
        player.getBank().add(169, 1); // range
        player.getBank().add(3026, 1); // restore
//Next Line
        player.getBank().add(6687, 1); // brew
        player.getBank().add(9075, 400); // Astral runes
        player.getBank().add(555, 6000); // Water runes
        player.getBank().add(557, 1000); // Earth runes
        player.getBank().add(147, 1); // atk
        player.getBank().add(159, 1); // str
        player.getBank().add(171, 1); // range
        player.getBank().add(3028, 1); // restore
//Next Line
        player.getBank().add(6689, 1); // brew
        player.getBank().add(7458, 100); // mithril gloves for pures
        player.getBank().add(7462, 100); // gloves
        player.getBank().add(3842, 100); // god book
        player.getBank().add(149, 1); // atk
        player.getBank().add(161, 1); // str
        player.getBank().add(173, 1); // range
        player.getBank().add(3030, 1); // restore
//Next Line
        player.getBank().add(6691, 1); // brew
        player.getBank().add(9144, 500); // bolts
        player.getBank().add(2503, 5); // hides
        player.getBank().add(4099, 5); // Mystic
        player.getBank().add(2414, 100); // zamy god cape
        player.getBank().add(10828, 5); // neit helm
        player.getBank().add(4587, 5); // Scim
        player.getBank().add(1163, 3); // rune full helm
//Next Line
        player.getBank().add(562, 50); // Chaos rune
        player.getBank().add(892, 400); // rune arrows
        player.getBank().add(2497, 5); // hides
        player.getBank().add(4101, 5); // Mystic
        player.getBank().add(4675, 5); // ancient staff
        player.getBank().add(1201, 5); // rune
        player.getBank().add(5698, 5); // dagger
        player.getBank().add(1127, 3); // rune pl8
//Next Line
        player.getBank().add(563, 50); // law rune
        player.getBank().add(9185, 5); // crossbow
        player.getBank().add(10499, 100); // avas
        player.getBank().add(4103, 5); // Mystic
        player.getBank().add(4107, 5); // Mystic
        player.getBank().add(3105, 5); // climbers
        player.getBank().add(11978, 3); // glory(6)
        player.getBank().add(1079, 3); // rune legs
//Next Line
        player.getBank().add(1215, 2); // dagger unpoisoned
        player.getBank().add(3751, 2); // zerker helm
        player.getBank().add(1093, 2); // rune


        // Give the players PK stats
		player.getStats().get(StatType.Attack).set(99);
		player.getStats().get(StatType.Strength).set(99);
		player.getStats().get(StatType.Defence).set(99);
		player.getStats().get(StatType.Hitpoints).set(99);
		player.getStats().get(StatType.Magic).set(99);
		player.getStats().get(StatType.Ranged).set(99);
		player.getStats().get(StatType.Prayer).set(99);
		player.getCombat().updateLevel();
	}

}
