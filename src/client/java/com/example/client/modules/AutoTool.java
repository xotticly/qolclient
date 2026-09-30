package com.example.client.modules;

import com.example.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Switches to the fastest hotbar tool for the block you're mining. */
public class AutoTool extends Module {
	public AutoTool() {
		super("AutoTool", "Picks the best tool for the block", Category.MISC);
	}

	@Override
	public void onTick(Minecraft mc) {
		var player = mc.player;
		if (player == null || mc.level == null || mc.screen != null) return;
		if (!mc.options.keyAttack.isDown()) return;
		if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return;

		BlockState state = mc.level.getBlockState(hit.getBlockPos());
		var inv = player.getInventory();

		int bestSlot = inv.getSelectedSlot();
		float bestSpeed = inv.getItem(bestSlot).getDestroySpeed(state);
		for (int i = 0; i < 9; i++) {
			ItemStack stack = inv.getItem(i);
			float speed = stack.getDestroySpeed(state);
			if (speed > bestSpeed) {
				bestSpeed = speed;
				bestSlot = i;
			}
		}
		if (bestSlot != inv.getSelectedSlot()) inv.setSelectedSlot(bestSlot);
	}
}
