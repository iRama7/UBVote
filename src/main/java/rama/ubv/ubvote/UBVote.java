package rama.ubv.ubvote;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class UBVote extends JavaPlugin implements Listener {

    //File
    private File signFile;
    private FileConfiguration signConfig;

    private File voteFile;
    private FileConfiguration voteConfig;

    //Files

    @Override
    public void onEnable() {
        createSignConfig();
        createPlayerVoteConfig();
        Bukkit.getPluginCommand("ubvote").setExecutor(new Command(this));
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void signClickEvent(PlayerInteractEvent e) throws IOException {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        if(b.getType() == Material.OAK_SIGN){
            Sign sign = (Sign) b.getState();
            if(ChatColor.stripColor(sign.getLine(0)).equals("Vota por:")){
                String gamemodeName = ChatColor.stripColor(sign.getLine(1));
                if(!voteConfig.isSet(p.getName())) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHas depositado tu voto en "+gamemodeName));
                    signConfig.set("signs."+gamemodeName+".votes", signConfig.getInt("signs."+gamemodeName+".votes") + 1);
                    signConfig.save(signFile);
                    voteConfig.set(p.getName(), gamemodeName);
                    voteConfig.save(voteFile);
                    sign.setLine(2, "Votos: "+signConfig.get("signs."+gamemodeName+".votes"));
                    sign.update();
                }else{
                    if(!voteConfig.get(p.getName()).equals(gamemodeName)) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHas movido tu voto a " + gamemodeName));
                        signConfig.set("signs."+gamemodeName+".votes", signConfig.getInt("signs."+gamemodeName+".votes") + 1);
                        signConfig.set("signs."+voteConfig.get(p.getName())+".votes", signConfig.getInt("signs."+voteConfig.getString(p.getName())+".votes") - 1);
                        sign.setLine(2, "Votos: "+signConfig.get("signs."+gamemodeName+".votes"));
                        sign.update();
                        Location loc = signConfig.getLocation("signs."+voteConfig.get(p.getName())+".location");
                        Block block = Bukkit.getWorld(p.getWorld().getName()).getBlockAt(loc);
                        Sign prevSign = (Sign) block.getState();
                        prevSign.setLine(2, "Votos: "+signConfig.getInt("signs."+voteConfig.getString(p.getName())+".votes"));
                        prevSign.update();
                        signConfig.save(signFile);
                        voteConfig.set(p.getName(), gamemodeName);
                        voteConfig.save(voteFile);
                    }else{
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYa has votado por esta modalidad!"));
                    }
                }
            }
        }
    }

    public FileConfiguration getSignConfig(){
        return this.signConfig;
    }

    public File getSignFile(){
        return this.signFile;
    }

    public FileConfiguration getPlayerVoteConfig(){
        return this.voteConfig;
    }

    public File getVoteFile(){
        return this.voteFile;
    }

    private void createSignConfig(){
        signFile = new File(getDataFolder(), "signs.yml");
        if(!signFile.exists()){
            signFile.getParentFile().mkdirs();
            saveResource("signs.yml", false);
        }

        signConfig = new YamlConfiguration();
        try{
            signConfig.load(signFile);
        } catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }

    private void createPlayerVoteConfig(){
        voteFile = new File(getDataFolder(), "votes.yml");
        if(!voteFile.exists()){
            voteFile.getParentFile().mkdirs();
            saveResource("votes.yml", false);
        }

        voteConfig = new YamlConfiguration();
        try{
            voteConfig.load(voteFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
