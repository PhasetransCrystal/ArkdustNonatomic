package com.phasetranscrystal.nonard.mixin;

import com.phasetranscrystal.nonard.client.event.ModClientEventHooks;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/mojang/blaze3d/platform/Window;getWindow()J", ordinal = 0), cancellable = true)
    public void inject$keyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (ModClientEventHooks.onPreKeyboardPress(key, scanCode, action, modifiers)) {
            ci.cancel();
        }
    }
}