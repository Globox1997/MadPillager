package net.madpillager.entity;

import java.util.EnumSet;

import net.madpillager.init.ConfigInit;
import net.madpillager.network.MadPillagerServerPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class MadPillagerEntity extends IllagerEntity implements InventoryOwner {
    private static final TrackedData<Boolean> RUNNING = DataTracker.registerData(MadPillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final SimpleInventory inventory = new SimpleInventory(5);
    private MadTntEntity tntEntity = null;
    private int cooldown;

    public MadPillagerEntity(EntityType<? extends MadPillagerEntity> entityType, World world) {
        super((EntityType<? extends IllagerEntity>) entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new RunAttack(this));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 5.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>((MobEntity) this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>((MobEntity) this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>((MobEntity) this, IronGolemEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createMadPillagerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f).add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RUNNING, false);
    }

    @Override
    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return false;
    }

    public boolean isRunning() {
        return this.dataTracker.get(RUNNING);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        NbtList nbtList = new NbtList();
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.getStack(i);
            if (itemStack.isEmpty())
                continue;
            nbtList.add(itemStack.writeNbt(new NbtCompound()));
        }
        nbt.put("Inventory", nbtList);
    }

    @Override
    public IllagerEntity.State getState() {
        return IllagerEntity.State.NEUTRAL;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        NbtList nbtList = nbt.getList("Inventory", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); ++i) {
            ItemStack itemStack = ItemStack.fromNbt(nbtList.getCompound(i));
            if (itemStack.isEmpty())
                continue;
            this.inventory.addStack(itemStack);
        }
        this.setCanPickUpLoot(true);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0f;
    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean canLead() {
        return false;
    }

    @Override
    public void tick() {
        if (!world.isClient) {
            if (!this.getMainHandStack().isOf(Items.TNT)) {
                cooldown++;
                if (cooldown >= ConfigInit.CONFIG.cooldown) {
                    this.cooldown = 0;
                    this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.TNT));
                }
            } else if (this.tntEntity == null)
                spawnTnt();
        }
        super.tick();
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.TNT));
    }

    @Override
    protected void enchantMainHandItem(Random random, float power) {
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (super.isTeammate(other)) {
            return true;
        }
        if (other instanceof LivingEntity && ((LivingEntity) other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PILLAGER_HURT;
    }

    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }

    @Override
    protected void loot(ItemEntity item) {
        super.loot(item);
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        int i = mappedIndex - 300;
        if (i >= 0 && i < this.inventory.size()) {
            return StackReference.of(this.inventory, i);
        }
        return super.getStackReference(mappedIndex);
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
    }

    @Override
    public double getMountedHeightOffset() {
        return this.getHeight() * 0.95f;
    }

    private void spawnTnt() {
        this.tntEntity = new MadTntEntity(world, (double) this.getX(), this.getY() + this.getHeight() * 0.95f, (double) this.getZ(), this, false);
        world.spawnEntity(this.tntEntity);
        this.tntEntity.startRiding(this, true);
    }

    private void primeTnt() {
        if (this.tntEntity != null) {
            this.tntEntity.setIgnited(true);
            if (!this.isSilent())
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
            MadPillagerServerPacket.writeS2CMadTntIgnitePacket((ServerWorld) this.world, this.getId(), this.tntEntity.getId());
        }
    }

    private class RunAttack extends Goal {

        private final MadPillagerEntity madPillagerEntity;
        private LivingEntity target;

        public RunAttack(MadPillagerEntity madPillagerEntity) {
            this.madPillagerEntity = madPillagerEntity;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.madPillagerEntity.getTarget();
            if (livingEntity == null)
                return false;
            if (!this.madPillagerEntity.getMainHandStack().isOf(Items.TNT))
                return false;

            this.target = livingEntity;
            return true;
        }

        @Override
        public boolean shouldContinue() {
            if (!this.target.isAlive()) {
                return false;
            }
            if (this.madPillagerEntity.squaredDistanceTo(this.target) > 225.0) {
                return false;
            }
            if (!this.madPillagerEntity.getMainHandStack().isOf(Items.TNT)) {
                return false;
            }
            return !this.madPillagerEntity.getNavigation().isIdle() || this.canStart();
        }

        @Override
        public void stop() {
            this.target = null;
            this.madPillagerEntity.getNavigation().stop();
        }

        @Override
        public void start() {
            if (this.target != null)
                this.madPillagerEntity.primeTnt();

        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            this.madPillagerEntity.getLookControl().lookAt(this.target, 30.0f, 30.0f);
            this.madPillagerEntity.getNavigation().startMovingTo(this.target, 1.0f);
        }

    }
}
