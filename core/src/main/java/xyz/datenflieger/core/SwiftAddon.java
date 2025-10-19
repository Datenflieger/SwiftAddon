package xyz.datenflieger.core;

import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;
import xyz.datenflieger.core.commands.UuidCommand;
import xyz.datenflieger.core.hud.FlySpeedHudWidget;
import xyz.datenflieger.core.listener.FlySpeedListener;
import xyz.datenflieger.core.listener.SwiftGameTickListener;

@AddonMain
public class SwiftAddon extends LabyAddon<SwiftConfiguration> {

  private static SwiftAddon instance;
  private FlySpeedListener flySpeedListener;

  @Override
  protected void enable() {
    instance = this;
    this.registerSettingCategory();

    this.registerListener(new SwiftGameTickListener(this));
    this.registerCommand(new UuidCommand());

    this.flySpeedListener = new FlySpeedListener(this);
    this.registerListener(this.flySpeedListener);

    this.labyAPI().hudWidgetRegistry().register(new FlySpeedHudWidget(this));

    this.logger().info("Enabled the Addon with Fly Speed Control");
  }

  @Override
  protected Class<SwiftConfiguration> configurationClass() {
    return SwiftConfiguration.class;
  }

  public FlySpeedListener getFlySpeedListener() {
    return this.flySpeedListener;
  }

  public static SwiftAddon getInstance() {
    return instance;
  }
}
