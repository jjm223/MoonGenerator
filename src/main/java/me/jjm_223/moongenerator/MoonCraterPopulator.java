package me.jjm_223.moongenerator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.Random;

public class MoonCraterPopulator extends BlockPopulator
{
    private static final int CRATER_CHANCE = 30;
    private static final int MIN_CRATER_SIZE = 5;
    private static final int SMALL_CRATER_SIZE = 8;
    private static final int BIG_CRATER_SIZE = 16;
    private static final int BIG_CRATER_CHANCE = 10;
    private static final int MAX_RADIUS_VARIANCE = 2;
    private static final int IGNORE_OUTER_BLOCK_CHANCE = 4;

    public void populate(World world, Random random, Chunk chunk)
    {
        if (random.nextInt(100) <= CRATER_CHANCE)
        {
            // Bit shifting left by 4 gets the block coordinate of a chunk. (multiply by 16)
            int centerX = (chunk.getX() << 4) + random.nextInt(16);
            int centerZ = (chunk.getZ() << 4) + random.nextInt(16);
            int centerY = world.getHighestBlockYAt(centerX, centerZ);
            Vector center = new BlockVector(centerX, centerY, centerZ);

            // Use 3 radii to make craters more irregular.
            int radiusBase, radiusX, radiusY, radiusZ;

            if (random.nextInt(100) <= BIG_CRATER_CHANCE)
            {
                radiusBase = random.nextInt(BIG_CRATER_SIZE - MIN_CRATER_SIZE + 1) + MIN_CRATER_SIZE;
                radiusX = radiusBase + random.nextInt(MAX_RADIUS_VARIANCE);
                radiusY = radiusBase + random.nextInt(MAX_RADIUS_VARIANCE);
                radiusZ = radiusBase + random.nextInt(MAX_RADIUS_VARIANCE);
            }
            else
            {
                radiusBase = random.nextInt(SMALL_CRATER_SIZE - MIN_CRATER_SIZE + 1) + MIN_CRATER_SIZE;
                radiusX = radiusBase + random.nextInt(MAX_RADIUS_VARIANCE);
                radiusY = radiusBase + random.nextInt(MAX_RADIUS_VARIANCE);
                radiusZ = radiusBase + random.nextInt(MAX_RADIUS_VARIANCE);
            }

            double maxRadius = Math.max(radiusX, Math.max(radiusY, radiusZ)) + 0.5;

            for (int x = -radiusX; x <= radiusX; x++)
            {
                for (int y = -radiusY; y <= radiusY; y++)
                {
                    for (int z = -radiusZ; z <= radiusZ; z++)
                    {
                        Vector position = center.clone().add(new Vector(x, y, z));

                        if (center.distance(position) <= maxRadius)
                        {
                            // Exclude some outer blocks to add imperfections to the crater.
                            if (!(maxRadius - center.distance(position) < 0.5 /* increasing results in more wild imperfections */
                                    && random.nextInt(100) <= IGNORE_OUTER_BLOCK_CHANCE))
                            {
                                world.getBlockAt(position.toLocation(world)).setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }
    }
}
