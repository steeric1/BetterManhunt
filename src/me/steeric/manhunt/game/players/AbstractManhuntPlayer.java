package me.steeric.manhunt.game.players;

import me.steeric.manhunt.game.Game;
import me.steeric.manhunt.game.data.MilestoneProgress;
import me.steeric.manhunt.game.managing.ChatManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public abstract class AbstractManhuntPlayer extends AbstractPlayer {

    private final MilestoneProgress progress;
    private ChatManager.ChatMode chatMode;
    private boolean dead;

    public AbstractManhuntPlayer(Player player, Game game) {
        super(player, game);
        this.progress = new MilestoneProgress();
        this.chatMode = ChatManager.ChatMode.TO_TEAM;
        this.dead = false;
    }

    public boolean isDead() { return this.dead; }
    public void setDead(boolean dead) { this.dead = dead; }

    @Override
    public void preparePlayer() {

        super.preparePlayer();

        Player playerHandle = this.getPlayerHandle();
        if (playerHandle != null)
            playerHandle.setGameMode(GameMode.SURVIVAL);
    }

    @Override
    public ChatManager.ChatMode getChatMode() {
        return this.chatMode;
    }

    @Override
    public void setChatMode(ChatManager.ChatMode mode) {
        this.chatMode = mode;
    }

    @Override
    public abstract String getPlayerListName();

    @Override
    public abstract String toString();

    public MilestoneProgress getMilestoneProgress() {
        return progress;
    }
}
