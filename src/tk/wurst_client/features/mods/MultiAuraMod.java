/*
 * Copyright � 2014 - 2016 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.features.mods;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.features.Feature;
import tk.wurst_client.settings.CheckboxSetting;
import tk.wurst_client.settings.SliderSetting;
import tk.wurst_client.settings.SliderSetting.ValueDisplay;
import tk.wurst_client.utils.EntityUtils;
import tk.wurst_client.utils.EntityUtils.TargetSettings;

@Mod.Info(
	description = "Faster Killaura that attacks multiple entities at once.",
	name = "MultiAura",
	tags = "ForceField, multi aura, force field",
	help = "Mods/MultiAura")
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false)
public class MultiAuraMod extends Mod implements UpdateListener
{
	public CheckboxSetting useKillaura =
		new CheckboxSetting("Use Killaura settings", false)
		{
			@Override
			public void update()
			{
				if(isChecked())
				{
					KillauraMod killaura = wurst.mods.killauraMod;
					useCooldown.lock(killaura.useCooldown.isChecked());
					speed.lockToValue(killaura.speed.getValue());
					range.lockToValue(killaura.range.getValue());
					fov.lockToValue(killaura.fov.getValue());
					hitThroughWalls.lock(killaura.hitThroughWalls.isChecked());
				}else
				{
					useCooldown.unlock();
					speed.unlock();
					range.unlock();
					fov.unlock();
					hitThroughWalls.unlock();
				}
			};
		};
	public CheckboxSetting useCooldown =
		new CheckboxSetting("Use Attack Cooldown as Speed", true)
		{
			@Override
			public void update()
			{
				speed.setDisabled(isChecked());
			};
		};
	public SliderSetting speed =
		new SliderSetting("Speed", 20, 0.1, 20, 0.1, ValueDisplay.DECIMAL);
	public SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	public SliderSetting fov =
		new SliderSetting("FOV", 360, 30, 360, 10, ValueDisplay.DEGREES);
	public CheckboxSetting hitThroughWalls =
		new CheckboxSetting("Hit through walls", true);
	
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetBehindWalls()
		{
			return hitThroughWalls.isChecked();
		}
		
		@Override
		public float getRange()
		{
			return range.getValueF();
		}
		
		@Override
		public float getFOV()
		{
			return fov.getValueF();
		}
	};
	
	@Override
	public void initSettings()
	{
		settings.add(useKillaura);
		settings.add(useCooldown);
		settings.add(speed);
		settings.add(range);
		settings.add(fov);
		settings.add(hitThroughWalls);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.targetSpf, wurst.mods.killauraMod,
			wurst.mods.killauraLegitMod, wurst.mods.clickAuraMod,
			wurst.mods.triggerBotMod};
	}
	
	@Override
	public void onEnable()
	{
		// TODO: Clean up this mess!
		if(wurst.mods.killauraMod.isEnabled())
			wurst.mods.killauraMod.setEnabled(false);
		if(wurst.mods.killauraLegitMod.isEnabled())
			wurst.mods.killauraLegitMod.setEnabled(false);
		if(wurst.mods.clickAuraMod.isEnabled())
			wurst.mods.clickAuraMod.setEnabled(false);
		if(wurst.mods.tpAuraMod.isEnabled())
			wurst.mods.tpAuraMod.setEnabled(false);
		if(wurst.mods.triggerBotMod.isEnabled())
			wurst.mods.triggerBotMod.setEnabled(false);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		EntityUtils.lookChanged = false;
	}
	
	@Override
	public void onUpdate()
	{
		// update timer
		updateMS();
		
		// check timer / cooldown
		if(useCooldown.isChecked() ? mc.player.getCooledAttackStrength(0F) < 1F
			: !hasTimePassedS(speed.getValueF()))
			return;
		
		// get entities
		ArrayList<Entity> entities =
			EntityUtils.getValidEntities(targetSettings);
		
		// head rotation
		EntityUtils.lookChanged = !entities.isEmpty();
		if(!EntityUtils.lookChanged)
			return;
		
		// AutoSword
		if(wurst.mods.autoSwordMod.isActive())
			AutoSwordMod.setSlot();
		
		// Criticals
		wurst.mods.criticalsMod.doCritical();
		
		// BlockHit
		wurst.mods.blockHitMod.doBlock();
		
		// attack entities
		for(Entity entity : entities)
		{
			EntityUtils.faceEntityPacket(entity);
			mc.playerController.attackEntity(mc.player, entity);
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}
		
		// reset timer
		updateLastMS();
	}
}
