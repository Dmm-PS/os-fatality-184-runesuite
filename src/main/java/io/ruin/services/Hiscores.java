package io.ruin.services;

import io.ruin.Server;
import io.ruin.api.database.DatabaseStatement;
import io.ruin.api.database.DatabaseUtils;
import io.ruin.model.World;
import io.ruin.model.entity.player.Player;
import io.ruin.model.inter.utils.Config;
import io.ruin.model.stat.StatType;
import io.ruin.utility.OfflineMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Hiscores {

    public static void save(Player player) {
        if (player.isAdmin())
            return;
        saveHiscores(player);
    }

    /**
     * ECO
     */

    private static void saveHiscores(Player player) {
        if (OfflineMode.enabled)
            return;
        Server.gameDb.execute(new DatabaseStatement() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement statement = null;
                ResultSet resultSet = null;
                try {
                    String tableName = player.getGameMode().isHardcoreIronman() ? "hcim_hiscores" : "skill_hiscores";
                    statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE userid = ? LIMIT 1", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    statement.setInt(1, player.getUserId());
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        resultSet.moveToInsertRow();
                        if (!player.isPKMode())
                            updateECO(player, resultSet);
                        else
                            updatePVP(player, resultSet);
                        resultSet.insertRow();
                    } else {
                        if (!player.isPKMode())
                            updateECO(player, resultSet);
                        else
                            updatePVP(player, resultSet);
                        resultSet.updateRow();
                    }
                } finally {
                    DatabaseUtils.close(statement, resultSet);
                }
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("FAILED");
                /* do nothing */
            }
        });
    }

    private static void updateECO(Player player, ResultSet resultSet) throws SQLException {
        resultSet.updateInt("userid", player.getUserId());
        int gameMode = player.getGameMode().groupId;
        resultSet.updateInt("mode", gameMode == -1 ? 0 : gameMode);
        resultSet.updateInt("rights", player.getClientGroupId());
        resultSet.updateInt("world", World.id);
        resultSet.updateInt("total_level", player.getStats().totalLevel);
        resultSet.updateInt("total_skill_level", getTotalSkillLevel(player));
        resultSet.updateLong("total_xp", player.getStats().totalXp);
        resultSet.updateLong("total_skill_xp", getTotalSkillXp(player));
        resultSet.updateInt("xp_0", (int) player.getStats().get(StatType.Attack).experience);
        resultSet.updateInt("xp_1", (int) player.getStats().get(StatType.Defence).experience);
        resultSet.updateInt("xp_2", (int) player.getStats().get(StatType.Strength).experience);
        resultSet.updateInt("xp_3", (int) player.getStats().get(StatType.Hitpoints).experience);
        resultSet.updateInt("xp_4", (int) player.getStats().get(StatType.Ranged).experience);
        resultSet.updateInt("xp_5", (int) player.getStats().get(StatType.Prayer).experience);
        resultSet.updateInt("xp_6", (int) player.getStats().get(StatType.Magic).experience);
        resultSet.updateInt("xp_7", (int) player.getStats().get(StatType.Cooking).experience);
        resultSet.updateInt("xp_8", (int) player.getStats().get(StatType.Woodcutting).experience);
        resultSet.updateInt("xp_9", (int) player.getStats().get(StatType.Fletching).experience);
        resultSet.updateInt("xp_10", (int) player.getStats().get(StatType.Fishing).experience);
        resultSet.updateInt("xp_11", (int) player.getStats().get(StatType.Firemaking).experience);
        resultSet.updateInt("xp_12", (int) player.getStats().get(StatType.Crafting).experience);
        resultSet.updateInt("xp_13", (int) player.getStats().get(StatType.Smithing).experience);
        resultSet.updateInt("xp_14", (int) player.getStats().get(StatType.Mining).experience);
        resultSet.updateInt("xp_15", (int) player.getStats().get(StatType.Herblore).experience);
        resultSet.updateInt("xp_16", (int) player.getStats().get(StatType.Agility).experience);
        resultSet.updateInt("xp_17", (int) player.getStats().get(StatType.Thieving).experience);
        resultSet.updateInt("xp_18", (int) player.getStats().get(StatType.Slayer).experience);
        resultSet.updateInt("xp_19", (int) player.getStats().get(StatType.Farming).experience);
        resultSet.updateInt("xp_20", (int) player.getStats().get(StatType.Runecrafting).experience);
        resultSet.updateInt("xp_21", (int) player.getStats().get(StatType.Hunter).experience);
        resultSet.updateInt("xp_22", (int) player.getStats().get(StatType.Construction).experience);
        resultSet.updateInt("kills", Config.PVP_KILLS.get(player));
        resultSet.updateInt("deaths", Config.PVP_DEATHS.get(player));
        resultSet.updateInt("highest_shutdown", player.highestShutdown);
        resultSet.updateInt("highest_killspree", player.highestKillSpree);
        resultSet.updateInt("pk_rating", player.pkRating);
        resultSet.updateInt("claimed_votes", player.claimedVotes);
        resultSet.updateInt("easy_clue_count", player.easyClueCount);
        resultSet.updateInt("medium_clue_count", player.medClueCount);
        resultSet.updateInt("hard_clue_count", player.hardClueCount);
        resultSet.updateInt("elite_clue_count", player.eliteClueCount);
        resultSet.updateInt("master_clue_count", player.masterClueCount);
    }

    private static void updatePVP(Player player, ResultSet resultSet) throws SQLException {
        resultSet.updateInt("userid", player.getUserId());
        int gameMode = player.getGameMode().groupId;
        resultSet.updateInt("mode", gameMode == -1 ? 0 : gameMode);
        resultSet.updateInt("rights", player.getClientGroupId());
        resultSet.updateInt("world", World.id);
        resultSet.updateInt("kills", Config.PVP_KILLS.get(player));
        resultSet.updateInt("deaths", Config.PVP_DEATHS.get(player));
        resultSet.updateInt("highest_shutdown", player.highestShutdown);
        resultSet.updateInt("highest_killspree", player.highestKillSpree);
        resultSet.updateInt("pk_rating", player.pkRating);
        resultSet.updateInt("claimed_votes", player.claimedVotes); }


    private static int getTotalSkillLevel(Player player) {
        int totalLevel = player.getStats().totalLevel;
        for (int i = 0; i < 7; i++)
            totalLevel -= player.getStats().get(i).fixedLevel;
        return totalLevel;
    }

    private static long getTotalSkillXp(Player player) {
        long totalExp = player.getStats().totalXp;
        for (int i = 0; i < 7; i++)
            totalExp -= player.getStats().get(i).experience;
        return totalExp;
    }
}