package rama.ubv.ubvote;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Command implements CommandExecutor {

    private final UBVote plugin;

    public Command(UBVote plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(args.length == 0){
            return false;
        }
        if(args[0].equals("setSign") && args.length == 2){
            Player p = (Player) sender;
            Block block = p.getTargetBlock(null, 3);
            String name = args[1];
            if(block.getType().equals(Material.OAK_SIGN)){
                Location loc = block.getLocation();
                FileConfiguration signConfig = plugin.getSignConfig();
                signConfig.set("signs."+name+".location", loc);
                signConfig.set("signs."+name+".votes", 0);
                try {
                    signConfig.save(plugin.getSignFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Sign sign = (Sign) block.getState();
                sign.setGlowingText(true);
                sign.setLine(0, "Vota por:");
                sign.setLine(1, name);
                sign.setLine(2, "Votos: 0");
                sign.update();
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHas añadido una nueva señal con el nombre "+name));
            }
        }else if(args[0].equals("reload")){
            Player p = (Player) sender;
            YamlConfiguration.loadConfiguration(plugin.getSignFile());
            YamlConfiguration.loadConfiguration(plugin.getVoteFile());
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHas recargado los archivos."));
        }
        return false;
    }
}
