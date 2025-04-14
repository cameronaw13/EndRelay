package cloud.viniciusith.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import cloud.viniciusith.endrelay.EndRelayMod;
import cloud.viniciusith.endrelay.block.EndRelayBlock;
import cloud.viniciusith.endrelay.block.entity.EndRelayBlockEntity;

@Mixin(CompassItem.class)
public class CompassItemMixin {
    @Inject(
        method = "useOnBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemUsageContext;getWorld()Lnet/minecraft/world/World;",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private ActionResult endRelayInjection(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir, BlockPos blockPos, World world) {
        BlockState blockState = world.getBlockState(blockPos);
        if(world.getBlockState(blockPos).isOf(EndRelayMod.END_RELAY_BLOCK) && !(Boolean)blockState.get(EndRelayBlock.HAS_COMPASS)) {
            ItemStack itemStack = context.getStack();
            if (!world.isClient) {
                PlayerEntity playerEntity = context.getPlayer();
                if (world.getBlockEntity(blockPos) instanceof EndRelayBlockEntity endRelayBlockEntity) {
                    endRelayBlockEntity.setStack(itemStack.copy());
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, blockState));
                }
                itemStack.decrement(1);
            }  
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }
}
