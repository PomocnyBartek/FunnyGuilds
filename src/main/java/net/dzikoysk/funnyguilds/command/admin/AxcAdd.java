package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

public class AxcAdd implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(messages.generalNoNickGiven);
            return;
        }

        if (!UserUtils.playedBefore(args[1])) {
            sender.sendMessage(messages.generalNotPlayedBefore);
            return;
        }
        
        User user = User.get(args[1]);
        if (user.hasGuild()) {
            sender.sendMessage(messages.generalUserHasGuild);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);
        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }

        guild.addMember(user);
        user.setGuild(guild);
        IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, user.getOfflineUser());

        Player player = user.getPlayer();
        if (player !=null) {
            player.sendMessage(messages.joinToMember.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
        }

        Player owner = guild.getOwner().getPlayer();
        if (owner != null) {
            owner.sendMessage(messages.joinToOwner.replace("{PLAYER}", user.getName()));
        }

        Bukkit.broadcastMessage(messages.broadcastJoin.replace("{PLAYER}", user.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }
}
