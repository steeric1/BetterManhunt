# BetterManhunt
### The BetterManhunt plugin for Minecraft.

## Info

Better Manhunt aims at giving its users better experiences and great fun when they play manhunt! Manhunt is a game mode where some players try to speedrun Minecraft while others try to stop them. This plugin makes the game easier in its 3 different stages: the creation, the gameplay and the deletion of the game!

A game can be created by issuing the command `/manhunt create <game-name>`. This gives the creator three options (or two, if they don't have the permission `bettermanhunt.createnewworld`). The creator can choose to use the current world, let the plugin find a world from the server worlds or to create an entirely new world. The criterion by which the plugin searches worlds can be changed in the servers config file. After the game has been created (and the worlds created), players can join the game with `/manhunt join <game-name>` and then `/teamhunters` or `/teamrunners`.

For easier and more fun gameplay, this plugin includes compasses and milestones, as well as a toggleable team chat. The compasses by default point at the closest runner, and when right clicked, show the distance to them. This functionality can be changed in the config file as well. The milestones are very similar to Minecraft's advancements.

The deletion of games happens easily, by issuing the command `/manhunt delete <game-name>`. This also deletes any worlds the plugin itself created, therefore leaving no traces in the server files.

## Other commands

`/manhunt list`
- lists the current games

`/manhunt start <game-name>`
- starts the specified game

`/quitgame`
- quits the game the player is currently in

`/toall`
- toggles chat mode to all players

`/toteam`
- toggles chat mode to team players (default)

`/headstart <time-in-seconds>`
- specifies the length of runner headstart in seconds

## Permissions

`bettermanhunt.createnewworld`
- gives players the ability to create new worlds upon game creation

`bettermanhunt.admin`
- gives players the ability to be in total control of all games

**If you use this plugin, I would strongly recommend backing up any worlds this plugin is used in that are not created by the plugin. The plugin should absolutely not destroy anything it didn't create, but it is still quite untested in that area.**
