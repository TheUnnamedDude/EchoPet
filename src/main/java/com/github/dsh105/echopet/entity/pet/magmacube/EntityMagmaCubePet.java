package com.github.dsh105.echopet.entity.pet.magmacube;

import com.github.dsh105.echopet.EchoPet;
import com.github.dsh105.echopet.util.Particle;
import net.minecraft.server.v1_6_R3.World;

import com.github.dsh105.echopet.entity.pet.EntityPet;
import com.github.dsh105.echopet.entity.pet.Pet;
import com.github.dsh105.echopet.entity.pet.SizeCategory;

public class EntityMagmaCubePet extends EntityPet {
	
	int jumpDelay;

	public EntityMagmaCubePet(World world) {
		super(world);
	}

	public EntityMagmaCubePet(World world, Pet pet) {
		super(world, pet);
		this.fireProof = true;
		int i = 1 << this.random.nextInt(3);
		this.height = 0.0F;
		this.jumpDelay = this.random.nextInt(15) + 10;
		this.setSize(i);
		this.aO = 0.2F;
	}
	
	/*@Override
	public int ax() {
		int i = this.getSize();
		return i * i;
	}*/
	
	protected void setSize(int i) {
		this.datawatcher.watch(16, new Byte((byte) i));
        this.a(0.6F * (float) i, 0.6F * (float) i);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.setHealth(this.getMaxHealth());
        //this.be = i; //removed 1.5.2
        ((MagmaCubePet) pet).size = i;
	}

	public int getSize() {
		return this.datawatcher.getByte(16);
	}

	protected void a() {
		super.a();
		this.datawatcher.a(16, new Byte((byte) 1));
	}

	@Override
	protected String getIdleSound() {
		return "";
	}

	protected String getDeathSound() {
		return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
	}
	
	public void l_() {
		super.l_();
		
		if (this.onGround && this.jumpDelay-- <= 0) {
			this.jumpDelay = this.bL();
			if (this.bO()) {
                // 1.6.4 aZ() -> ba()
				this.makeSound(this.aO(), this.ba(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
			}
			getControllerJump().a();
		}

		if (this.random.nextBoolean() && particle <= 0 && !this.isInvisible()) {
			try {
				Particle.FIRE.sendToLocation(pet.getLocation());
			} catch (Exception e) {
				EchoPet.getPluginInstance().debug(e, "Particle effect failed.");
			}
		}
	}

	//q() - 1.5.2
	public boolean bO() {
		return this.getSize() > 0;
	}

	//j() - 1.5.2
	//bH() - 1.6.2
	protected int bL() {
		return this.random.nextInt(15) + 10;
	}

    // 1.6.4 aZ() > ba()
	@Override
	protected float ba() {
		return 0.4F * (float) this.getSize();
	}
	
	@Override
	public SizeCategory getSizeCategory() {
		if (this.getSize() == 1) {
			return SizeCategory.TINY;
		}
		else if (this.getSize() == 4) {
			return SizeCategory.LARGE;
		}
		else {
			return SizeCategory.REGULAR;
		}
	}
}