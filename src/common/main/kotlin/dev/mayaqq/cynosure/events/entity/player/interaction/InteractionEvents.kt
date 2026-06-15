package dev.mayaqq.cynosure.events.entity.player.interaction

import dev.mayaqq.cynosure.events.InteractionResultEvent
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

public sealed class InteractionEvent(
    public val level: Level,
    public val player: Player,
    public val hand: InteractionHand
) : InteractionResultEvent() {

    public class UseEntity(
        level: Level,
        player: Player,
        hand: InteractionHand,
        public val entity: Entity,
        public val hitVec: Vec3?,
        public val phase: Phase
    ) : InteractionEvent(level, player, hand) {
        public enum class Phase {
            SPECIFIC,
            GENERAL
        }
    }

    public class AttackEntity(
        level: Level,
        player: Player,
        hand: InteractionHand,
        public val entity: Entity
    ) : InteractionEvent(level, player, hand)

    public class UseBlock(
        level: Level,
        player: Player,
        hand: InteractionHand,
        public val hitResult: BlockHitResult
    ) : InteractionEvent(level, player, hand)

    public class AttackBlock(
        level: Level,
        player: Player,
        hand: InteractionHand,
        public val pos: BlockPos,
        public val face: Direction
    ) : InteractionEvent(level, player, hand)

    public class UseItem(
        level: Level,
        player: Player,
        hand: InteractionHand
    ) : InteractionEvent(level, player, hand)

    public class AttackWithItem(
        level: Level,
        player: Player,
        hand: InteractionHand
    ) : InteractionEvent(level, player, hand)
    /**
     * Shorthand for getting the item stack in the active hand of the player
     */
    public fun getUsedStack(): ItemStack = player.getItemInHand(hand)
}