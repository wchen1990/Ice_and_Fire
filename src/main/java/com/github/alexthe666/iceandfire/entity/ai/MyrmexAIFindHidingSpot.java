package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MyrmexAIFindHidingSpot extends Goal {
    private static final int RADIUS = 32;
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super Entity> targetEntitySelector;
    private final EntityMyrmexSentinel myrmex;
    private BlockPos targetBlock = null;
    private int wanderRadius = RADIUS;

    public MyrmexAIFindHidingSpot(EntityMyrmexSentinel myrmex) {
        super();
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(myrmex);
        this.targetEntitySelector = new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity myrmex) {
                return myrmex != null && myrmex instanceof EntityMyrmexSentinel;
            }
        };
        this.myrmex = myrmex;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        this.targetBlock = getTargetPosition(wanderRadius);
        return this.myrmex.canMove() && this.myrmex.getAttackTarget() == null && myrmex.canSeeSky() && !myrmex.isHiding();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !myrmex.shouldEnterHive() && this.myrmex.getNavigator().noPath() && this.myrmex.getAttackTarget() == null  && !myrmex.isHiding();
    }

    @Override
    public void tick() {
       if(targetBlock != null){
           if (areMyrmexNear(5) || this.myrmex.isOnResin()) {
               this.myrmex.getNavigator().tryMoveToXYZ(this.targetBlock.getX() + 0.5D, this.targetBlock.getY(), this.targetBlock.getZ() + 0.5D, 1D);
               if (this.myrmex.getDistanceSq(new Vec3d(this.targetBlock)) < 20) {
                   this.wanderRadius += RADIUS;
                   this.targetBlock = getTargetPosition(wanderRadius);
               }
           } else {
               if (this.myrmex.getAttackTarget() == null && this.myrmex.getCustomer() == null && myrmex.visibleTicks == 0) {
                   myrmex.setHiding(true);
               }
           }
       }

    }

    public void resetTask() {
        this.targetBlock = null;
        wanderRadius = RADIUS;
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return this.myrmex.getBoundingBox().grow(targetDistance, 14.0D, targetDistance);
    }

    public BlockPos getTargetPosition(int radius) {
        int x = (int) myrmex.getPosX() + myrmex.getRNG().nextInt(radius * 2) - radius;
        int z = (int) myrmex.getPosZ() + myrmex.getRNG().nextInt(radius * 2) - radius;
        return myrmex.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
    }

    private boolean areMyrmexNear(double distance) {
        List<Entity> sentinels = this.myrmex.world.getEntitiesInAABBexcluding(this.myrmex, this.getTargetableArea(distance), this.targetEntitySelector);
        List<Entity> hiddenSentinels = new ArrayList<>();
        for (Entity sentinel : sentinels) {
            if (sentinel instanceof EntityMyrmexSentinel && ((EntityMyrmexSentinel) sentinel).isHiding()) {
                hiddenSentinels.add(sentinel);
            }
        }
        return !hiddenSentinels.isEmpty();
    }

}
