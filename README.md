# ShowMe

A random image discord bot built on [SmallD](https://github.com/princesslana/smalld)
and [disparse](https://github.com/BoscoJared/disparse).

# Configuration

Configuration is all via environment variables.

**SHOWME_TOKEN** [required]: Discord bot token

**SHOWME_DATA** [required]:
Folder from which to fetch random images.
This should be split up by category with the folder name being the category that the users will request.
See the [example-data](https://github.com/ianagbip1oti/showme/tree/master/example-data) folder for an example of a setup
that will support `!showme cat` and `!showme dog`

**SHOWME_CHANNEL_ID**: Optinally restrict ShowMe to responding with images in a single channel only.

**SHOWME_COOLDOWN**: Optionally restrict how often each category can be shown.
Format is based on the ISO-8601 duration format PnDTnHnMn. For example `SHOWME_COOLDOWN=PT15M` would be a 15 minute cooldown.
