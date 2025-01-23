package eu.virtusdevelops.playertimers.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class TextUtil {

    public static MiniMessage MM = MiniMessage.miniMessage();


    public static String choiceFor(int index, String noun) {
        return "{index,choice,0#|1#1 noun |1<{index,number,long} nouns }"
                .replace("index", String.valueOf(index))
                .replace("noun", noun);
    }

    public static String prettyPrint(long h, long m, long s) {
        String fmt =
                choiceFor(0, "h") +
                        choiceFor(1, "m") +
                        choiceFor(2, "s");
        return java.text.MessageFormat.format(fmt, h, m, s).trim();
    }

    /**
     * Prase's seconds to time
     * @param duration in seconds
     * @return parsed time
     */
    public static String getTimeFormated(long duration){
        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;

        return prettyPrint(hours, minutes, seconds);
    }

}
