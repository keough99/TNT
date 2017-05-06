package com.github.keough99.main;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.server.v1_11_R1.AxisAlignedBB;
import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.Blocks;
import net.minecraft.server.v1_11_R1.DamageSource;
import net.minecraft.server.v1_11_R1.EnchantmentProtection;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityFallingBlock;
import net.minecraft.server.v1_11_R1.EntityFireball;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.EntityLiving;
import net.minecraft.server.v1_11_R1.EntityTNTPrimed;
import net.minecraft.server.v1_11_R1.EnumParticle;
import net.minecraft.server.v1_11_R1.IBlockData;
import net.minecraft.server.v1_11_R1.IBlockProperties;
import net.minecraft.server.v1_11_R1.Material;
import net.minecraft.server.v1_11_R1.MathHelper;
import net.minecraft.server.v1_11_R1.Vec3D;
import net.minecraft.server.v1_11_R1.World;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.event.CraftEventFactory;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.PluginManager;

public class TNT
{
  private final boolean a;
  private final boolean b;
  private final Random c = new Random();
  private final World world;
  private final double posX;
  private final double posY;
  private final double posZ;
  public final Entity source;
  private final float size;
  private final List<BlockPosition> blocks = Lists.newArrayList();
  private final Map<EntityHuman, Vec3D> k = Maps.newHashMap();
  public boolean wasCanceled = false;
  
  public TNT(World world, Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1)
  {
    this.world = world;
    this.source = entity;
    this.size = ((float)Math.max(f, 0.0D));
    this.posX = d0;
    this.posY = d1;
    this.posZ = d2;
    this.a = flag;
    this.b = flag1;
  }
  
  public void a()
  {
    if (this.size < 0.1F) {
      return;
    }
    HashSet hashset = Sets.newHashSet();
    for (int k = 0; k < 16; k++) {
      for (int i = 0; i < 16; i++) {
        for (int j = 0; j < 16; j++) {
          if ((k == 0) || (k == 15) || (i == 0) || (i == 15) || (j == 0) || (j == 15))
          {
            double d0 = k / 15.0F * 2.0F - 1.0F;
            double d1 = i / 15.0F * 2.0F - 1.0F;
            double d2 = j / 15.0F * 2.0F - 1.0F;
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            
            d0 /= d3;
            d1 /= d3;
            d2 /= d3;
            float f = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);
            double d4 = this.posX;
            double d5 = this.posY;
            double d6 = this.posZ;
            for (; f > 0.0F; f -= 0.22500001F)
            {
              BlockPosition blockposition = new BlockPosition(d4, d5, d6);
              IBlockData iblockdata = this.world.getType(blockposition);
              if (((IBlockProperties) iblockdata.getBlock()).getMaterial() != Material.AIR)
              {
                float f2 = this.source != null ? this.source.a(this, this.world, blockposition, iblockdata) : iblockdata.getBlock().a(null);
                
                f -= (f2 + 0.3F) * 0.3F;
              }
              if ((f > 0.0F) && ((this.source == null) || (this.source.a(this, this.world, blockposition, iblockdata, f))) && (blockposition.getY() < 256) && (blockposition.getY() >= 0)) {
                hashset.add(blockposition);
              }
              d4 += d0 * 0.30000001192092896D;
              d5 += d1 * 0.30000001192092896D;
              d6 += d2 * 0.30000001192092896D;
            }
          }
        }
      }
    }
    this.blocks.addAll(hashset);
    float f3 = this.size * 2.0F;
    
