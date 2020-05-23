package com.github.princesslana.showme;

import com.github.princesslana.smalld.Attachment;
import com.github.princesslana.smalld.SmallD;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import disparse.discord.smalld.SmallDDispatcher;
import disparse.discord.smalld.SmallDEvent;
import disparse.discord.smalld.SmallDUtils;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.ParsedEntity;

public class ShowMe implements Consumer<SmallD> {

  private static final Path DATA_PATH = Paths.get(System.getenv("SHOWME_DATA"));

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
    String channelId = SmallDUtils.getChannelId(evt.getJson());

    Path folder = DATA_PATH.resolve(ofWhat);

    List<Path> images =
      Files.list(folder).filter(Files::isRegularFile).collect(Collectors.toList());

    Attachment attachment = new Attachment(ofWhat + ".jpg", "image/jpeg", randomChoice(images).toUri().toURL());

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
    SmallD.run(System.getenv("SHOWME_TOKEN"), new ShowMe());
  }




}

