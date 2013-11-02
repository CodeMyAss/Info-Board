
package me.xxsniperzzxxsd.infoboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.xxsniperzzxxsd.infoboard.Util.RandomChatColor;
import me.xxsniperzzxxsd.infoboard.Util.Scroller;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;


public class ScoreBoard {

	public Main plugin;

	public ScoreBoard(Main instance)
	{
		plugin = instance;
	}

	int rotation = 1;
	public ArrayList<String> hidefrom = new ArrayList<String>();
	public String rank = "default";
	public String world = "global";
	private static int numberScore = -1;

	public boolean updateScoreBoard(Player player) {
		if (!plugin.getConfig().getStringList("Disabled Worlds").contains(player.getWorld().getName()) && !hidefrom.contains(player.getName()) &&(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null || player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().equalsIgnoreCase("InfoBoard")))
		{
			if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null)
			{
				createScoreBoard(player);
			} else
			{
				if (plugin.getConfig().contains("Info Board." + String.valueOf(rotation) + "." + player.getWorld().getName()))
				{
					world = player.getWorld().getName();
				}
				if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null)
					if (Main.permission != null)
					{try{
						rank = Main.permission.getPlayerGroups(player.getWorld(), player.getName())[0];
						if (plugin.getConfig().getString("Info Board." + String.valueOf(rotation) + "." + world + "." + rank + ".Title") == null)
							rank = "default";
						}catch(UnsupportedOperationException UOE){
							rank = "default";
						}
					}
				if (plugin.getConfig().getString("Info Board." + String.valueOf(rotation) + "." + world + "." + rank + ".Title") == null)
					return true;
				Scoreboard infoBoard = player.getScoreboard();
				Objective infoObjective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

				int row = 0;
				int spaces = 0;

				List<String> list = plugin.getConfig().getStringList("Info Board." + String.valueOf(rotation) + "." + world + "." + rank + ".Rows");
				ArrayList<String> remove = new ArrayList<String>();

				for (OfflinePlayer op : infoBoard.getPlayers())
				{
					Iterator<String> iter = list.iterator();
					boolean onlist = false;
					while (iter.hasNext())
					{
						String s = iter.next();
						if (getLine(s, player).equalsIgnoreCase(op.getName()) || s.length() == 2)
						{
							onlist = true;
						} else
						{
							if(plugin.ScrollManager.getScrollers(player) != null)
							for (Scroller scroller : plugin.ScrollManager.getScrollers(player))
							{
								scroller.getLastMessage().equals(op.getName());
									onlist = true;
							}
						}
					}
					if (!onlist)
					{
						if (!remove.contains(op.getName()))
							remove.add(op.getName());
					}
				}

				for (String s : remove)
				{
					if (!ChatColor.stripColor(s).equals(""))
					{
						infoBoard.resetScores(Bukkit.getOfflinePlayer(s));
					}
				}
				list = plugin.getConfig().getStringList("Info Board." + String.valueOf(rotation) + "." + world + "." + rank + ".Rows");

				for (String s : list)
				{
					String string = getLine(s, player);
					if (!infoBoard.getPlayers().contains(Bukkit.getOfflinePlayer(string)))
					{
						boolean set = true;
						Score score = null;
						String line = list.get(row);

						if (getLine(line, player).equalsIgnoreCase(" "))
						{
							String space = "&" + spaces;
							spaces++;
							score = infoObjective.getScore(Bukkit.getOfflinePlayer(getLine(space, player)));
						} else
						{
							if (line.contains("~!"))
							{
								String l = (line.split("<")[1]).split(">")[0];
								String l1 = getLine("<" + l + ">", player);
								if (l1.equalsIgnoreCase("Unkown") || l1.equalsIgnoreCase("0"))
								{
									set = false;
								}
								line = line.replaceAll("~!<" + l + ">", "");
							}
							if (line.contains("<split>"))
							{
								String a = line.split("<split>")[0];
								String b = line.split("<split>")[1];

								score = infoObjective.getScore(Bukkit.getOfflinePlayer(getLine(a, player)));
								try
								{
									numberScore = Integer.valueOf(getLine(b.replaceAll(" ", ""), player));
								} catch (NumberFormatException ne)
								{
									numberScore = 0;
								}
							} else if (line.contains(";"))
							{
								String a = line.split(";")[0];
								String b = line.split(";")[1];

								score = infoObjective.getScore(Bukkit.getOfflinePlayer(getLine(a, player)));
								try
								{
									numberScore = Integer.valueOf(getLine(b.replaceAll(" ", ""), player));
								} catch (NumberFormatException ne)
								{
									numberScore = 0;
								}
							} else
							{
								score = infoObjective.getScore(Bukkit.getOfflinePlayer(getLine(line, player)));
							}
						}
						if (set)
						{
							if (numberScore == -1)
								numberScore = list.size() - 1 - row;
							if (!score.getPlayer().getName().startsWith("<scroll>"))
							{
								score.setScore(1);
								score.setScore(numberScore);
							}
							numberScore = -1;
						}
					}
					row++;
				}
				rank = "default";
			}
		}
		return true;
	}

	public boolean createScoreBoard(Player player) {
		if (!hidefrom.contains(player.getName()) && !plugin.getConfig().getStringList("Disabled Worlds").contains(player.getWorld().getName()) && (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null || player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getName().equalsIgnoreCase("InfoBoard")))
		{

			if (plugin.getConfig().contains("Info Board." + String.valueOf(rotation) + "." + player.getWorld().getName()))
			{
				world = player.getWorld().getName();
			}
			if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null)

				if (Main.permission != null)
				{
					try{
					rank = Main.permission.getPlayerGroups(player.getWorld(), player.getName())[0];
					if (plugin.getConfig().getString("Info Board." + String.valueOf(rotation) + "." + world + "." + rank + ".Title") == null)
						rank = "default";
					}catch(UnsupportedOperationException UOE){
						rank = "default";
					}
				}
			if (plugin.getConfig().getString("Info Board." + String.valueOf(rotation) + "." + world + "." + rank + ".Title") == null)
				return true;

			if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null)
			{
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
				player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			}
			ScoreboardManager manager = Bukkit.getScoreboardManager();

			Scoreboard infoBoard = manager.getNewScoreboard();
			Objective infoObjective = infoBoard.registerNewObjective("InfoBoard", "dummy");
			infoObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

			plugin.ScrollManager.reset(player);
			
			String title = plugin.getConfig().getString("Info Board." + String.valueOf(rotation) + "." + world + "." + rank + ".Title");
			if (title.startsWith("<scroll>") && plugin.getConfig().getBoolean("Scrolling Text.Enable"))
			{
				// Replace <scroll>
				title = title.replaceAll("<scroll>", "");

				title = plugin.ScrollManager.createTitleScroller(player, title).getScrolled();
				
			}else{
				title = getLine(title,player);
			}

			infoObjective.setDisplayName(title);
			// Set ScoreBoard
			int row;
			int spaces = 0;


			List<String> list = plugin.getConfig().getStringList("Info Board." + String.valueOf(rotation) + "." + world + "." + rank + ".Rows");
			for (row = 0; row != plugin.getConfig().getStringList("Info Board." + String.valueOf(rotation) + "." + world + "." + rank + ".Rows").size(); row++)
			{
				boolean set = true;
				Score score = null;
				String line = list.get(row);
				if (getLine(line, player).equalsIgnoreCase(" "))
				{
					String space = "&" + spaces;
					spaces++;
					score = infoObjective.getScore(Bukkit.getOfflinePlayer(getLine(space, player)));
				} else
				{
					if (line.contains("~!"))
					{
						String l = (line.split("<")[1]).split(">")[0];
						String l1 = getLine("<" + l + ">", player);
						if (l1.equalsIgnoreCase("Unkown") || l1.equalsIgnoreCase("0"))
						{
							set = false;
						}
						line = line.replaceAll("~!<" + l + ">", "");
					}
					if (line.startsWith("<scroll>") && plugin.getConfig().getBoolean("Scrolling Text.Enable"))
					{
						// Replace <scroll>
						line = line.replaceAll("<scroll>", "");

						line = plugin.ScrollManager.createScroller(player, line).getScrolled();
					}
					if (line.contains("<split>"))
					{
						String a = line.split("<split>")[0];
						String b = line.split("<split>")[1];

						score = infoObjective.getScore(Bukkit.getOfflinePlayer(getLine(a, player)));
						try
						{
							numberScore = Integer.valueOf(getLine(b.replaceAll(" ", ""), player));
						} catch (NumberFormatException ne)
						{
							numberScore = 0;
						}
					} else if (line.contains(";"))
					{

						String a = line.split(";")[0];
						String b = line.split(";")[1];

						score = infoObjective.getScore(Bukkit.getOfflinePlayer(getLine(a, player)));
						try
						{
							numberScore = Integer.valueOf(getLine(b.replaceAll(" ", ""), player));
						} catch (NumberFormatException ne)
						{
							numberScore = 0;
						}
					} else
					{
						score = infoObjective.getScore(Bukkit.getOfflinePlayer(getLine(line, player)));
					}

				}
				if (set)
				{
					if (numberScore == -1)
						numberScore = list.size() - 1 - row;

					score.setScore(1);
					score.setScore(numberScore);

					numberScore = -1;
				}
			}
			player.setScoreboard(infoBoard);
		}
		rank = "default";
		world = "global";
		return true;
	}

	public static String getLine(String string, Player user) {

		// Replace all the variables
		String newString = GetVariables.replaceVariables(string, user);

		// Repalce color codes
		newString = ChatColor.translateAlternateColorCodes('&', newString);
		newString = newString.replaceAll("&x", RandomChatColor.getColor().toString());
		newString = newString.replaceAll("&y", RandomChatColor.getFormat().toString());

		// Make sure string isnt to long
		if (newString.length() > 16)
			newString = newString.substring(0, Math.min(newString.length(), 16));

		return newString;
	}

}
