package com.github.yannicklamprecht.worldborder.v1_15_R1;

import static com.github.yannicklamprecht.worldborder.api.ConsumerSupplierTupel.of;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.github.yannicklamprecht.worldborder.api.AbstractWorldBorder;
import com.github.yannicklamprecht.worldborder.api.Position;
import com.github.yannicklamprecht.worldborder.api.WorldBorderAction;

import net.minecraft.server.v1_15_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_15_R1.PacketPlayOutWorldBorder;

public class WorldBorder extends AbstractWorldBorder {

  private final net.minecraft.server.v1_15_R1.WorldBorder handle;

  public WorldBorder(Player player) {
    this(new net.minecraft.server.v1_15_R1.WorldBorder());
    this.handle.world = ((CraftWorld) player.getWorld()).getHandle();
  }

  public WorldBorder(World world) {
    this(((CraftWorld) world).getHandle().getWorldBorder());
  }

  private WorldBorder(net.minecraft.server.v1_15_R1.WorldBorder worldBorder) {
    super(
        of(
            position -> worldBorder.setCenter(position.x(), position.x()),
            () -> new Position(worldBorder.getCenterX(), worldBorder.getCenterZ())
        ),
        () -> new Position(worldBorder.c(), worldBorder.d()),
        () -> new Position(worldBorder.e(), worldBorder.f()),
        of(worldBorder::setSize, worldBorder::getSize),
        of(worldBorder::setDamageBuffer, worldBorder::getDamageBuffer),
        of(worldBorder::setDamageAmount, worldBorder::getDamageAmount),
        of(worldBorder::setWarningTime, worldBorder::getWarningTime),
        of(worldBorder::setWarningDistance, worldBorder::getWarningDistance),
        (Location location) -> worldBorder
            .isInBounds(new ChunkCoordIntPair(location.getBlockX(), location.getBlockZ())),
        worldBorder::transitionSizeBetween
    );
    this.handle = worldBorder;
  }

  @Override
  public void send(Player player, WorldBorderAction worldBorderAction) {
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
        new PacketPlayOutWorldBorder(handle,
            PacketPlayOutWorldBorder.EnumWorldBorderAction.valueOf(worldBorderAction.name())));
  }
}
