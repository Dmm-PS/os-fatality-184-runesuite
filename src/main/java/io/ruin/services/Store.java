package io.ruin.services;

import io.ruin.Server;
import io.ruin.api.database.DatabaseStatement;
import io.ruin.api.database.DatabaseUtils;
import io.ruin.model.World;
import io.ruin.model.entity.npc.NPC;
import io.ruin.model.entity.player.Player;
import io.ruin.model.entity.player.PlayerAction;
import io.ruin.model.inter.dialogue.ItemDialogue;
import io.ruin.model.inter.dialogue.MessageDialogue;
import io.ruin.model.inter.dialogue.NPCDialogue;
import io.ruin.model.inter.dialogue.YesNoDialogue;
import io.ruin.model.item.Item;
import io.ruin.model.item.actions.ItemAction;
import io.ruin.model.item.actions.impl.DiceBag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Store {

    /**
     * Credits item
     */

    public static void claimCredits(Player player, Item item) {
        if(!World.isLive() && !player.isAdmin()) {
            player.dialogue(new MessageDialogue("Sorry, you can't claim credits on this world."));
            return;
        }
        PlayerAction action = player.getAction(1);
        if(action == PlayerAction.FIGHT || action == PlayerAction.ATTACK) {
            player.dialogue(new MessageDialogue("Sorry, you can't claim credits from where you're standing."));
            return;
        }
        player.dialogue(new YesNoDialogue("Are you sure you want to do this?", "Your claimed credits will be made available to your online account.", item, () -> claimCredits0(player, item)));
    }

    private static void claimCredits0(Player player, Item item) {
        player.lock();
        player.dialogue(new ItemDialogue().one(item.getId(), "Attempting to claim credits, please wait...").hideContinue());
        Server.forumDb.execute(new DatabaseStatement() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement statement = null;
                ResultSet resultSet = null;
                try {
                    statement = connection.prepareStatement("SELECT * FROM xf_user WHERE user_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    statement.setInt(1, player.getUserId());
                    resultSet = statement.executeQuery();
                    if(!resultSet.next()) {
                        resultSet.moveToInsertRow();
                        updateCredits(player, item.getAmount(), resultSet);
                        resultSet.insertRow();
                    } else {
                        long credits = resultSet.getInt("donation_shards");
                        updateCredits(player, (int) Math.min(credits + item.getAmount(), Integer.MAX_VALUE), resultSet);
                        resultSet.updateRow();
                    }
                    finish(true);
                } finally {
                    DatabaseUtils.close(statement, resultSet);
                }
            }
            @Override
            public void failed(Throwable t) {
                finish(false);
                Server.logError("", t); //todo exclude timeouts
            }
            private void finish(boolean success) {
                Server.worker.execute(() -> {
                    if(success) {
                        item.remove();
                        player.dialogue(new ItemDialogue().one(item.getId(), item.getAmount() + " " + World.type.getWorldName() + " Credits have been added to your web account."));
                    } else {
                        player.dialogue(new MessageDialogue("Unable to claim credits at this time, please try again."));
                    }
                    player.unlock();
                });
            }
        });
    }

    private static void updateCredits(Player player, int credits, ResultSet resultSet) throws SQLException {
        resultSet.updateInt("user_id", player.getUserId());
        resultSet.updateString("username", player.getName()); //eh idk why this is stored lol
        resultSet.updateInt("donation_shards", credits);
    }

    static {
        ItemAction.registerInventory(13190, "claim", Store::claimCredits);
    }

    /**
     * Purchases
     */

    public static void claimPurchases(Player player, NPC npc, StoreConsumer consumer) {
        if(!World.isLive() && !player.isAdmin()) {
            player.dialogue(new NPCDialogue(npc, "Sorry, you can't claim store purchases on this world."));
            return;
        }
        player.lock();
        player.dialogue(new NPCDialogue(npc, "Attempting to claim store purchases, please wait...").hideContinue());
        Server.gameDb.execute(new DatabaseStatement() {
            List<Item> items = new ArrayList<>();
            long spent = 0;
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement statement = null;
                ResultSet resultSet = null;
                try {
                    statement = connection.prepareStatement("SELECT * FROM collection_box WHERE uid = ? AND claimed = 0", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    statement.setInt(1, player.getUserId());
                    resultSet = statement.executeQuery();
                    while(resultSet.next()) {
                        int itemId = resultSet.getInt("item_id");
                        int itemAmount = resultSet.getInt("item_amount");
                        int price = resultSet.getInt("price");
                        resultSet.updateBoolean("claimed", true);
                        resultSet.updateRow();
                        if (itemId == 1233) { // Starter package
                            items.add(new Item(290, 1));
                            items.add(new Item(6199, 1));
                            items.add(new Item(4151, 1));
                            items.add(new Item(537, 120));
                        } else if (itemId == 1234) { // Green Skin
                            player.unlockedGreenSkin = true;
                            player.sendMessage("Right click the Make-over mage to use your new green skin!");
                        } else if (itemId == 1235) { // Blue Skin
                            player.unlockedBlueSkin = true;
                            player.sendMessage("Right click the Make-over mage to use your new blue skin!");
                        } else if (itemId == 1236) { // Purple Skin
                            player.unlockedPurpleSkin = true;
                            player.sendMessage("Right click the Make-over mage to use your new purple skin!");
                        } else {
                            items.add(new Item(itemId, itemAmount));
                        }
                        //Elite void
                        if (itemId == 13072) {
                            items.add(new Item(13073, itemAmount));
                            items.add(new Item(8842, itemAmount));
                            items.add(new Item(11663, itemAmount));
                            items.add(new Item(11664, itemAmount));
                            items.add(new Item(11665, itemAmount));
                        }
                        //Mummy
                        if (itemId == 20080) {
                            items.add(new Item(20083, itemAmount));
                            items.add(new Item(20086, itemAmount));
                            items.add(new Item(20089, itemAmount));
                            items.add(new Item(20092, itemAmount));
                        }
                        //Ankou
                        if (itemId == 20095) {
                            items.add(new Item(20098, itemAmount));
                            items.add(new Item(20101, itemAmount));
                            items.add(new Item(20104, itemAmount));
                            items.add(new Item(20107, itemAmount));
                        }
                        if(itemId == DiceBag.DICE_BAG)
                            player.diceHost = true;
                        if(itemId != 13190)
                            spent += price;
                    }
                    finish(false);
                } finally {
                    DatabaseUtils.close(statement, resultSet);
                }
            }
            @Override
            public void failed(Throwable t) {
                finish(true);
                Server.logError("", t); //todo exclude timeouts
            }
            private void finish(boolean error) {
                Server.worker.execute(() -> {
                    consumer.accept(items, spent, error);
                    player.unlock();
                });
            }
        });
    }

    @FunctionalInterface
    public interface StoreConsumer {
        void accept(List<Item> items, long spent, boolean error);
    }

}
