package me.jjm_223.moongenerator;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class MoonGenPlugin extends JavaPlugin
{
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
    {
        return new MoonChunkGenerator();
    }
}
