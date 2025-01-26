package eu.virtusdevelops.playertimers.core.controllers;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;

public class TemplateController {
    private JavaPlugin plugin;



    public TemplateController(JavaPlugin plugin) {
        this.plugin = plugin;
    }



    public List<String> loadTemplate(String name){
        try{
            var commands = plugin.getConfig().getStringList("templates." + name);
            return commands;
        }catch (Exception e){
            return List.of();
        }
    }

    public Set<String> getAllTemplates(){
        var section = plugin.getConfig().getConfigurationSection("templates");

        return section.getKeys(false);
    }

}
