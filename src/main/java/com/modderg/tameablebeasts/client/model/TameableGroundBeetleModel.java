package com.modderg.tameablebeasts.client.model;

import com.modderg.tameablebeasts.TameableBeast;
import com.modderg.tameablebeasts.entities.GroundBeetleEntity;
import com.modderg.tameablebeasts.entities.ScarecrowAllayEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TameableGroundBeetleModel extends GeoModel<GroundBeetleEntity> {

    private final ResourceLocation[][] textures = {
            new ResourceLocation[]{
                    new ResourceLocation(TameableBeast.MODID, "textures/entity/ground_beetle.png"),
                    new ResourceLocation(TameableBeast.MODID, "textures/entity/ground_beetle2.png")
            },
            new ResourceLocation[]{
                    new ResourceLocation(TameableBeast.MODID, "textures/entity/tameable_beetle_baby.png")
            },
    };

    @Override
    public ResourceLocation getModelResource(GroundBeetleEntity entity) {
        if(entity.isBaby()){
            return new ResourceLocation(TameableBeast.MODID, "geo/tameable_beetle_baby.geo.json");
        }
        return new ResourceLocation(TameableBeast.MODID, "geo/ground_beetle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GroundBeetleEntity entity) {
        if(entity.isBaby()){
            return textures[1][0];
        }
        return textures[0][entity.getTextureID()];
    }

    @Override
    public ResourceLocation getAnimationResource(GroundBeetleEntity entity) {
        if(entity.isBaby()){
            return new ResourceLocation(TameableBeast.MODID, "animations/tameable_baby_beetle_anims.json");
        }
        return new ResourceLocation(TameableBeast.MODID, "animations/ground_beetle_anims.json");
    }

    @Override
    public void setCustomAnimations(GroundBeetleEntity animatable, long instanceId, AnimationState<GroundBeetleEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
