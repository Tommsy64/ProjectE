package moze_intel.projecte.config;

import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.NBTWhitelist;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.Utils;
import net.minecraft.item.ItemStack;

import java.io.*;

public final class NBTWhitelistParser
{
	private static final String VERSION = "#0.1a";
	private static File CONFIG;
	private static boolean loaded;

	public static void init()
	{
		CONFIG = new File(PECore.CONFIG_DIR, "nbt_whitelist.cfg");
		loaded = false;

		if (!CONFIG.exists())
		{
			try
			{
				if (CONFIG.createNewFile())
				{
					writeDefaultFile();
					loaded = true;
				}
			}
			catch (IOException e)
			{
				PELogger.logFatal("Exception in file I/O: couldn't create custom configuration files.");
				e.printStackTrace();
				return;
			}
		}
		else
		{
			BufferedReader reader = null;

			try
			{
				reader = new BufferedReader(new FileReader(CONFIG));

				String line = reader.readLine();

				if (line == null || !line.equals(VERSION))
				{
					PELogger.logFatal("Found old NBT whitelist file: resetting.");
					writeDefaultFile();
				}
			}
			catch (IOException e)
			{
				PELogger.logFatal("Exception in file I/O: couldn't create custom configuration files.");
				e.printStackTrace();
			}
			finally
			{
				Utils.closeStream(reader);
			}

			loaded = true;
		}
	}

	public static void readUserData()
	{
		if (!loaded)
		{
			PELogger.logFatal("ERROR: configurations files are not loaded!");
			return;
		}

		LineNumberReader reader = null;

		try
		{
			reader = new LineNumberReader(new FileReader(CONFIG));

			String line;

			while ((line = reader.readLine()) != null)
			{
				line = line.trim();

				if (line.isEmpty() || line.charAt(0) == '#')
				{
					continue;
				}

				ItemStack stack = Utils.getStackFromString(line, 0);

				if (stack == null)
				{
					PELogger.logFatal("Error in NBT whitelist file: no item stack found for " + line);
					PELogger.logFatal("At line: " + reader.getLineNumber());
					continue;
				}

				if (NBTWhitelist.register(stack))
				{
					PELogger.logInfo("Registered NBT whitelist for: " + line);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Utils.closeStream(reader);
		}
	}

	private static void writeDefaultFile() throws IOException
	{
		PrintWriter writer = null;

		try
		{
			writer = new PrintWriter(CONFIG);

			writer.println(VERSION);
			writer.println("#Custom NBT whitelist file");
			writer.println("#This file is used for items that should keep NBT data when condensed/transmuted.");
			writer.println("#To add an item, just put it's unlocalized name on a new line. Here's some examples:");
			writer.println("TConstruct:pickaxe");
			writer.println("ExtraUtilities:unstableingot");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			Utils.closeStream(writer);
		}
	}
}
