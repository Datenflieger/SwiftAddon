package xyz.datenflieger.v1_21_10.mixins;

import net.minecraft.world.entity.player.Abilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.datenflieger.core.SwiftAddon;
import xyz.datenflieger.core.listener.FlySpeedListener;

@Mixin(Abilities.class)
public abstract class MixinPlayerAbilities {

  @Shadow
  private float flyingSpeed;

  @Inject(method = "getFlyingSpeed", at = @At("RETURN"), cancellable = true)
  public void getFlyingSpeed(CallbackInfoReturnable<Float> cir) {
    try {
      SwiftAddon addon = SwiftAddon.getInstance();
      if (addon == null || !addon.configuration().enabled().get() || !addon.configuration().flySpeedEnabled().get()) {
        return;
      }
      FlySpeedListener listener = addon.getFlySpeedListener();
      if (listener == null) {
        return;
      }
      float customSpeed = listener.getCurrentFlySpeed();
      
      if (customSpeed != 0.05f) {
        cir.setReturnValue(customSpeed);
      }
    } catch (Exception e) {
      
    }
  }
}
