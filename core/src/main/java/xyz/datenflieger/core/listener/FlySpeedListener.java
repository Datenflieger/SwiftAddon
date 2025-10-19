package xyz.datenflieger.core.listener;

import net.labymod.api.Laby;
import net.labymod.api.client.entity.player.ClientPlayer;
import org.jetbrains.annotations.NotNull;
import xyz.datenflieger.core.SwiftAddon;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FlySpeedListener {
    private static final Logger LOGGER = Logger.getLogger(FlySpeedListener.class.getName());
    
    private static final int MIN_LEVEL = 0;
    private static final int MAX_LEVEL = 10;
    private static final long DISPLAY_DURATION_MS = 2000L;
    private static final float BASE_SPEED = 0.05f;
    private static final float SPEED_INCREMENT = 0.05f;
    
    private static final float[] SPEED_LEVELS = new float[MAX_LEVEL + 1];
    
    static {
        for (int i = 0; i <= MAX_LEVEL; i++) {
            SPEED_LEVELS[i] = BASE_SPEED + (i * SPEED_INCREMENT);
        }
    }
    private int currentSpeedLevel = MIN_LEVEL;
    private long lastChangeTime = 0;
    private final SwiftAddon addon;

    public FlySpeedListener(@NotNull SwiftAddon addon) {
        this.addon = addon;
    }

    public void adjustFlySpeed(double scrollDelta) {
        try {
            ClientPlayer player = Laby.labyAPI().minecraft().getClientPlayer();
            if (player == null) {
                return;
            }

            if (scrollDelta > 0) {
                this.currentSpeedLevel = Math.min(this.currentSpeedLevel + 1, MAX_LEVEL);
            } else if (scrollDelta < 0) {
                this.currentSpeedLevel = Math.max(this.currentSpeedLevel - 1, MIN_LEVEL);
            }

            this.lastChangeTime = System.currentTimeMillis();
            LOGGER.fine("Adjusted fly speed to level: " + this.currentSpeedLevel);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to adjust fly speed", e);
        }
    }
    
    public String getDisplayText() {
        if (!shouldDisplay()) {
            return null;
        }
        
        return this.currentSpeedLevel == MIN_LEVEL 
            ? "Fly Speed: Normal" 
            : String.format("Fly Speed: Level %d (%.2fx)", 
                this.currentSpeedLevel, 
                SPEED_LEVELS[this.currentSpeedLevel] / BASE_SPEED);
    }
    
    public boolean shouldDisplay() {
        return System.currentTimeMillis() - this.lastChangeTime <= DISPLAY_DURATION_MS;
    }

    public float getCurrentFlySpeed() {
        try {
            return SPEED_LEVELS[this.currentSpeedLevel];
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.warning("Invalid speed level: " + this.currentSpeedLevel);
            return SPEED_LEVELS[0]; 
        }
    }

    public int getCurrentSpeedLevel() {
        return this.currentSpeedLevel;
    }

    public void setCurrentSpeedLevel(int level) {
        this.currentSpeedLevel = Math.max(MIN_LEVEL, Math.min(level, MAX_LEVEL));
        this.lastChangeTime = System.currentTimeMillis();
        LOGGER.fine("Set fly speed to level: " + this.currentSpeedLevel);
    }
    
    public void reset() {
        setCurrentSpeedLevel(MIN_LEVEL);
    }
}
