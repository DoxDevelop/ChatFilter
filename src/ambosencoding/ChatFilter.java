package ambosencoding;

import com.google.common.base.Joiner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatFilter extends JavaPlugin implements Listener {

    private final Pattern ipPattern = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3}(:\\d{1,5})?");
    private final Pattern domainPattern = Pattern.compile("(https?://)?([a-zA-Z\\-]+\\.)*[a-zA-Z\\-]+\\.([a-zA-Z]+)");
    private List<String> blacklistedWords = Collections.emptyList();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        blacklistedWords = getConfig().getStringList("blacklistedWords")
                .stream().map(String::toLowerCase).collect(Collectors.toList());

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (ipPattern.matcher(e.getMessage()).matches() || domainPattern.matcher(e.getMessage()).matches()) {
            e.getPlayer().sendMessage("Du darfst keine IP's / Links posten!");
            e.setCancelled(true);
        }

        String[] words = e.getMessage().split(" ");

        for (int i = 0; i < words.length; i++) {
            if (blacklistedWords.contains(words[i].toLowerCase())) {
                char[] chars = new char[words[i].length()];
                Arrays.fill(chars, '*');
                words[i] = new String(chars);
            }
        }

        e.setMessage(Joiner.on(" ").join(words));
    }

}
