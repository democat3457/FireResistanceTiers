package com.eksekk.fireresistancetiers.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PlayerAttackHandler extends EntityAttackHandler
{
	public static final Class<?> PLAYER_CLASS = EntityPlayer.class;
	public static Class<?> ENTITY_CLASS = EntityLivingBase.class;
	
	public static final Method canBlockDamageSource = ObfuscationReflectionHelper.findMethod(ENTITY_CLASS, "func_184583_d", boolean.class, DamageSource.class);
	public static final Method damageShield = ObfuscationReflectionHelper.findMethod(PLAYER_CLASS, "func_184590_k", void.class, float.class);
	public static final Method blockUsingShield = ObfuscationReflectionHelper.findMethod(PLAYER_CLASS, "func_190629_c", void.class, EntityLivingBase.class);
	public static final Method damageEntity = ObfuscationReflectionHelper.findMethod(PLAYER_CLASS, "func_70665_d", void.class, DamageSource.class, float.class);
	public static final Method markVelocityChanged = ObfuscationReflectionHelper.findMethod(ENTITY_CLASS, "func_70018_K", void.class);
	public static final Method checkTotemDeathProtection = ObfuscationReflectionHelper.findMethod(ENTITY_CLASS, "func_190628_d", boolean.class, DamageSource.class);
	public static final Method getDeathSound = ObfuscationReflectionHelper.findMethod(PLAYER_CLASS, "func_184615_bR", SoundEvent.class);
	public static final Method getSoundVolume = ObfuscationReflectionHelper.findMethod(ENTITY_CLASS, "func_70599_aP", float.class);
	public static final Method getSoundPitch = ObfuscationReflectionHelper.findMethod(ENTITY_CLASS, "func_70647_i", float.class);
	public static final Method playHurtSound = ObfuscationReflectionHelper.findMethod(ENTITY_CLASS, "func_184581_c", void.class, DamageSource.class);
	public static final Method spawnShoulderEntities = ObfuscationReflectionHelper.findMethod(PLAYER_CLASS, "func_192030_dh", void.class);
	
	public static final Field lastDamage = ObfuscationReflectionHelper.findField(ENTITY_CLASS, "field_110153_bc");
	public static final Field recentlyHit = ObfuscationReflectionHelper.findField(ENTITY_CLASS, "field_70718_bc");
	public static final Field attackingPlayer = ObfuscationReflectionHelper.findField(ENTITY_CLASS, "field_70717_bb");
	public static final Field lastDamageSource = ObfuscationReflectionHelper.findField(ENTITY_CLASS, "field_189750_bF");
	public static final Field lastDamageStamp = ObfuscationReflectionHelper.findField(ENTITY_CLASS, "field_189751_bG");
	public static final Field idleTime = ObfuscationReflectionHelper.findField(ENTITY_CLASS, "field_70708_bq");
	
	public static boolean attackEntityFrom(EntityPlayer entity, DamageSource source, float amount) throws IllegalAccessException, InvocationTargetException
    {
        if (!net.minecraftforge.common.ForgeHooks.onPlayerAttack(entity, source, amount)) return false;
        if (entity.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (entity.capabilities.disableDamage && !source.canHarmInCreative())
        {
            return false;
        }
        else
        {
        	idleTime.set(entity, 0);

            if (entity.getHealth() <= 0.0F)
            {
                return false;
            }
            else
            {
                if (entity.isPlayerSleeping() && !entity.world.isRemote)
                {
                	entity.wakeUpPlayer(true, true, false);
                }

                spawnShoulderEntities.invoke(entity);

                if (source.isDifficultyScaled())
                {
                    if (entity.world.getDifficulty() == EnumDifficulty.PEACEFUL)
                    {
                        amount = 0.0F;
                    }

                    if (entity.world.getDifficulty() == EnumDifficulty.EASY)
                    {
                        amount = Math.min(amount / 2.0F + 1.0F, amount);
                    }

                    if (entity.world.getDifficulty() == EnumDifficulty.HARD)
                    {
                        amount = amount * 3.0F / 2.0F;
                    }
                }

                return amount == 0.0F ? false : EntityAttackHandler.attackEntityFrom(entity, source, amount);
            }
        }
    }
}
