package com.github.princesslana.showme;

import com.github.princesslana.smalld.SmallD;
import java.util.List;
import java.util.function.Consumer;
import disparse.discord.smalld.SmallDDispatcher;
import disparse.discord.smalld.SmallDEvent;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.ParsedEntity;

public class ShowMe implements Consumer<SmallD> {

  @CommandHandler(commandName = "showme")
  public static void showme(SmallDEvent evt) {

  }

  public void accept(SmallD smalld) {
    SmallDDispatcher.init(smalld, "!");
  }

  public static final void main(String[] args) {
    SmallD.run(System.getenv("SHOWME_TOKEN"), new ShowMe());
  }



}