    int i = MathHelper.floor(this.posX - f3 - 1.0D);
    int j = MathHelper.floor(this.posX + f3 + 1.0D);
    int l = MathHelper.floor(this.posY - f3 - 1.0D);
    int i1 = MathHelper.floor(this.posY + f3 + 1.0D);
    int j1 = MathHelper.floor(this.posZ - f3 - 1.0D);
    int k1 = MathHelper.floor(this.posZ + f3 + 1.0D);
    List list = this.world.getEntities(this.source, new AxisAlignedBB(i, l, j1, j, i1, k1));
    Vec3D vec3d = new Vec3D(this.posX, this.posY, this.posZ);
    for (int l1 = 0; l1 < list.size(); l1++)
    {
      Entity entity = (Entity)list.get(l1);
      if (!entity.aW())
      {
        double d7 = entity.f(this.posX, this.posY, this.posZ) / f3;
        if (d7 <= 1.0D)
        {
          double d8 = entity.locX - this.posX;
          double d9 = entity.locY + entity.getHeadHeight() - this.posY;
          double d10 = entity.locZ - this.posZ;
          double d11 = MathHelper.sqrt(d8 * d8 + d9 * d9 + d10 * d10);
          if (d11 != 0.0D)
          {
            d8 /= d11;
            d9 /= d11;
            d10 /= d11;
            double d12 = this.world.a(vec3d, entity.getBoundingBox());
            double d13 = (1.0D - d7) * d12;
            
            CraftEventFactory.entityDamage = this.source;
            entity.forceExplosionKnockback = false;
            boolean wasDamaged = entity.damageEntity(DamageSource.explosion(this), (int)((d13 * d13 + d13) / 2.0D * 8.0D * f3 + 1.0D));
            CraftEventFactory.entityDamage = null;
            if ((wasDamaged) || ((entity instanceof EntityTNTPrimed)) || ((entity instanceof EntityFallingBlock)) || (entity.forceExplosionKnockback))
            {
              double d14 = EnchantmentProtection.a(entity, d13);
              
              entity.motX += d8 * d14;
              entity.motY += d9 * d14;
              entity.motZ += d10 * d14;
              if (((entity instanceof EntityHuman)) && (!((EntityHuman)entity).abilities.isInvulnerable)) {
                this.k.put((EntityHuman)entity, new Vec3D(d8 * d13, d9 * d13, d10 * d13));
              }
            }
          }
        }
      }
    }
  }
  
  public void a(boolean flag)
  {
    this.world.makeSound(this.posX, this.posY, this.posZ, "random.explode", 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);
    if ((this.size >= 2.0F) && (this.b)) {
      this.world.addParticle(EnumParticle.EXPLOSION_HUGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D, new int[0]);
    } else {
      this.world.addParticle(EnumParticle.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D, new int[0]);
    }
    if (this.b)
    {
      org.bukkit.World bworld = this.world.getWorld();
      org.bukkit.entity.Entity explode = this.source == null ? null : this.source.getBukkitEntity();
      Location location = new Location(bworld, this.posX, this.posY, this.posZ);
      
      List<org.bukkit.block.Block> blockList = Lists.newArrayList();
      for (int i1 = this.blocks.size() - 1; i1 >= 0; i1--)
      {
        BlockPosition cpos = (BlockPosition)this.blocks.get(i1);
        org.bukkit.block.Block bblock = bworld.getBlockAt(cpos.getX(), cpos.getY(), cpos.getZ());
        if (bblock.getType() != org.bukkit.Material.AIR) {
          blockList.add(bblock);
        }
      }
      float yield;
      boolean cancelled;
      List<org.bukkit.block.Block> bukkitBlocks;
      float yield1;
      if (explode != null)
      {
        EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, 0.3F);
        this.world.getServer().getPluginManager().callEvent(event);
        boolean cancelled1 = event.isCancelled();
        List<org.bukkit.block.Block> bukkitBlocks1 = event.blockList();
        yield1 = event.getYield();
      }
      else
      {
        BlockExplodeEvent event = new BlockExplodeEvent(location.getBlock(), blockList, 0.3F);
        this.world.getServer().getPluginManager().callEvent(event);
        cancelled = event.isCancelled();
        bukkitBlocks = event.blockList();
        yield1 = event.getYield();
      }
      this.blocks.clear();
      for (org.bukkit.block.Block bblock : bukkitBlocks)
      {
        BlockPosition coords = new BlockPosition(bblock.getX(), bblock.getY(), bblock.getZ());
        this.blocks.add(coords);
      }
      if (cancelled)
      {
        this.wasCanceled = true;
        return;
      }
      Iterator iterator = this.blocks.iterator();
      while (iterator.hasNext())
      {
        BlockPosition blockposition = (BlockPosition)iterator.next();
        Block block = this.world.getType(blockposition).getBlock();
        
        if (flag)
        {
          double d0 = blockposition.getX() + this.world.random.nextFloat();
          double d1 = blockposition.getY() + this.world.random.nextFloat();
          double d2 = blockposition.getZ() + this.world.random.nextFloat();
          double d3 = d0 - this.posX;
          double d4 = d1 - this.posY;
          double d5 = d2 - this.posZ;
          double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
          
          d3 /= d6;
          d4 /= d6;
          d5 /= d6;
          double d7 = 0.5D / (d6 / this.size + 0.1D);
          
          d7 *= (this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F);
          d3 *= d7;
          d4 *= d7;
          d5 *= d7;
          this.world.addParticle(EnumParticle.EXPLOSION_NORMAL, (d0 + this.posX * 1.0D) / 2.0D, (d1 + this.posY * 1.0D) / 2.0D, (d2 + this.posZ * 1.0D) / 2.0D, d3, d4, d5, new int[0]);
          this.world.addParticle(EnumParticle.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
        }
        if (((IBlockProperties) block).getMaterial() != Material.AIR)
        {
          if (block.a(this)) {
            block.dropNaturally(this.world, blockposition, this.world.getType(blockposition), yield1, 0);
          }
          this.world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
          block.wasExploded(this.world, blockposition, this);
        }
      }
    }
    if (this.a)
    {
      Iterator iterator = this.blocks.iterator();
      while (iterator.hasNext())
      {
        BlockPosition blockposition = (BlockPosition)iterator.next();
        if ((((IBlockProperties) this.world.getType(blockposition).getBlock()).getMaterial() == Material.AIR) && (this.world.getType(blockposition.down()).getBlock().o()) && (this.c.nextInt(3) == 0)) {
          if (!org.bukkit.craftbukkit.v1_11_R1.event.CraftEventFactory.callBlockIgniteEvent(this.world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this).isCancelled()) {
            this.world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
          }
        }
      }
    }
  }
  
  public Map<EntityHuman, Vec3D> b()
  {
    return this.k;
  }
  
  public EntityLiving getSource()
  {
    return (this.source instanceof EntityFireball) ? ((EntityFireball)this.source).shooter : (this.source instanceof EntityLiving) ? (EntityLiving)this.source : (this.source instanceof EntityTNTPrimed) ? ((EntityTNTPrimed)this.source).getSource() : this.source == null ? null : null;
  }
  
  public void clearBlocks()
  {
    this.blocks.clear();
  }
  
  public List<BlockPosition> getBlocks()
  {
    return this.blocks;
  }
}
