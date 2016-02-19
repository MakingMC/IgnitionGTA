package Main;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_8_R3.EntityProjectile;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.logging.Logger;

public final class Main extends JavaPlugin implements Listener {
    public static Economy econ = null;
    private static final Logger log = Logger.getLogger("Minecraft");
    public HashMap<String, Integer> stars = new HashMap<>();
    public HashMap<String, Integer> kills = new HashMap<>();
    public HashMap<String, Integer> deaths = new HashMap<>();
    public HashMap<String, String> arrowName = new HashMap<>();
    public HashMap<String, Boolean> isTyping = new HashMap<>();
    public Hologram hersaz;
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Enabling...");
        getLogger().info("Enabled");
        stars = new HashMap<>();
        kills = new HashMap<>();
        deaths = new HashMap<>();
        arrowName = new HashMap<>();
        isTyping = new HashMap<>();
        Hologram h = new Hologram();
        new BukkitRunnable() {
            @Override
            public void run()
            {
                removeGold();
            }
        }.runTaskTimer(this, 1L, 0L);
        for (Player p : Bukkit.getOnlinePlayers()
                ) {
            isTyping.put(p.getName(), false);
            arrowName.putIfAbsent(p.getName(), null);
            stars.putIfAbsent(p.getPlayer().getName(), 0);
            if (Bukkit.getOnlinePlayers().size() >= 1) {
                if (stars.size() >= 1) {

                    new BukkitRunnable() {
                        @Override
                        public void run()
                        {
                            starDown();
                        }
                    }.runTaskTimer(this, 0L, 900L);
                    new BukkitRunnable() {
                        @Override
                        public void run()
                        {
                            starCops();
                        }
                    }.runTaskTimer(this, 0L, 300L);


                }
            }
        }
    }
    public void removeGold(){
        for (Player p:Bukkit.getOnlinePlayers()
             ) {
            if(p.getPlayer().getInventory().contains(Material.GOLD_NUGGET)) {

                p.getInventory().remove(Material.GOLD_NUGGET);
                p.updateInventory();
            }
        }
    }
    public void DAB(Player p){

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        ItemMeta bootmeta = boots.getItemMeta();
        bootmeta.setDisplayName(String.format("%s%sDAB", ChatColor.LIGHT_PURPLE, ChatColor.BOLD));
        boots.setItemMeta(bootmeta);
        ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemMeta pantsmeta = pants.getItemMeta();
        pantsmeta.setDisplayName(String.format("%s%sMY", ChatColor.RED, ChatColor.BOLD));
        pants.setItemMeta(pantsmeta);
        ItemStack shirt = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta shirtmeta = shirt.getItemMeta();
        shirtmeta.setDisplayName(String.format("%s%sAT", ChatColor.GREEN, ChatColor.BOLD));
        shirt.setItemMeta(shirtmeta);
        ItemStack hat = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta hatmeta = hat.getItemMeta();
        hatmeta.setDisplayName(String.format("%s%sLOOK", ChatColor.AQUA, ChatColor.BOLD));
        hat.setItemMeta(hatmeta);
        p.getInventory().setArmorContents(new ItemStack[]{boots, pants, shirt, hat});

    }
    public void giveMoney(Integer money, Player player){
        econ.depositPlayer(player, money);
        player.sendMessage(String.format("%sYou have just earned %s%s%s dollars!", ChatColor.GREEN, ChatColor.LIGHT_PURPLE, money, ChatColor.GREEN));
    }
    @EventHandler
    public void orgs(EntityShootBowEvent e){
        if(arrowName.get(e.getEntity().getName()) != null) {
            e.getProjectile().setCustomName(arrowName.get(e.getEntity().getName()));
            e.getProjectile().setCustomNameVisible(true);
        }
    }
    @EventHandler
    public void pickupGold(PlayerPickupItemEvent e){
        Player player = e.getPlayer();

        if(e.getItem().getType() == EntityType.DROPPED_ITEM){
            if(e.getItem().getItemStack().getType() == Material.GOLD_NUGGET){
                String name = e.getItem().getItemStack().getItemMeta().getDisplayName();
                int am = Integer.valueOf(name);
                giveMoney(am, player);
            }
        }
    }
    public void Scoresses(Player p){


            new BukkitRunnable() {
                @Override
                public void run() {
                    Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
                    Objective objective = sb.registerNewObjective(p.getName(), "dummy");
                    Scoreboard sb2 = Bukkit.getScoreboardManager().getNewScoreboard();
                    Objective objective2 = sb2.registerNewObjective(p.getName(), "dummy");
                    objective2.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    checkConfig(p);
                    int strs = stars.get(p.getName());
                    int bal = (int)econ.getBalance(p);

                    objective.getScore(String.format("%s%sGang",ChatColor.DARK_GREEN, ChatColor.BOLD )).setScore(20);
                    if(IgnitionGangs.Main.gang.get(p.getName())!=null) {
                        objective.getScore(String.format("%s        ", IgnitionGangs.Main.gang.get(p.getName()))).setScore(19);
                    }
                    if(IgnitionGangs.Main.gang.get(p.getName())==null) {
                        objective.getScore(String.format("%sNo gang",ChatColor.RED)).setScore(19);
                    }

                    objective.getScore("        ").setScore(18);
                    objective.getScore(" ").setScore(17);
                    objective.getScore(String.format("%s%sBalance", ChatColor.AQUA, ChatColor.BOLD)).setScore(16);
                    objective.getScore(String.format("%s ", bal)).setScore(15);
                    if(strs == 0) {
                        objective.getScore(String.format("%s%sWanted Level", ChatColor.RED, ChatColor.BOLD)).setScore(14);
                        objective.getScore("✪✪✪✪✪").setScore(13);
                    }
                    if(strs == 1) {
                        objective.getScore(String.format("%s%sWanted Level", ChatColor.RED, ChatColor.BOLD)).setScore(14);
                        objective.getScore(String.format("%s✪%s✪✪✪✪", ChatColor.GOLD, ChatColor.WHITE)).setScore(13);
                    }
                    if(strs == 2) {
                        objective.getScore(String.format("%s%sWanted Level", ChatColor.RED, ChatColor.BOLD)).setScore(14);
                        objective.getScore(String.format("%s✪✪%s✪✪✪", ChatColor.GOLD, ChatColor.WHITE)).setScore(13);
                    }
                    if(strs == 3) {
                        objective.getScore(String.format("%s%sWanted Level", ChatColor.RED, ChatColor.BOLD)).setScore(14);
                        objective.getScore(String.format("%s✪✪✪%s✪✪", ChatColor.GOLD, ChatColor.WHITE)).setScore(13);
                    }
                    if(strs == 4) {
                        objective.getScore(String.format("%s%sWanted Level", ChatColor.RED, ChatColor.BOLD)).setScore(14);
                        objective.getScore(String.format("%s✪✪✪✪%s✪", ChatColor.GOLD, ChatColor.WHITE)).setScore(13);
                    }
                    if(strs == 5) {
                        objective.getScore(String.format("%s%sWanted Level", ChatColor.RED, ChatColor.BOLD)).setScore(14);
                        objective.getScore(String.format("%s✪✪✪✪✪", ChatColor.GOLD)).setScore(13);
                    }
                    //objective.getScore("  ").setScore(12);
                    //objective.getScore(String.format("%s%sOnline Players", ChatColor.GREEN, ChatColor.BOLD)).setScore(11);
                    //objective.getScore(String.format("%s   ", Bukkit.getServer().getOnlinePlayers().size())).setScore(10);
                    //objective.getScore("   ").setScore(9);
                    objective.getScore(String.format("%s%sKills", ChatColor.GREEN,ChatColor.BOLD)).setScore(8);
                    objective.getScore(String.format("%s    ", kills.get( p.getName()))).setScore(7);
                    //objective.getScore("    ").setScore(6);
                    objective.getScore(String.format("%s%sDeaths", ChatColor.DARK_RED,ChatColor.BOLD)).setScore(5);
                    objective.getScore(String.format("%s             ", deaths.get(p.getName()))).setScore(4);
                    //objective.getScore("     ").setScore(3);
                    objective.getScore(String.format("%s%sK/D Ratio",ChatColor.LIGHT_PURPLE , ChatColor.BOLD)).setScore(2);
                    if(deaths.get(p.getName()) != 0 && kills.get(p.getName()) != 0) {
                        objective.getScore(String.format("%s                  ", Math.round(kills.get(p.getName())/deaths.get(p.getName())))).setScore(1);
                    }
                    else{
                        objective.getScore(String.format("%s                  ", 0)).setScore(1);
                    }
                    p.setScoreboard(sb);
                }
            }.runTaskTimer(this, 0L, 100L);

        }
        public void checkConfig(Player p){
            if(this.getConfig().get(p.getName()+".kills") != null) {
                int k = this.getConfig().getInt(p.getName()+".kills");
                kills.putIfAbsent(p.getName(), k);
            }

            if(this.getConfig().get(p.getName()+".deaths") != null) {
                int k = this.getConfig().getInt(p.getName()+".deaths");
                deaths.putIfAbsent(p.getName(), k);
            }
            if(this.getConfig().get(p.getName()+".kills") == null){
                kills.putIfAbsent(p.getName(), 0);
            }
            if(this.getConfig().get(p.getName()+".deaths") == null){
                deaths.putIfAbsent(p.getName(), 0);
            }
        }
    @EventHandler
    public void playerHitPlayer(EntityDamageByEntityEvent e){
        if(e.getDamager().getType().equals(EntityType.PLAYER)){
                holog((Player)e.getDamager(), e.getEntity().getLocation(), String.format("%s",e.getDamage()));

        }
    }
    public void holog(Player p, Location loc, String message) {
        final Hologram holo = new Hologram(message);
        holo.show(p, loc);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable() {

            @Override
            public void run() {
                holo.destroy();
            }
        }, 140);
    }
    @EventHandler
    public void countDown(ServerListPingEvent event) {
        int amofp = event.getNumPlayers();
        event.setMaxPlayers(300);
        event.setMotd("                           "+amofp + " Players                         "+ChatColor.RED + ChatColor.BOLD + "               Welcome to IgnitionGTA " + event.getAddress().getHostName());

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {


        Player player = event.getPlayer();
        stars.putIfAbsent(player.getName(), 0);
        arrowName.putIfAbsent(player.getName(), null);
        checkConfig(event.getPlayer());
        econ.createPlayerAccount(player);
        Scoresses(event.getPlayer());
    }
    @EventHandler
    public void ShotPlayer(WeaponDamageEntityEvent event){
        //TODO: Make name of gold ingot be the amount the person is going to get
        if(event.getVictim().isDead()) {
            Player killer = (Player) event.getDamager();
            Player vic = (Player) event.getVictim();
            deaths.put(vic.getName(), deaths.get(vic.getName()) + 1);
            if (killer.getType().equals(EntityType.PLAYER)) {
                kills.put(killer.getName(), kills.get(killer.getName()) + 1);
                if (stars.get(killer.getName()) <= 4) {
                    stars.put(killer.getName(), stars.get(killer.getName()) + 1);
                }
            }
        }
    }




    @EventHandler
    public void starsToKiller(PlayerDeathEvent event) {


        Player p = event.getEntity().getPlayer();
        Location l = p.getLocation();
        ItemStack s = new ItemStack(Material.GOLD_NUGGET, 1);
        ItemMeta im = s.getItemMeta();

        int mon = (int) econ.getBalance(p)/3;
        im.setDisplayName(String.format("%s",mon));
        s.setItemMeta(im);
        econ.withdrawPlayer(p, mon);
        getServer().getWorlds().get(0).dropItem(l, s);
        event.getEntity().sendMessage(String.format("%sYou have lost %s%s%s dollars.", ChatColor.LIGHT_PURPLE, ChatColor.GREEN, mon, ChatColor.LIGHT_PURPLE));
        deaths.put(p.getName(), deaths.get(p.getName())+1);
        if(event.getEntity().getKiller().getType().equals(EntityType.PLAYER)) {
            //econ.depositPlayer(p.getKiller(), mon);
            //event.getEntity().getKiller().sendMessage(String.format("%sYou have earned %s%s%s dollars.", ChatColor.LIGHT_PURPLE, ChatColor.GREEN, mon, ChatColor.LIGHT_PURPLE));
            kills.put(event.getEntity().getKiller().getName(), kills.get(event.getEntity().getKiller().getName())+1);
            if (stars.get(event.getEntity().getKiller().getName()) <= 4) {

                stars.put(event.getEntity().getKiller().getName(), stars.get(event.getEntity().getKiller().getName()) + 1);
            }
        }
    }


    public void starDown() {
        for (Player player : Bukkit.getOnlinePlayers()
                ) {


            if (stars.get(player.getName()) > 0) {
                stars.put(player.getName(), stars.get(player.getName()) - 1);

            }
        }
    }

    @EventHandler
    public void damageIndicator(EntityDamageByEntityEvent event) {

        event.getDamager().sendMessage(String.format("You hit %s%s for %s damage", event.getEntity().getName(), ChatColor.WHITE, event.getDamage()));

    }


    @EventHandler
    public void killCop(EntityDeathEvent event) {

        if (event.getEntity().getKiller() != null) {

            if(event.getEntity().getKiller() instanceof Player) {
                Location l = event.getEntity().getLocation();
                ItemStack s = new ItemStack(Material.GOLD_NUGGET, 1);
                ItemMeta im = s.getItemMeta();

                im.setDisplayName(String.format("%s", 5));
                s.setItemMeta(im);

                getServer().getWorlds().get(0).dropItemNaturally(l, s);
            }
            if (stars.get(event.getEntity().getKiller().getName()) <= 4) {
                if (event.getEntity().getType() == EntityType.PIG_ZOMBIE) {
                    if (event.getEntity().getKiller() instanceof Player) {
                        Player player = event.getEntity().getKiller().getPlayer();
                        stars.put(event.getEntity().getKiller().getName(), stars.get(event.getEntity().getKiller().getName())+1);
                    }
                }
            }
        }


    }



    public void starCops() {
        for (Player player : Bukkit.getOnlinePlayers()
                ) {
            World world = getServer().getWorlds().get(0);
            Location playerLocation = new Location(world, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
            Location areaVoid = new Location(world, 0, 0, 0);

            if(stars.get(player.getName()) != 0){
                for (int i = 0; i < 3; ++i) {
                    PigZombie pigZombie = world.spawn(playerLocation, PigZombie.class);
                    pigZombie.setAngry(true);
                    pigZombie.setTarget(player);
                    pigZombie.getEquipment().setItemInHandDropChance(0);
                    pigZombie.setBaby(false);
                    pigZombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1));
                    pigZombie.getEquipment().setBootsDropChance(0);
                    pigZombie.getEquipment().setLeggingsDropChance(0);
                    pigZombie.getEquipment().setChestplateDropChance(0);
                    pigZombie.getEquipment().setHelmetDropChance(0);
                    pigZombie.getEquipment().setItemInHandDropChance(0);
                    switch (stars.get(player.getName())) {
                        case 1: {
                            pigZombie.setCustomName(String.format("%s%sCop", ChatColor.AQUA, ChatColor.BOLD));
                            new BukkitRunnable() {
                                @Override
                                public void run() {

                                    pigZombie.teleport(areaVoid);
                                    pigZombie.setLastDamageCause(null);

                                }
                            }.runTaskTimer(this, 360L, 0L);

                            break;
                        }
                        case 2: {
                            pigZombie.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
                            pigZombie.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                            pigZombie.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                            pigZombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                            pigZombie.getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD));
                            pigZombie.setCustomName(String.format("%s%sFBI", ChatColor.BLUE, ChatColor.BOLD));

                            new BukkitRunnable() {
                                @Override
                                public void run() {

                                    pigZombie.teleport(areaVoid);
                                    pigZombie.setLastDamageCause(null);

                                }
                            }.runTaskTimer(this, 360L, 0L);
                            break;
                        }
                        case 3: {
                            pigZombie.setCustomName(String.format("%s%sSwat", ChatColor.GREEN, ChatColor.BOLD));
                            pigZombie.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
                            pigZombie.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
                            pigZombie.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
                            pigZombie.getEquipment().setHelmet(new ItemStack(Material.GOLD_HELMET));
                            pigZombie.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD));
                            new BukkitRunnable() {
                                @Override
                                public void run() {

                                    pigZombie.teleport(areaVoid);
                                    pigZombie.setLastDamageCause(null);

                                }
                            }.runTaskTimer(this, 360L, 0L);
                            break;
                        }
                        case 4: {
                            pigZombie.setCustomName(String.format("%s%sArmy", ChatColor.RED, ChatColor.BOLD));
                            pigZombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
                            pigZombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                            pigZombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                            pigZombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                            pigZombie.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
                            new BukkitRunnable() {
                                @Override
                                public void run() {

                                    pigZombie.teleport(areaVoid);
                                    pigZombie.setLastDamageCause(null);

                                }
                            }.runTaskTimer(this, 360L, 0L);
                            break;
                        }
                        case 5: {
                            pigZombie.setCustomName(String.format("%s%sMarine", ChatColor.DARK_RED, ChatColor.BOLD));
                            pigZombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                            pigZombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                            pigZombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                            pigZombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                            pigZombie.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
                            new BukkitRunnable() {
                                @Override
                                public void run() {

                                    pigZombie.teleport(areaVoid);
                                    pigZombie.setLastDamageCause(null);

                                }
                            }.runTaskTimer(this, 360L, 0L);
                            break;
                        }
                        default:
                            break;
                    }
                }
                }

            }


        }




    @Override
    public void onDisable() {
        getLogger().info("Disabling...");
        getLogger().info("Disabled.");
        for (Player p: Bukkit.getOnlinePlayers()
             ) {
            this.getConfig().set(p.getName()+".kills", kills.get(p.getName()));
            this.getConfig().set(p.getName()+".deaths", deaths.get(p.getName()));
            saveConfig();
        }

    }
    @EventHandler
    public void chat(PlayerChatEvent e){
        if(isTyping.get(e.getPlayer().getName())) {
            arrowName.put(e.getPlayer().getName(), e.getMessage());
            isTyping.put(e.getPlayer().getName(), false);
            e.getPlayer().sendMessage(String.format("%sYou have set your arrow name to %s%s%s.", ChatColor.AQUA, ChatColor.GREEN, arrowName.get(e.getPlayer().getName()), ChatColor.AQUA));
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void drop(EntityInteractEvent e){
        getServer().broadcastMessage(e.getEntity().getName());
    }
    @Override
    public void onLoad() {
        getLogger().info("Loading...");
        getLogger().info("Loaded.");
    }
@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        switch (label.toLowerCase()) {
            case "check": {
                DAB(player);
                player.sendMessage(String.format("You have %s Stars", stars.get(player.getName())));
                break;
            }
            case "ha":{
                kills.put(sender.getName(), kills.get(sender.getName())+1);
                break;
            }
            case "ba":{
                deaths.put(sender.getName(), deaths.get(sender.getName())+1);
                break;
            }
            case "zero": {
                if(player.isOp()) {
                    stars.put(sender.getName(), 0);
                }
                else{
                    player.sendMessage(ChatColor.RED+"You must be op!");
                }
                break;
            }
            case "arrow":{
                if(args.length == 0) {
                    if(isTyping.get(player.getName()).equals(true)){
                        player.sendMessage(String.format("%s/arrow has already been typed. Please type in chat the name of your arrow", ChatColor.RED));
                    }
                    else {
                        isTyping.put(player.getName(), true);
                        player.sendMessage(String.format("%sPlease type the new name your arrow", ChatColor.GREEN));
                    }
                }
                else{
                    player.sendMessage(String.format("%sPlease do /arrow", ChatColor.RED));
                }
                break;
            }
            default:
                break;

        }
            return true;
    }
}