package com.itemstackmarker;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public final class MarkerRenderer {
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final FormattedCharSequence SPACE = Component.literal(" ").getVisualOrderText();

    private MarkerRenderer() {
    }

    public static void render(WorldRenderContext context) {
        MarkerConfig cfg = MarkerConfig.get();
        if (!cfg.enabled) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        OrderedSubmitNodeCollector queue = context.commandQueue();
        PoseStack pose = context.matrices();
        Vec3 cam = context.worldState().cameraRenderState.pos;
        float partial = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Font.DisplayMode mode = cfg.throughWalls ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;
        double maxSq = cfg.maxDistance * cfg.maxDistance;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof ItemEntity item) || item.isRemoved()) {
                continue;
            }
            int count = item.getItem().getCount();
            if (count <= 0 || (count == 1 && !cfg.showSingles)) {
                continue;
            }

            // Item position relative to the camera.
            Vec3 rel = item.getPosition(partial).subtract(cam);
            if (rel.lengthSqr() > maxSq) {
                continue;
            }

            drawLine(queue, pose, rel, cfg, mode);
            drawCount(queue, pose, rel, count, cfg, mode, mc.font);
        }
    }

    /**
     * The white line. Uses the background rectangle that Minecraft's text renderer
     * draws behind a single space, stretched into a tall thin bar. It turns around
     * to face you (left/right only) so it never goes edge-on.
     */
    private static void drawLine(OrderedSubmitNodeCollector queue, PoseStack pose, Vec3 rel,
                                 MarkerConfig cfg, Font.DisplayMode mode) {
        // A background rect behind " " at x=-1.5 spans x -2.5..2.5 and y -1..9 (font pixels).
        float sx = cfg.lineWidth / 5.0f;
        float sy = cfg.lineHeight / 10.0f;
        float yaw = (float) Math.atan2(-rel.x, -rel.z);

        pose.pushPose();
        pose.translate(rel.x, rel.y + cfg.lineStart + 9.0f * sy, rel.z);
        pose.mulPose(new Quaternionf().rotateY(yaw));
        pose.scale(sx, -sy, sx);
        queue.submitText(pose, -1.5f, 0.0f, SPACE, false, mode, FULL_BRIGHT, 0xFFFFFFFF, cfg.lineColor, 0);
        pose.popPose();
    }

    /** The stack count, floating above the line and always facing the camera. */
    private static void drawCount(OrderedSubmitNodeCollector queue, PoseStack pose, Vec3 rel, int count,
                                  MarkerConfig cfg, Font.DisplayMode mode, Font font) {
        double anchorY = rel.y + cfg.lineStart + cfg.lineHeight;

        // Vector from the label to the camera, used to face it.
        double vx = -rel.x;
        double vy = -anchorY;
        double vz = -rel.z;
        double horizontal = Math.sqrt(vx * vx + vz * vz);
        double dist = Math.sqrt(horizontal * horizontal + vy * vy);

        float yaw = (float) Math.atan2(vx, vz);
        float pitch = (float) Math.atan2(vy, horizontal);

        float scale = 0.06f * cfg.textScale * (1.0f + (float) dist * cfg.distanceGrowth);

        Component text = Component.literal(Integer.toString(count)).withStyle(ChatFormatting.BOLD);
        float width = font.width(text);

        pose.pushPose();
        // Text hangs downward from its origin, so lift by one text-height plus a small gap.
        pose.translate(rel.x, anchorY + 9.0f * scale + 0.05f, rel.z);
        pose.mulPose(new Quaternionf().rotateY(yaw).rotateX(-pitch));
        pose.scale(scale, -scale, scale);
        queue.submitText(pose, -width / 2.0f, 0.0f, text.getVisualOrderText(), false, mode, FULL_BRIGHT,
                cfg.textColor, cfg.textBackground, cfg.textOutline);
        pose.popPose();
    }
}
