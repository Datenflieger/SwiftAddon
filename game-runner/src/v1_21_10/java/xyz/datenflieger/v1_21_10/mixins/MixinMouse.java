package xyz.datenflieger.v1_21_10.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.datenflieger.core.SwiftAddon;
import xyz.datenflieger.core.listener.FlySpeedListener;

@SuppressWarnings("unused")
@Mixin(MouseHandler.class)
public class MixinMouse {

  @Shadow
  private Minecraft minecraft;

  @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
  private void onMouseScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
    SwiftAddon addon = SwiftAddon.getInstance();

    if (addon == null || !addon.configuration().enabled().get() || !addon.configuration().flySpeedEnabled().get()) {
      return;
    }

    FlySpeedListener listener = addon.getFlySpeedListener();
    if (listener == null) {
      return;
    }

    boolean ctrlPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS ||
                          GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;

    if (!ctrlPressed) {
      return;
    }

    // Prefer using the vanilla Minecraft player abilities to check flying
    if (this.minecraft != null && this.minecraft.player != null) {
      try {
        if (this.minecraft.player.getAbilities().flying) {
          listener.adjustFlySpeed(yOffset);
          ci.cancel();
        }
      } catch (Exception ignored) {
      }
    }
  }
}
