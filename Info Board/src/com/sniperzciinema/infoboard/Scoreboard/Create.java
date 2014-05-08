
package com.sniperzciinema.infoboard.Scoreboard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.sniperzciinema.infoboard.InfoBoard;
import com.sniperzciinema.infoboard.Scroll.ScrollManager;
import com.sniperzciinema.infoboard.Util.Files;
import com.sniperzciinema.infoboard.Util.Messages;
import com.sniperzciinema.infoboard.Util.Settings;


public class Create {
	
	public static boolean createScoreBoard(Player player) {
		
		String worldName = "global";
		String rankName = "default";
		int value = -1;
		
		// Before we make the scoreboard lets make sure the player is okay to
		// see it
		if (!Settings.isWorldDisabled(player.getWorld().getName()) && player.hasPermission("InfoBoard.View") && !InfoBoard.hidefrom.contains(player.getName()) && ((player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) || player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().equalsIgnoreCase("InfoBoard")))
		{
			// Does the config contain a scoreboard meant for the world the players
			// currently in
			if (Settings.doesWorldHaveScoreBoard(InfoBoard.rotation, player.getWorld().getName()))
				worldName = player.getWorld().getName();
			else if (Settings.doesGlobalHaveScoreBoard(InfoBoard.rotation))
				worldName = "global";
			else
				return false;
			
			// Is there vault on the server? If so lets make sure it has a
			// permissions plugin ready
			if ((Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) && (InfoBoard.permission != null) && InfoBoard.permissionB)
			{
				try
				{
					// Get the players rank
					String rank = InfoBoard.permission.getPlayerGroups(player.getWorld(), player.getName())[0];
					
					// If the players rank/default is on the world's board set the
					// rankname
					if (Settings.doesRankHaveScoreBoard(InfoBoard.rotation, worldName, rank))
						rankName = rank;
				}
				catch (UnsupportedOperationException UOE)
				{
					// Wasn't able to get players rank
				}
			}
			// Make sure there is a default for the board
			if (!Settings.doesRankHaveScoreBoard(InfoBoard.rotation, worldName, rankName))
				return false;
			
			if (Settings.isPageValid(InfoBoard.rotation, worldName, rankName))
			{
				// If the player has an objective in the sidebar
				if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null)
				{
					// unregister and remove that display(Normally the last page
					// of
					// InfoBoard
					player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
					player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
				}
				// Create a new scoreboard and set it to the sidebar display
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				
				Scoreboard infoBoard = manager.getNewScoreboard();
				Objective infoObjective = infoBoard.registerNewObjective("InfoBoard", "dummy");
				infoObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
				
				// Remove and scrolling texts that the player may have had
				ScrollManager.reset(player);
				
				// Now we go to the title setting method thats down below
				infoObjective.setDisplayName(Messages.getTitle(player, player.getScoreboard(), infoObjective, worldName, rankName));
				
				int row;
				int spaces = 0;
				
				// I create a list variable to go through so i can remember what
				// row
				// number i'm on by using the for loop below
				List<String> list = Files.getConfig().getStringList("Info Board." + String.valueOf(InfoBoard.rotation) + "." + worldName + "." + rankName + ".Rows");
				for (row = 0; row != Files.getConfig().getStringList("Info Board." + String.valueOf(InfoBoard.rotation) + "." + worldName + "." + rankName + ".Rows").size(); row++)
				{
					// Set the basics
					Score score = null;
					// Get the line from the list using the row
					String line = list.get(row);
					boolean set = ShouldSet.test(line, player);
					line = ShouldSet.getLine(line, player);
					// If the line is just a space, it means empty line, so
					// we'll
					// just set it as only a color code
					if (set)
					{
						if (line.equalsIgnoreCase(" "))
						{
							String space = "&" + spaces;
							spaces++;
							score = infoObjective.getScore(Bukkit.getOfflinePlayer(Messages.getLine(space, player)));
						}
						else if (line.startsWith("<staff"))
						{
							String rank = line.split("<staff")[1].split(">")[0];
							StringBuilder staff = new StringBuilder();
							
							if (InfoBoard.permissionB)
								for (Player user : Bukkit.getOnlinePlayers())
									if (InfoBoard.permission.getGroups()[0].equals(rank))
									{
										staff.append(user.getName());
										staff.append(",");
									}
							ScrollManager.createScroller(player, staff.toString());
							score = infoObjective.getScore(Bukkit.getOfflinePlayer(staff.toString()));
						}
						else
						{
							// Now for the scrolling lines
							if (line.startsWith("<scroll>") && !line.contains("<staff"))
								// Replace <scroll> with "" and create the
								// scroll
								// object, but first lets make sure they
								// have
								// scroll
								// on,
								// because if its not then that means the
								// timer
								// was
								// never started
								if (Files.getConfig().getBoolean("Scrolling Text.Enable"))
								{
									line = line.replaceAll("<scroll>", "");
									line = ScrollManager.createScroller(player, line).getScrolled();
								}
								else
									line = "Enable Scroll";
							// If the line contains <split> (They want to
							// set
							// their
							// own
							// score for the line
							if (line.contains("<split>"))
							{
								// We'll split the line and the score, then
								// set
								// them
								String a = line.split("<split>")[0];
								String b = line.split("<split>")[1];
								
								score = infoObjective.getScore(Bukkit.getOfflinePlayer(Messages.getLine(a, player)));
								// Lets make sure it is a number first, if
								// not
								// we'll
								// set
								// it as 0
								try
								{
									value = Integer.valueOf(Messages.getLine(b.replaceAll(" ", ""), player));
								}
								catch (NumberFormatException ne)
								{
									value = 0;
								}
							}// If the line contains ; (Same thing as
								// <split>
								// but
								// looks
								// nicer in a config)
							else if (line.contains(";"))
							{
								String a = line.split(";")[0];
								String b = line.split(";")[1];
								
								score = infoObjective.getScore(Bukkit.getOfflinePlayer(Messages.getLine(a, player)));
								try
								{
									value = Integer.valueOf(Messages.getLine(b.replaceAll(" ", ""), player));
								}
								catch (NumberFormatException ne)
								{
									value = 0;
								}
							}// If the line doesn't contain any form of
								// a split
								// just
								// set
								// the line to the line
							
							else
								score = infoObjective.getScore(Bukkit.getOfflinePlayer(Messages.getLine(line, player)));
							
						}
						// If it was determined that we are in fact showing this
						// line,
						// we'll give it a number that will help sort it on the
						// scoreboard so it doesn't change the order
						
						if (value == -1)
							value = list.size() - 1 - row;
						
						// I set it as 1 first, so if "value" is 0, it'll
						// still show the value as 0
						score.setScore(1);
						score.setScore(value);
						
						// Then i set value back to -1(Which means it wants an
						// auto number
						value = -1;
						
					}
				}
				// then we just set the scoreboard for the player
				player.setScoreboard(infoBoard);
			}
		}
		return true;
	}
	
}
