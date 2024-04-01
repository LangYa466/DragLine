package me.nelly.hackerdetector;

import net.minecraft.entity.player.EntityPlayer;

public abstract class Detection {

    public String name;
    public Category type;
    public long lastViolated;

    public Detection(String name, Category type) {
        this.name = name;
        this.type = type;
    }

    public abstract boolean runCheck(EntityPlayer player);
}
