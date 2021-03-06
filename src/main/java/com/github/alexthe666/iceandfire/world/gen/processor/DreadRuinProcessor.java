package com.github.alexthe666.iceandfire.world.gen.processor;

import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraftforge.registries.ForgeRegistries;

public class DreadRuinProcessor extends StructureProcessor {

    public static final ResourceLocation DREAD_CHEST_LOOT = new ResourceLocation("iceandfire", "chest/mausoleum_chest");
    private float integrity = 1.0F;

    public DreadRuinProcessor() {
    }

    public static BlockState getRandomCrackedBlock(@Nullable BlockState prev, Random random) {
        float rand = random.nextFloat();
        if (rand < 0.5) {
            return IafBlockRegistry.DREAD_STONE_BRICKS.getDefaultState();
        } else if (rand < 0.9) {
            return IafBlockRegistry.DREAD_STONE_BRICKS_CRACKED.getDefaultState();
        } else {
            return IafBlockRegistry.DREAD_STONE_BRICKS_MOSSY.getDefaultState();
        }
    }

    public Template.BlockInfo func_230386_a_(IWorldReader worldReader, BlockPos pos, BlockPos pos2, Template.BlockInfo infoIn1, Template.BlockInfo infoIn2, PlacementSettings settings) {
        Random random = settings.getRandom(infoIn2.pos);
        if (random.nextFloat() <= integrity) {
            if (infoIn2.state.getBlock() == IafBlockRegistry.DREAD_STONE_BRICKS) {
                BlockState state = getRandomCrackedBlock(null, random);
                return new Template.BlockInfo(infoIn2.pos, state, null);
            }
            if (infoIn2.state.getBlock() instanceof AbstractChestBlock) {
                ResourceLocation loot = DREAD_CHEST_LOOT;
                CompoundNBT tag = new CompoundNBT();
                tag.putString("LootTable", loot.toString());
                tag.putLong("LootTableSeed", random.nextLong());
                Template.BlockInfo newInfo = new Template.BlockInfo(infoIn2.pos, Blocks.CHEST.getDefaultState(), tag);
                return newInfo;
            }
            if (infoIn2.state.getBlock() == IafBlockRegistry.DREAD_SPAWNER) {
                CompoundNBT tag = new CompoundNBT();
                CompoundNBT spawnData = new CompoundNBT();
                ResourceLocation spawnerMobId = ForgeRegistries.ENTITIES.getKey(getRandomMobForMobSpawner(random));
                if (spawnerMobId != null) {
                    spawnData.putString("id", spawnerMobId.toString());
                    tag.remove("SpawnPotentials");
                    tag.put("SpawnData", spawnData.copy());
                }
                Template.BlockInfo newInfo = new Template.BlockInfo(infoIn2.pos, IafBlockRegistry.DREAD_SPAWNER.getDefaultState(), tag);
                return newInfo;

            }
            return infoIn2;
        }
        return infoIn2;
    }


   /* @Nullable
    @Override
    public Template.BlockInfo process(IWorldReader worldIn, BlockPos pos, Template.BlockInfo blockInfoIn, Template.BlockInfo blockInfoIn2, PlacementSettings settings) {
        Random random = settings.getRandom(pos);
        if (random.nextFloat() <= integrity) {
            if (blockInfoIn.state.getBlock() == IafBlockRegistry.DREAD_STONE_BRICKS) {
                BlockState state = getRandomCrackedBlock(null, random);
                return new Template.BlockInfo(pos, state, null);
            }
            if (blockInfoIn.state.getBlock() instanceof AbstractChestBlock) {
                ResourceLocation loot = DREAD_CHEST_LOOT;
                CompoundNBT tag = new CompoundNBT();
                tag.putString("LootTable", loot.toString());
                tag.putLong("LootTableSeed", random.nextLong());
                Template.BlockInfo newInfo = new Template.BlockInfo(pos, Blocks.CHEST.getDefaultState(), tag);
                return newInfo;
            }
            if (blockInfoIn.state.getBlock() == IafBlockRegistry.DREAD_SPAWNER) {
                CompoundNBT tag = new CompoundNBT();
                CompoundNBT spawnData = new CompoundNBT();
                ResourceLocation spawnerMobId = ForgeRegistries.ENTITIES.getKey(getRandomMobForMobSpawner(random));
                if (spawnerMobId != null) {
                    spawnData.putString("id", spawnerMobId.toString());
                    tag.remove("SpawnPotentials");
                    tag.put("SpawnData", spawnData.copy());
                }
                Template.BlockInfo newInfo = new Template.BlockInfo(pos, IafBlockRegistry.DREAD_SPAWNER.getDefaultState(), tag);
                return newInfo;

            }
            return blockInfoIn;
        }
        return blockInfoIn;

    }*/

    @Override
    protected IStructureProcessorType getType() {
        return IStructureProcessorType.BLOCK_ROT;
    }

    private EntityType getRandomMobForMobSpawner(Random random) {
        float rand = random.nextFloat();
        if (rand < 0.3D) {
            return IafEntityRegistry.DREAD_THRALL;
        } else if (rand < 0.5D) {
            return IafEntityRegistry.DREAD_GHOUL;
        } else if (rand < 0.7D) {
            return IafEntityRegistry.DREAD_BEAST;
        } else if (rand < 0.85D) {
            return IafEntityRegistry.DREAD_SCUTTLER;
        }
        return IafEntityRegistry.DREAD_KNIGHT;
    }
}
