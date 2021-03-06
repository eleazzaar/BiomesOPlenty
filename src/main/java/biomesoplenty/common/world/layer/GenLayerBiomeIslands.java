/*******************************************************************************
 * Copyright 2015-2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/

package biomesoplenty.common.world.layer;

import biomesoplenty.common.enums.BOPClimates;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerBiomeIslands extends BOPGenLayer
{
    private GenLayer climateLayer;
    private int islandChance;
    
    public GenLayerBiomeIslands(long seed, GenLayer biomesLayer, GenLayer climateLayer, int islandChance)
    {
        super(seed);
        this.parent = biomesLayer;
        this.climateLayer = climateLayer;
        this.islandChance = islandChance;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
    {
        int[] biomeIds = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] climateVals = this.climateLayer.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int z = 0; z < areaHeight; ++z)
        {
            for (int x = 0; x < areaWidth; ++x)
            {
                
                int biomeID = biomeIds[x + 1 + (z + 1) * (areaWidth + 2)];               
                this.initChunkSeed((long)(x + areaX), (long)(z + areaY));
                
                // if it's an ocean, there's a chance of creating an island here
                if ((biomeID == BiomeGenBase.ocean.biomeID || biomeID == BiomeGenBase.deepOcean.biomeID) && (this.nextInt(this.islandChance) == 0))
                {
                    // check that the tile is surrounded by ocean
                    int biomeNorth = biomeIds[x + 1 + (z + 1 - 1) * (areaWidth + 2)];
                    int biomeEast = biomeIds[x + 1 + 1 + (z + 1) * (areaWidth + 2)];
                    int biomeWest = biomeIds[x + 1 - 1 + (z + 1) * (areaWidth + 2)];
                    int biomeSouth = biomeIds[x + 1 + (z + 1 + 1) * (areaWidth + 2)];
                    if (
                        (biomeNorth == BiomeGenBase.ocean.biomeID || biomeNorth == BiomeGenBase.deepOcean.biomeID) &&
                        (biomeEast == BiomeGenBase.ocean.biomeID || biomeEast == BiomeGenBase.deepOcean.biomeID) &&
                        (biomeWest == BiomeGenBase.ocean.biomeID || biomeWest == BiomeGenBase.deepOcean.biomeID) &&
                        (biomeSouth == BiomeGenBase.ocean.biomeID || biomeSouth == BiomeGenBase.deepOcean.biomeID)
                    )
                    {
                        int climateVal = climateVals[x + z * areaWidth];
                        out[x + z * areaWidth] = BOPClimates.lookup(climateVal).getRandomLandBiome(this).biomeID;
                    }
                    else
                    {
                        out[x + z * areaWidth] = biomeID;
                    }
                }
                else
                {
                    out[x + z * areaWidth] = biomeID;
                }
            }
        }

        return out;
    }
    
}