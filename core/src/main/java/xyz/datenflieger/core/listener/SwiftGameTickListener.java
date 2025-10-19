package xyz.datenflieger.core.listener;

import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import xyz.datenflieger.core.SwiftAddon;

public class SwiftGameTickListener {

  private final SwiftAddon addon;

  public SwiftGameTickListener(SwiftAddon addon) {
    this.addon = addon;
  }

}
