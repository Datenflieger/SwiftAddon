package xyz.datenflieger.core.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextDecoration;
import xyz.datenflieger.core.SwiftAddon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class UuidCommand extends Command {

  public UuidCommand() {
    super("uuid");
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    SwiftAddon addon = SwiftAddon.getInstance();
    if (addon == null || !addon.configuration().enabled().get() || !addon.configuration().uuidCommandEnabled().get()) {
      return false;
    }

    if (arguments.length == 0) {
      this.displayMessage(Component.text("Usage: /uuid <playername> or /uuid self", NamedTextColor.RED));
      return true;
    }

    final String playerName;
    if (arguments[0].equalsIgnoreCase("self")) {
      try {
        playerName = Laby.labyAPI().minecraft().getClientPlayer().getName();
      } catch (Exception e) {
        this.displayMessage(Component.text("Could not get your player name", NamedTextColor.RED));
        return true;
      }
    } else {
      playerName = arguments[0];
    }

    new Thread(() -> {
      try {
        String uuid = fetchUuidFromMojang(playerName);
        
        if (uuid == null) {
          Laby.labyAPI().minecraft().executeOnRenderThread(() -> {
            this.displayMessage(Component.text("Player not found: " + playerName, NamedTextColor.RED));
          });
          return;
        }

        String formattedUuid = formatUuid(uuid);

        Laby.labyAPI().minecraft().executeOnRenderThread(() -> {
          Component message = Component.text()
              .append(Component.text("UUID of ", NamedTextColor.GRAY))
              .append(Component.text(playerName, NamedTextColor.AQUA))
              .append(Component.text(": ", NamedTextColor.GRAY))
              .append(Component.text(formattedUuid, NamedTextColor.WHITE))
              .append(Component.text(" ", NamedTextColor.GRAY))
              .append(Component.text("[Copy]", NamedTextColor.GREEN, TextDecoration.BOLD)
                  .clickEvent(ClickEvent.copyToClipboard(formattedUuid))
                  .hoverEvent(HoverEvent.showText(Component.text("Click to copy UUID", NamedTextColor.YELLOW))))
              .build();
          
          this.displayMessage(message);
        });
        
      } catch (Exception e) {
        Laby.labyAPI().minecraft().executeOnRenderThread(() -> {
          this.displayMessage(Component.text("Error fetching UUID: " + e.getMessage(), NamedTextColor.RED));
        });
      }
    }).start();

    return true;
  }

  @Override
  public List<String> complete(String[] arguments) {
    return new ArrayList<>();
  }

  private String fetchUuidFromMojang(String playerName) throws Exception {
    String urlString = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
    URI uri = new URI(urlString);
    HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
    connection.setRequestMethod("GET");
    connection.setConnectTimeout(5000);
    connection.setReadTimeout(5000);

    int responseCode = connection.getResponseCode();
    if (responseCode == 404) {
      return null;
    }

    if (responseCode != 200) {
      throw new Exception("HTTP Error: " + responseCode);
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String line;
    
    while ((line = reader.readLine()) != null) {
      response.append(line);
    }
    reader.close();

    JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
    return json.get("id").getAsString();
  }

  private String formatUuid(String uuid) {
    if (uuid.length() != 32) {
      return uuid;
    }
    
    return uuid.substring(0, 8) + "-" +
           uuid.substring(8, 12) + "-" +
           uuid.substring(12, 16) + "-" +
           uuid.substring(16, 20) + "-" +
           uuid.substring(20, 32);
  }
}
