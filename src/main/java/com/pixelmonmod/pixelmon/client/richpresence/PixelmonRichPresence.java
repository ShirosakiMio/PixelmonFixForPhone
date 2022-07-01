//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.pixelmonmod.pixelmon.client.richpresence;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.species.Pokedex;
import com.pixelmonmod.pixelmon.api.util.ThreadPool;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.storage.ClientStorageManager;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.TextFormatting;

public final class PixelmonRichPresence {
    public static final String LOADING_GAME = "Loading Game (%v)";
    public static final String MAIN_MENU = "Main Menu (%v)";
    public static final String SINGLEPLAYER = "Singleplayer (%v)";
    public static final String NOSERVER_DETAIL = "Catching them all!";
    public static final String SERVER_DETAIL = "Playing on %s";
    public static final String LOCAL_MULTIPLAYER = "Local Multiplayer (%v)";
    public static final String MULTIPLAYER = "Multiplayer (%v)";
    public static final String ICON_NAME = "discord.gg/pixelmon";
    private final ThreadPool THREAD_POOL = ThreadPool.createNewThreadPool("Pixelmon Rich Presence", 2);
    private final String application;
    private long time;
    private boolean available = false;
    private boolean initing = false;
    private boolean active = false;
    private boolean acknowledgedServer = false;

    public PixelmonRichPresence(String application) {
        this.application = application;
        this.init();
    }

    public boolean isAcknowledgedServer() {
        return this.acknowledgedServer;
    }

    public void setAcknowledgedServer(boolean acknowledgedServer) {
        this.acknowledgedServer = acknowledgedServer;
    }

    public void init() {
        this.initing = true;
        this.THREAD_POOL.scheduleAtFixedRate(this::poll, 0L, 10L);
        Runtime.getRuntime().addShutdownHook(new Thread(this::destruct));
        this.setState("Loading Game (%v)");

    }

    public void setState(String state) {
        this.setState(state, false);
    }

    public void setState(String state, boolean time) {
        this.setState(state, "", time, -1, -1);
    }

    public void setState(String state, String details, boolean time) {
        this.setState(state, details, time, -1, -1);
    }

    public void setState(String state, String details, boolean time, int players, int maxPlayers) {
        if (time) {
            if (this.time == -1L) {
                this.time = System.currentTimeMillis() / 1000L;
            }

        } else {
            this.resetTime();
        }

        if (players > -1) {
        }

    }

    public void onJoin() {
        this.resetTime();
        this.THREAD_POOL.repeatUntilComplete(this::updateGameStateRichPresence, 15L, TimeUnit.SECONDS);
    }

    public void resetTime() {
        this.time = -1L;
    }

    private boolean updateGameStateRichPresence() {
        IntegratedServer sp = Minecraft.func_71410_x().func_71401_C();
        ServerData mp = Minecraft.func_71410_x().func_147104_D();
        if (sp == null && mp == null) {
            return true;
        } else {
            if ((sp == null || !sp.func_71344_c()) && (mp == null || !mp.func_181041_d())) {
                if (sp != null && mp == null) {
                    ClientProxy.getRichPresence().setState("Catching them all!", "Singleplayer (%v)", true, ClientStorageManager.pokedex.countCaught(), Pokedex.pokedexSize);
                } else {
                    int players;
                    int max;
                    try {
                        String status = TextFormatting.func_110646_a(mp.field_78846_c.getString());
                        String[] split = status.split("/");
                        players = Integer.parseInt(split[0]);
                        max = Integer.parseInt(split[1]);
                    } catch (Exception var7) {
                        players = 0;
                        max = 0;
                    }

                    ClientProxy.getRichPresence().setState("Playing on %s".replace("%s", mp.field_78847_a), "Multiplayer (%v)", true, players, max);
                }
            } else {
                ClientProxy.getRichPresence().setState("Catching them all!", "Local Multiplayer (%v)", true, ClientStorageManager.pokedex.countCaught(), Pokedex.pokedexSize);
            }

            return false;
        }
    }

    public void poll() {

    }

    public void destruct() {
        this.active = false;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isIniting() {
        return this.available && this.initing;
    }
}
