package com.modderg.tameablebeasts.entities;

import com.modderg.tameablebeasts.block.ScarecrowBlock;
import com.modderg.tameablebeasts.config.ModCommonConfigs;
import com.modderg.tameablebeasts.core.TameableGAnimal;
import com.modderg.tameablebeasts.core.goals.AvoidBlockGoal;
import com.modderg.tameablebeasts.core.goals.TameablePanicGoal;
import com.modderg.tameablebeasts.init.ModEntityClass;
import com.modderg.tameablebeasts.init.SoundInit;
import com.modderg.tameablebeasts.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class ChikoteEntity extends TameableGAnimal implements GeoEntity, ItemSteerable{

    private static final EntityDataAccessor<Boolean> SADDLE = SynchedEntityData.defineId(ChikoteEntity.class, EntityDataSerializers.BOOLEAN);
    public void setSaddle(boolean i){
        this.getEntityData().set(SADDLE, i);
    }
    public boolean getSaddle(){
        return this.getEntityData().get(SADDLE);
    }

    private static final EntityDataAccessor<Integer> TEXTUREID = SynchedEntityData.defineId(ChikoteEntity.class, EntityDataSerializers.INT);
    public void setTexture(int i){
        this.getEntityData().set(TEXTUREID, i);
    }
    public int getTextureID(){
        return this.getEntityData().get(TEXTUREID);
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TEXTUREID", this.getTextureID());
        compound.putBoolean("SADDLE", this.getSaddle());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTUREID, 0);
        this.entityData.define(SADDLE, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("TEXTUREID")) {
            this.setTexture(compound.getInt("TEXTUREID"));
            updateAttributes();
        }
        if (compound.contains("SADDLE")) {
            this.setSaddle(compound.getBoolean("SADDLE"));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType p_146748_, @org.jetbrains.annotations.Nullable SpawnGroupData p_146749_, @org.jetbrains.annotations.Nullable CompoundTag p_146750_) {
        updateAttributes();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.generateRandomMaxHealth(p_146746_.getRandom()));
        this.setTexture(this.random.nextInt(5));
        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    public ChikoteEntity(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.BEETROOT), false));
        this.goalSelector.addGoal(4, new AvoidBlockGoal<>(this, ScarecrowBlock.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, ScarecrowAllayEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new RaidGardenGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new TameablePanicGoal(this, 1.25D));
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(8, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new FollowParentGoalIfNotSitting(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(11, new ChikoteRandomLookAroundGoal(this, this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        if (this.isTame() && isOwnedBy(player)){
            if (player.isShiftKeyDown()) {
                this.setOrderedToSit(!this.isOrderedToSit());
                return InteractionResult.CONSUME;
            }

            if (this.getSaddle() && !this.isInSittingPose()){
                player.startRiding(this);
                return InteractionResult.sidedSuccess(this.getLevel().isClientSide);
            }

            if (itemstack.is(ItemInit.CHIKOTE_SADDLE.get()) && !this.isBaby() && !this.getSaddle()) {
                setSaddle(true);
                this.playSound(SoundEvents.HORSE_SADDLE, 0.15F, 1.0F);
                itemstack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }

        if (itemstack.is(Items.POTATO) && !this.isTame()) {
            tameGAnimal(player, itemstack, 10);
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public @org.jetbrains.annotations.Nullable AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        ChikoteEntity chikote = ModEntityClass.TAMEABLE_CHIKOTE.get().create(p_146743_);
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            chikote.setOwnerUUID(uuid);
            chikote.setTame(true);
        }
        chikote.setTexture(this.getTextureID());
        return chikote;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.BEETROOT);
    }

    //spawn and death

    private void updateAttributes(){
        if (this.isBaby()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.15D);
        } else {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        }
    }

    protected float generateRandomMaxHealth(RandomSource p_218806_) {
        return 15.0F + (float)p_218806_.nextInt(8) + (float)p_218806_.nextInt(9);
    }

    //ride stuff

    @Override
    public void travel(Vec3 pos) {
        if (this.isAlive()) {
            if (this.canBeControlledByRider()) {
                LivingEntity passenger = (LivingEntity)getControllingPassenger();
                this.yRotO = getYRot();
                this.xRotO = getXRot();

                this.m_274367_(1.0f);

                setYRot(passenger.getYRot());
                setXRot(passenger.getXRot() * 0.5f);
                setRot(getYRot(), getXRot());

                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float x = passenger.xxa * 0.5F;
                float z = passenger.zza;

                if (z <= 0)
                    z *= 0.25f;

                this.setSpeed(0.3f);
                super.travel(new Vec3(x, pos.y, z));
            } else {
                super.travel(pos);
            }
        }
    }


    @Nullable
    public LivingEntity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : (LivingEntity) this.getPassengers().get(0);
    }

    @Override
    public boolean boost() {
        return false;
    }

    public static boolean checkChikoteSpawnRules(EntityType<ChikoteEntity> p_218242_, LevelAccessor p_218243_, MobSpawnType p_218244_, BlockPos p_218245_, RandomSource p_218246_) {
        return checkAnimalSpawnRules(p_218242_,p_218243_,p_218244_,p_218245_,p_218246_) && ModCommonConfigs.CAN_SPAWN_CHIKOTE.get();
    }

    //sounds

    @Override
    public SoundEvent getAmbientSound() {
        return SoundInit.CHIKOTE_AMBIENT.get();
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundInit.CHIKOTE_DEATH.get();
    }

    @org.jetbrains.annotations.Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {return SoundInit.CHIKOTE_HURT.get();}

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(SoundInit.CHIKOTE_STEPS.get(), 0.15F, 1.0F);
    }

    @Override
    public SoundEvent getTameSound(){
        return SoundInit.CHIKOTE_HAPPY.get();
    }

    @Override
    public SoundEvent getInteractSound(){
        return SoundInit.CHIKOTE_INTERACT.get();
    }

    //animation stuff
    protected AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    public static <T extends ChikoteEntity & GeoEntity> AnimationController<T> flyController(T entity) {
        return new AnimationController<>(entity,"movement", 5, event ->{
            if(entity.isInSittingPose()){
                event.getController().setAnimation(RawAnimation.begin().then("sit", Animation.LoopType.LOOP));
            } else {
                if (entity.interact <= 0){
                    if(event.isMoving()){
                        event.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
                    } else {
                        event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
                    }
                } else {
                    event.getController().setAnimation(RawAnimation.begin().then("interact", Animation.LoopType.PLAY_ONCE));
                }
            }
            return PlayState.CONTINUE;
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(flyController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    //Goals

    public class ChikoteRandomLookAroundGoal extends RandomLookAroundGoal {

        private final ChikoteEntity chikote;

        public ChikoteRandomLookAroundGoal(Mob p_25720_, ChikoteEntity chikote) {
            super(p_25720_);
            this.chikote = chikote;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !chikote.canBeControlledByRider();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !chikote.canBeControlledByRider();
        }
    }

    static class RaidGardenGoal extends MoveToBlockGoal {
        private final ChikoteEntity TameableChikoteEntity;
        private boolean wantsToRaid;
        private boolean canRaid;

        public RaidGardenGoal(ChikoteEntity p_29782_) {
            super(p_29782_, (double)0.7F, 16);
            this.TameableChikoteEntity = p_29782_;
        }

        public boolean canUse() {
            if (this.nextStartTick <= 0) {
                if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.TameableChikoteEntity.getLevel(), this.TameableChikoteEntity)) {
                    return false;
                }
                this.canRaid = false;
                this.wantsToRaid = true;
            }

            return super.canUse();
        }

        public boolean canContinueToUse() {
            return this.canRaid && super.canContinueToUse();
        }

        public void tick() {
            super.tick();
            this.TameableChikoteEntity.getLookControl().setLookAt((double)this.blockPos.getX() + 0.5D, (double)(this.blockPos.getY() + 1), (double)this.blockPos.getZ() + 0.5D, 10.0F, (float)this.TameableChikoteEntity.getMaxHeadXRot());
            if (this.isReachedTarget()) {
                Level level = this.TameableChikoteEntity.getLevel();
                BlockPos blockpos = this.blockPos.above();
                BlockState blockstate = level.getBlockState(blockpos);
                Block block = blockstate.getBlock();

                if (this.canRaid && block instanceof BeetrootBlock) {
                    int i = blockstate.getValue(BeetrootBlock.AGE);
                    if (i == 0) {
                        level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 2);
                        level.destroyBlock(blockpos, true, this.TameableChikoteEntity);
                    } else {
                        level.setBlock(blockpos, blockstate.setValue(BeetrootBlock.AGE, Integer.valueOf(i - 1)), 2);
                        level.levelEvent(2001, blockpos, Block.getId(blockstate));
                    }
                } else if (this.canRaid && block instanceof PotatoBlock) {
                    int i = blockstate.getValue(PotatoBlock.AGE);
                    if (i == 0) {
                        level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 2);
                        level.destroyBlock(blockpos, true, this.TameableChikoteEntity);
                    } else {
                        level.setBlock(blockpos, blockstate.setValue(PotatoBlock.AGE, Integer.valueOf(i - 1)), 2);
                        level.levelEvent(2001, blockpos, Block.getId(blockstate));
                    }
                }

                this.canRaid = false;
                this.nextStartTick = 10;
            }

        }

        protected boolean isValidTarget(LevelReader p_29785_, BlockPos p_29786_) {
            BlockState blockstate = p_29785_.getBlockState(p_29786_);
            if (blockstate.is(Blocks.FARMLAND) && this.wantsToRaid) {
                blockstate = p_29785_.getBlockState(p_29786_.above());
                if (blockstate.getBlock() instanceof BeetrootBlock && ((BeetrootBlock)blockstate.getBlock()).isMaxAge(blockstate)) {
                    this.canRaid = true;
                    return true;
                } else if (blockstate.getBlock() instanceof PotatoBlock && ((PotatoBlock)blockstate.getBlock()).isMaxAge(blockstate)) {
                    this.canRaid = true;
                    return true;
                }
            }

            return false;
        }
    }
}
