package net.dzikoysk.funnyguilds.data.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;

public class DatabaseBasic {
	
	private static DatabaseBasic instance;
	
	public DatabaseBasic(){
		instance = this;
	}
	
	public void load(){
		try {
			Database db = Database.getInstance();
			db.openConnection();
			
			usersTable(db);
			regionsTable(db);
			guildsTable(db);
			
			ResultSet users = Database.getInstance().executeQuery("SELECT * FROM users");
			while(users.next()) DatabaseUser.deserialize(users);
			FunnyGuilds.info("Loaded users: " + UserUtils.getUsers().size());
			
			ResultSet regions = Database.getInstance().executeQuery("SELECT * FROM regions");
			while(regions.next()) DatabaseRegion.deserialize(regions);
			FunnyGuilds.info("Loaded regions: " + RegionUtils.getRegions().size());
			
			ResultSet guilds = Database.getInstance().executeQuery("SELECT * FROM guilds");
			while(guilds.next()) DatabaseGuild.deserialize(guilds);
			FunnyGuilds.info("Loaded guilds: " + GuildUtils.getGuilds().size());
			
			db.closeConnection();
			IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE);
		} catch (Exception e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
	}
	
	public void save() throws ClassNotFoundException, SQLException {
		Database db = Database.getInstance();
		db.openConnection();
		for(User user : UserUtils.getUsers()){
			if(!user.changed()) continue;
			try {
				new DatabaseUser(user).save(db);
			} catch (Exception e){
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		for(Region region : RegionUtils.getRegions()){
			if(!region.changed()) continue;
			try {
				new DatabaseRegion(region).save(db);
			} catch (Exception e){
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		for(Guild guild : GuildUtils.getGuilds()){
			if(!guild.changed()) continue;
			try {
				new DatabaseGuild(guild).save(db);
			} catch (Exception e){
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		db.closeConnection();
	}
	
	public void guildsTable(Database db) {
		StringBuilder sb = new StringBuilder();
		sb.append("create table if not exists guilds(");
		sb.append("uuid varchar(100) not null,");
		sb.append("name text not null,");
		sb.append("tag text not null,");
		sb.append("owner text not null,");
		sb.append("home text not null,");
		sb.append("region text not null,");
		sb.append("members text not null,");
		sb.append("regions text not null,");
		sb.append("points int not null,");
		sb.append("lives int not null,");
		sb.append("ban bigint not null,");
		sb.append("born bigint not null,");
		sb.append("validity bigint not null,");
		sb.append("pvp boolean not null,");
		sb.append("attacked bigint,");
		sb.append("allies text,");
		sb.append("enemies text,");
		sb.append("info text,");
		sb.append("deputy text,");
		sb.append("primary key (uuid));");
		db.executeUpdate(sb.toString());
		db.executeUpdate("alter table guilds add born bigint not null;");
		db.executeUpdate("alter table guilds add validity bigint not null;");
		db.executeUpdate("alter table guilds add attacked bigint not null;");
		db.executeUpdate("alter table guilds add lives int not null;");
		db.executeUpdate("alter table guilds add ban bigint not null;");
		db.executeUpdate("alter table guilds add pvp boolean not null;");
		db.executeUpdate("alter table guilds add deputy text;");
		//db.executeUpdate("alter table guilds add constraint deputy foreign key (deputy) references users (name) on update cascade;");
	}
	
	public void regionsTable(Database db) {
		StringBuilder sb = new StringBuilder();
		sb.append("create table if not exists regions(");
		sb.append("name varchar(100) not null,");
		sb.append("center text not null,");
		sb.append("size int not null,");
		sb.append("enlarge int not null,");
		sb.append("primary key (name));");
		db.executeUpdate(sb.toString());
	}
	
	public void usersTable(Database db) {
		StringBuilder sb = new StringBuilder();
		sb.append("create table if not exists users(");
		sb.append("uuid varchar(100) not null,");
		sb.append("name text not null,");
		sb.append("points int not null,");
		sb.append("kills int not null,");
		sb.append("deaths int not null,");
		sb.append("guild varchar(100),");
		sb.append("ban bigint,");
		sb.append("reason text,");
		sb.append("primary key (uuid));");
		db.executeUpdate(sb.toString());
		db.executeUpdate("alter table users add ban bigint;");
		db.executeUpdate("alter table users add reason text;");
		db.executeUpdate("alter table users add guild varchar(100);");
		db.executeUpdate("alter table users add constraint guild foreign key (guild) references guilds (name) on update cascade;");

	}

	public static DatabaseBasic getInstance(){
		if(instance != null) return instance;
		return new DatabaseBasic();
	}
}
