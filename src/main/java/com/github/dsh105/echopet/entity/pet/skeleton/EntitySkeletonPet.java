package com.github.dsh105.echopet.entity.pet.skeleton;

import net.minecraft.server.v1_6_R3.Item;
import net.minecraft.server.v1_6_R3.ItemStack;
import net.minecraft.server.v1_6_R3.World;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.dsh105.echopet.EchoPet;
import com.github.dsh105.echopet.entity.pet.EntityPet;
import com.github.dsh105.echopet.entity.pet.Pet;
import com.github.dsh105.echopet.entity.pet.SizeCategory;

public class EntitySkeletonPet extends EntityPet {

	public EntitySkeletonPet(World world) {
		super(world);
	}

	public EntitySkeletonPet(World world, Pet pet) {
		super(world, pet);
		this.a(0.6F, 0.6F);
		this.fireProof = true;
		final SkeletonPet sp = (SkeletonPet) pet;
		new BukkitRunnable() {
			public void run() {
				if (sp.wither) {
					setEquipment(0, new ItemStack(Item.STONE_SWORD));
				}
				else {
					setEquipment(0, new ItemStack(Item.BOW));
				}
			}
		}.runTaskLater(EchoPet.getPluginInstance(), 5L);
	}
	
	public void setWither(boolean flag) {
		this.datawatcher.watch(13, (byte) (flag ? 1 : 0));
		((SkeletonPet) pet).wither = flag;
	}
	
	public int getSkeletonType() {
		return this.datawatcher.getByte(13);
	}

	protected void a() {
		super.a();
		this.datawatcher.a(13, new Byte((byte) 0));
	}

	@Override
	protected String getIdleSound() {
		return "mob.skeleton.say";
	}

	protected void a(int i, int j, int k, int l) {
		this.makeSound("mob.skeleton.step", 0.15F, 1.0F);
	}
	
	@Override
	protected String getDeathSound() {
		return "mob.skeleton.death";
	}
	
	@Override
	public SizeCategory getSizeCategory() {
		if (this.getSkeletonType() == 1) {
			return SizeCategory.LARGE;
		}
		else {
			return SizeCategory.REGULAR;
		}
	}
}