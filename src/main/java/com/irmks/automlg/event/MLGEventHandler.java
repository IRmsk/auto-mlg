package com.irmks.automlg.event;

import com.irmks.automlg.AutoMLG;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 自动落地水事件处理器。
 * 监听玩家坠落，在脚下放置水方块并取消坠落伤害。
 * 水方块会在短暂延迟后自动清除，避免水流蔓延。
 * 不消耗任何物品，不需要作弊权限，生存/创造均生效。
 */
public class MLGEventHandler {

    /** 触发自动落地水的最小坠落距离（格）。小于此距离不会受伤，无需放水。 */
    private static final float MIN_FALL_DISTANCE = 3.0F;

    /** 水方块保留的 tick 数（20 tick = 1秒）。保留足够时间让玩家看到效果，又不至于水流太远。 */
    private static final long WATER_REMOVE_DELAY = 12L;

    /** 按维度记录待移除的水方块：位置 -> 放置时的 gameTime */
    private final Map<Level, Map<BlockPos, Long>> pendingRemoval = new HashMap<>();

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        // 只处理玩家
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Level level = player.level();
        // 只在服务端执行
        if (level.isClientSide) {
            return;
        }

        // 坠落距离太小，不会受伤
        if (event.getDistance() < MIN_FALL_DISTANCE) {
            return;
        }

        // 在玩家脚的位置放水（玩家站在地面上时，blockPosition() 是脚下的空气方块）
        BlockPos waterPos = player.blockPosition();
        BlockState currentState = level.getBlockState(waterPos);

        // 只在空气或可替换方块处放水（避免替换掉重要方块）
        if (currentState.isAir() || currentState.canBeReplaced()) {
            level.setBlock(waterPos, Blocks.WATER.defaultBlockState(), 3);
            pendingRemoval
                    .computeIfAbsent(level, k -> new HashMap<>())
                    .put(waterPos.immutable(), level.getGameTime());
            AutoMLG.LOGGER.debug("AutoMLG: placed water at {} for {}", waterPos, player.getName().getString());
        }

        // 取消坠落伤害（放水也会减伤，但直接取消确保万无一失）
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Map<BlockPos, Long> waters = pendingRemoval.get(event.level);
        if (waters == null || waters.isEmpty()) {
            return;
        }

        long currentTime = event.level.getGameTime();
        Iterator<Map.Entry<BlockPos, Long>> it = waters.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<BlockPos, Long> entry = it.next();
            if (currentTime - entry.getValue() >= WATER_REMOVE_DELAY) {
                BlockPos pos = entry.getKey();
                BlockState state = event.level.getBlockState(pos);
                // 只移除水方块（源水或流动水），如果已被其他方块替换则不动
                if (state.is(Blocks.WATER)) {
                    event.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
                it.remove();
            }
        }
    }
}
