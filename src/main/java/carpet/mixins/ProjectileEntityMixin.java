package carpet.mixins;

import carpet.logging.LoggerRegistry;
import carpet.logging.logHelpers.TrajectoryLogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity
{
    private TrajectoryLogHelper logHelper;
    public ProjectileEntityMixin(EntityType<?> entityType_1, World world_1) { super(entityType_1, world_1); }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("RETURN"))
    private void addLogger(EntityType<? extends ProjectileEntity> entityType_1, World world_1, CallbackInfo ci)
    {
        if (LoggerRegistry.__projectiles && !world_1.isClient)
            logHelper = new TrajectoryLogHelper("projectiles");
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickCheck(CallbackInfo ci)
    {
        if (LoggerRegistry.__projectiles && logHelper != null)
            logHelper.onTick(getX(), getY(), getZ(), getVelocity());
    }

    // todo should be moved on one place this is acceessed from
    @Inject(method = "onEntityHit", at = @At("RETURN"))
    private void removeOnEntity(EntityHitResult entityHitResult, CallbackInfo ci)
    {
        if (LoggerRegistry.__projectiles && logHelper != null)
        {
            logHelper.onFinish();
            logHelper = null;
        }
    }

    @Inject(method = "onBlockHit", at = @At("RETURN"))
    private void removeOnBlock(BlockHitResult blockHitResult, CallbackInfo ci)
    {
        if (LoggerRegistry.__projectiles && logHelper != null)
        {
            logHelper.onFinish();
            logHelper = null;
        }
    }
}
