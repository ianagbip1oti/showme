package com.github.princesslana.showme;

import com.github.princesslana.smalld.Attachment;
import com.github.princesslana.smalld.SmallD;
import com.google.gson.JsonObject;
import disparse.discord.smalld.SmallDDispatcher;
import disparse.discord.smalld.SmallDEvent;
import disparse.discord.smalld.SmallDUtils;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.ParsedEntity;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowMe implements Consumer<SmallD> {

  private static final Logger LOG = LoggerFactory.getLogger(ShowMe.class);

  /** Path to data folder. Categories should be subfolders wlithin here */
  private static final Path DATA_PATH = Paths.get(System.getenv("SHOWME_DATA"));

  /** Optionally restrict to the given channel id */
  private static final String CHANNEL_ID = System.getenv("SHOWME_CHANNEL_ID");

  private static final Cooldown COOLDOWN = new Cooldown();

  @CommandHandler(
    commandName = "showme",
    description="Show a random image. Add an argument for what type of image."
  )
  public static void showme(SmallDEvent evt, List<String> args)
    throws IOException, MalformedURLException {

    if (args.isEmpty()) {
      return;
    }

    String ofWhat = args.get(0);

    // Let's only accept alphanumeric to prevent funky stuff like / or .. here
    if (!ofWhat.matches("^[a-zA-Z0-9]+$")) {
      LOG.warn("Invalid request for '" + ofWhat + "' pics");
      return;
      
    }

    String channelId = SmallDUtils.getChannelId(evt.getJson());

    if (CHANNEL_ID != null && !CHANNEL_ID.equals(channelId)) {
      LOG.warn("Request was in disallowed channel: " + channelId);
      return;
    }

    Path folder = DATA_PATH.resolve(ofWhat);

    if (!Files.isDirectory(folder)) {
      LOG.warn("Request for images that don't exist: " + ofWhat);
      return;
    }

    if (!COOLDOWN.acquire(ofWhat)) {
      LOG.warn("Requested within cooldown: " + ofWhat);

      evt.getSmalld().post(
        "/channels/" + channelId + "/messages",
        message("You can't do that again... yet"));

      return;
    }

    List<Path> images =
      Files.list(folder).filter(Files::isRegularFile).collect(Collectors.toList());

    Attachment attachment = new Attachment(
        ofWhat + ".jpg", "image/jpeg", randomChoice(images).toUri().toURL());

    evt.getSmalld().post(
      "/channels/" + channelId + "/messages",
      message("Here is your " + ofWhat + " pic. Enjoy!"),
      attachment);
  }

  private static String message(String content) {
    JsonObject message = new JsonObject();
    message.addProperty("content", content);
    return message.toString();
  }

  private static <T> T randomChoice(List<T> items) {
    int idx = ThreadLocalRandom.current().nextInt(items.size());
    return items.get(idx);
  }

  public void accept(SmallD smalld) {
    SmallDDispatcher.init(smalld, "!");
  }

  public static final void main(String[] args) {
    if (!Files.isDirectory(DATA_PATH)) {
      throw new IllegalStateException("Data path does not point to a folder");
    }

    SmallD.run(System.getenv("SHOWME_TOKEN"), new ShowMe());
  }

  private static class Cooldown {

    private static final Duration COOLDOWN =
      Optional.ofNullable(System.getenv("SHOWME_COOLDOWN")).map(Duration::parse).orElse(null);

    private final Map<String, Instant> cooldowns = new HashMap<>();


    public boolean acquire(String name) {
      if (COOLDOWN == null) {
        return true;
      }

      Instant now = Instant.now();

      if (cooldowns.containsKey(name) && now.isBefore(cooldowns.get(name))) {
        return false;
      }

      cooldowns.put(name, now.plus(COOLDOWN));

      return true;
    }
  }
}

