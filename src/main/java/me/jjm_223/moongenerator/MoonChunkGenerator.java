package me.jjm_223.moongenerator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MoonChunkGenerator extends ChunkGenerator
{
    private NoiseGenerator generator;

    private NoiseGenerator getGenerator(World world)
    {
        if (generator == null)
        {
            generator = new SimplexNoiseGenerator(world);
        }

        return generator;
    }

    private int getHeight(World world, double x, double y, int variance)
    {
        NoiseGenerator gen = getGenerator(world);

        double result = gen.noise(x, y) * variance;
        return NoiseGenerator.floor(result);
    }

    public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int cx, int cz, ChunkGenerator.BiomeGrid biomes)
    {
        ChunkGenerator.ChunkData data = createChunkData(world);

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                int height = getHeight(world, (cx + x * 0.0625) * 0.25, (cz + z * 0.0625) * 0.25, 5) + 60;
                for (int y = 0; y < height; y++)
                {
                    data.setBlock(x, y, z, Material.CLAY);
                }
                biomes.setBiome(x, z, Biome.PLAINS);
            }
        }

        return data;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world)
    {
        return Collections.<BlockPopulator>singletonList(new MoonCraterPopulator());
    }
}
