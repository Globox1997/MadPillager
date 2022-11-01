package net.madpillager.entity;

import org.jetbrains.annotations.Nullable;

import net.madpillager.init.ConfigInit;
import net.madpillager.init.EntityInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class MadTntEntity extends TntEntity {

    public static final TrackedData<Boolean> HOLD_BY_MAD_PILLAGER = DataTracker.registerData(MadTntEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private LivingEntity igniter = null;
    private int igniterId = 0;
    private boolean ignited = false;

    public MadTntEntity(EntityType<? extends MadTntEntity> entityType, World world) {
        super(EntityInit.MAD_TNT_ENTITY, world);
    }

    public MadTntEntity(World world, double x, double y, double z, LivingEntity igniter, boolean ignited) {
        this(EntityInit.MAD_TNT_ENTITY, world);
        this.setPosition(x, y, z);
        this.setFuse(ConfigInit.CONFIG.tntStartTimer + world.random.nextInt(ConfigInit.CONFIG.randomExtraTicks));
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.igniter = igniter;
        this.ignited = ignited;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HOLD_BY_MAD_PILLAGER, false);
    }

    @Override
    public void tick() {
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }

        int i = this.getFuse();
        if (this.ignited)
            i--;
        this.setFuse(i);
        if (i <= 0) {
            if (!this.world.isClient) {
                this.discard();
                this.explode();
            }
        } else {
            this.updateWaterState();
            if (this.world.isClient) {
                this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.getCausingEntity() != null && this.getCausingEntity() instanceof MadPillagerEntity madPillagerEntity && madPillagerEntity.isAlive())
            madPillagerEntity.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);

        super.remove(reason);
    }

    @Nullable
    @Override
    public LivingEntity getCausingEntity() {
        if (this.igniterId != 0 && this.world.getEntityById(this.igniterId) != null && this.world.getEntityById(this.igniterId) instanceof LivingEntity)
            return (LivingEntity) this.world.getEntityById(this.igniterId);
        return this.igniter;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.igniter != null)
            nbt.putInt("Igniter", this.igniter.getId());
        nbt.putBoolean("Ignited", this.ignited);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Igniter"))
            this.igniterId = nbt.getInt("Igniter");
        this.ignited = nbt.getBoolean("Ignited");
    }

    public void setIgniterEntity(LivingEntity livingEntity) {
        this.igniter = livingEntity;
    }

    public void setIgnited(boolean ignited) {
        this.ignited = ignited;
    }

    public boolean isIgnited() {
        return this.ignited;
    }

    private void explode() {
        this.world.createExplosion(this, this.getX(), this.getBodyY(0.0625), this.getZ(), ConfigInit.CONFIG.explosionPower, Explosion.DestructionType.BREAK);
    }

}
