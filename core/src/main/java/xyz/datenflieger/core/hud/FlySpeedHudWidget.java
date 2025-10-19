package xyz.datenflieger.core.hud;

import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import xyz.datenflieger.core.SwiftAddon;

public class FlySpeedHudWidget extends TextHudWidget<TextHudWidgetConfig> {

  private final SwiftAddon addon;
  private TextLine line;

  public FlySpeedHudWidget(SwiftAddon addon) {
    super("flyspeed");
    this.addon = addon;
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    this.line = super.createLine("Fly Speed", "");
  }

  @Override
  public boolean isVisibleInGame() {
    if (this.addon.getFlySpeedListener() == null) {
      return false;
    }
    return this.addon.getFlySpeedListener().shouldDisplay();
  }

  @Override
  public void onTick(boolean isEditorContext) {
    if (this.addon.getFlySpeedListener() != null) {
      int level = this.addon.getFlySpeedListener().getCurrentSpeedLevel();

      String levelText = level == 0 ? "Normal" : "Level " + level;
      String bar = createVisualBar(level);
      
      this.line.updateAndFlush(levelText + " " + bar);
    }
  }
  
  private String createVisualBar(int level) {
    StringBuilder bar = new StringBuilder("§8[");

    for (int i = 0; i < 10; i++) {
      if (i < level) {
        if (level <= 3) {
          bar.append("§a");
        } else if (level <= 6) {
          bar.append("§e");
        } else {
          bar.append("§c");
        }
        bar.append("█");
      } else {
        bar.append("§8█");
      }
    }
    
    bar.append("§8]");
    return bar.toString();
  }
}
