package plugin23.untitled28;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class Main extends JavaPlugin implements Listener {

    private final Set<Block> placedBlocks = new HashSet<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        placedBlocks.add(block); // 플레이어가 설치한 블럭 기록
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        // 플레이어가 설치한 블럭이면 2배 드롭 안함
        if (placedBlocks.contains(block)) {
            placedBlocks.remove(block); // 제거
            return;
        }

        Material type = block.getType();

        // 잔디 블럭 → 흙 2개
        if (type == Material.GRASS_BLOCK) {
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.DIRT, 2));
            return;
        }

        // 자연 블럭 → 2배 드롭
        if (shouldDoubleDrop(type)) {
            event.setDropItems(false);
            for (ItemStack drop : block.getDrops(player.getInventory().getItemInMainHand(), player)) {
                block.getWorld().dropItemNaturally(block.getLocation(), drop.clone());
                block.getWorld().dropItemNaturally(block.getLocation(), drop.clone());
            }
        }
    }

    private boolean shouldDoubleDrop(Material material) {
        if (material.isAir() || material == Material.WATER || material == Material.LAVA) return false;
        String name = material.name().toLowerCase();
        return !(name.contains("leaves") || name.contains("sapling") || name.contains("mushroom") || name.contains("flower") || name.contains("grass_block"));
    }
}
