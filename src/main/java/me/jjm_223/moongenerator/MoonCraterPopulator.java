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
            int radiusBase;
            double radiusX, radiusY, radiusZ;

            if (random.nextInt(100) <= BIG_CRATER_CHANCE)
            {
                radiusBase = random.nextInt(BIG_CRATER_SIZE - MIN_CRATER_SIZE + 1) + MIN_CRATER_SIZE;
                radiusX = radiusBase + random.nextInt(MAX_RADIUS_VARIANCE);
                // Craters on the moon are flatter than they are wide.
                radiusY = (int) (radiusBase * 0.7);
                radiusZ = radiusBase + random.nextInt(MAX_RADIUS_VARIANCE);
            }
            else
            {
                radiusBase = random.nextInt(SMALL_CRATER_SIZE - MIN_CRATER_SIZE + 1) + MIN_CRATER_SIZE;
                radiusX = radiusBase + random.nextInt(MAX_RADIUS_VARIANCE);
                radiusY = (int) (radiusBase * 0.7);
                radiusZ = radiusBase + random.nextInt(MAX_RADIUS_VARIANCE);
            }

            // Add 0.5 to the radius to improve the shape of the ellipsoid.
            radiusX += 0.5;
            radiusY += 0.5;
            radiusZ += 0.5;

            // Create inverse radii squared. Doing the calculations back here and using the inverse for optimization.
            double irx = 1.0 / (radiusX * radiusX);
            double iry = 1.0 / (radiusY * radiusY);
            double irz = 1.0 / (radiusZ * radiusZ);

            // Round the radii up to iterate through them all.
            int radXCeil = (int) Math.ceil(radiusX);
            int radYCeil = (int) Math.ceil(radiusY);
            int radZCeil = (int) Math.ceil(radiusZ);

            for (int cx = -radXCeil; cx < radXCeil; cx++)
            {
                for (int cy = -radYCeil; cy < radYCeil; cy++)
                {
                    for (int cz = -radZCeil; cz < radZCeil; cz++)
                    {
                        // Test if block is within the sphere.
                        if (validBlock(cx, cy, cz, irx, iry, irz))
                        {
                            // Skip some blocks to add imperfections to the crater.
                            if (!(outerBlock(cx, cy, cz, irx, iry, irz)
                                    && random.nextInt(100) <= IGNORE_OUTER_BLOCK_CHANCE))
                            {
                                world.getBlockAt(center.clone().add(new Vector(cx, cy, cz)).toLocation(world)).setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Tests if coordinate is within an ellipsoid.
     *
     * @param x x coordinate to test
     * @param y y coordinate to test
     * @param z z coordinate to test
     * @param irx inverse of the x radius squared
     * @param iry inverse of the y radius squared
     * @param irz inverse of the z radius squared
     * @return true if block is within the ellipsoid
     */
    private boolean validBlock(int x, int y, int z, double irx, double iry, double irz)
    {
        double result = ((x * x) * irx) + ((y * y) * iry) + ((z * z) * irz);

        return result < 1;
    }

    /**
     * Tests if coordinate is on the outer edge of an ellipsoid.
     *
     * @param x x coordinate to test
     * @param y y coordinate to test
     * @param z z coordinate to test
     * @param irx inverse of the x radius squared
     * @param iry inverse of the y radius squared
     * @param irz inverse of the z radius squared
     * @return true if block is on the outer edge of the ellipsoid.
     */
    private boolean outerBlock(int x, int y, int z, double irx, double iry, double irz)
    {
        double result = ((x * x) * irx) + ((y * y) * iry) + ((z * z) * irz);

        return Math.abs(1 - result) < 0.1;
    }
}
