package autumnvn.autumn;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class Utils {

    static class TimedEntity {
        long time;
        Entity entity;

        TimedEntity(Entity entity) {
            this.entity = entity;
            this.time = System.currentTimeMillis();
        }
    }

    static TimedEntity recentTargetedEntity;

    public static Entity getTargetedEntity() {
        Entity cameraEntity = AutumnClient.client.getCameraEntity();
        if (cameraEntity == null) return null;

        double maxDistance = 128;
        float tickDelta = AutumnClient.client.getTickDelta();
        Vec3d vec3d = cameraEntity.getEyePos();
        Vec3d vec3d2 = cameraEntity.getRotationVec(tickDelta).multiply(maxDistance);
        Box box = cameraEntity.getBoundingBox().stretch(vec3d2).expand(1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(cameraEntity, vec3d, vec3d.add(vec3d2), box, Entity::canHit, maxDistance * maxDistance);

        if (entityHitResult != null) {
            recentTargetedEntity = new TimedEntity(entityHitResult.getEntity());
            return recentTargetedEntity.entity;
        }

        if (recentTargetedEntity != null && (System.currentTimeMillis() - recentTargetedEntity.time) > 3000) {
            recentTargetedEntity = null;
        }

        return recentTargetedEntity == null ? null : recentTargetedEntity.entity;
    }

    public static String color(double value, double min, double max) {
        double third = (max - min) / 3;
        return value < min + third ? "§c" : value < min + third * 2 ? "§e" : "§a";
    }

    public static String getOwnerName(Entity entity) {
        if (!(entity instanceof TameableEntity tameableEntity)) return null;
        if (tameableEntity.getOwnerUuid() == null) return null;
        PlayerEntity owner = entity.getWorld().getPlayerByUuid(tameableEntity.getOwnerUuid());

        return owner == null ? null : Objects.requireNonNull(owner.getDisplayName()).getString();
    }
}
