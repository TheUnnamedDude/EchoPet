package com.github.dsh105.echopet.entity.pet.enderman;

import com.github.dsh105.echopet.EchoPet;
import com.github.dsh105.echopet.util.Particle;
import net.minecraft.server.v1_6_R3.World;

import com.github.dsh105.echopet.entity.pet.EntityPet;
import com.github.dsh105.echopet.entity.pet.Pet;
import com.github.dsh105.echopet.entity.pet.SizeCategory;

public class EntityEndermanPet extends EntityPet {

	public EntityEndermanPet(World world) {
		super(world);
	}

	public EntityEndermanPet(World world, Pet pet) {
		super(world, pet);
		this.a(0.6F, 0.9F);
		this.fireProof = true;
	}
	
	public void setScreaming(boolean flag) {
		this.datawatcher.watch(18, Byte.valueOf((byte) (flag ? 1 : 0)));
		((EndermanPet) pet).scream = flag;
	}
	
	//Obfuscated...
	
	protected void a() {
        super.a();
        this.datawatcher.a(16, new Byte((byte) 0));
        this.datawatcher.a(17, new Byte((byte) 0));
        this.datawatcher.a(18, new Byte((byte) 0));
    }
	
	@Override
	protected String getIdleSound() {
		return this.bT() ? "mob.endermen.scream" : "mob.endermen.idle";
    }
	public boolean bT() { //1.5.2 - q()
        return this.datawatcher.getByte(18) > 0;
    }
	
	@Override
	protected String getDeathSound() {
		return "mob.enderman.death";
	}
	
	@Override
	public SizeCategory getSizeCategory() {
		return SizeCategory.REGULAR;
	}

	@Override
	public void l_() {
		super.l_();
		if (this.random.nextBoolean() && particle <= 0 && !this.isInvisible()) {
			try {
				Particle.PORTAL.sendToLocation(pet.getLocation());
			} catch (Exception e) {
				EchoPet.getPluginInstance().debug(e, "Particle effect failed.");
			}
		}
	}
}
