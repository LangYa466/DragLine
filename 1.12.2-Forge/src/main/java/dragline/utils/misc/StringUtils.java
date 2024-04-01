/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.utils.misc;

import com.google.common.base.Charsets;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public final class StringUtils {
    private static final Map<String,String> pinyinMap=new HashMap<>();

    public static String toCompleteString(final String[] args, final int start) {
        if(args.length <= start) return "";

        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return codePoint == 0 || codePoint == '\t' || codePoint == '\n' || codePoint == '\r' || codePoint >= ' ' && codePoint <= '\ud7ff' || codePoint >= '\ue000' && codePoint <= '\ufffd' || codePoint >= 65536 && codePoint <= 1114111;
    }

    public static String filterEmoji(String source) {
        if (isBlank(source)) {
            return source;
        } else {
            StringBuilder buf = null;
            int len = source.length();

            for (int i = 0; i < len; ++i) {
                char codePoint = source.charAt(i);
                if (isEmojiCharacter(codePoint)) {
                    if (buf == null) {
                        buf = new StringBuilder(source.length());
                    }

                    buf.append(codePoint);
                }
            }

            if (buf == null) {
                return source;
            } else if (buf.length() == len) {
                return source;
            } else {
                return buf.toString();
            }
        }
    }

    public static boolean isBlank(String s) {
        if (s != null) {
            for (int i = 0; i < s.length(); ++i) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
    }

    public static String toPinyin(final String inString, final String fill) {
        if(pinyinMap.isEmpty()) {
            try {
                String[] dict = IOUtils.toString(StringUtils.class.getClassLoader().getResourceAsStream("assets/minecraft/liquidbounce/chatbypass/pinyin"), Charsets.UTF_8).split(";");
                for(String word:dict){
                    String[] wordData=word.split(",");
                    pinyinMap.put(wordData[0],wordData[1]);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        final String[] strSections = inString.split("");
        final StringBuilder result = new StringBuilder();
        boolean lastIsPinyin = false;
        for(String section : strSections){
            if (pinyinMap.containsKey(section)) {
                result.append(fill);
                result.append(pinyinMap.get(section));
                lastIsPinyin = true;
            } else {
                if(lastIsPinyin) {
                    result.append(fill);
                }
                result.append(section);
                lastIsPinyin = false;
            }
        }
        return result.toString();
    }
    public static String replace(final String string, final String searchChars, String replaceChars) {
        if(string.isEmpty() || searchChars.isEmpty() || searchChars.equals(replaceChars))
            return string;

        if(replaceChars == null)
            replaceChars = "";

        final int stringLength = string.length();
        final int searchCharsLength = searchChars.length();
        final StringBuilder stringBuilder = new StringBuilder(string);

        for(int i = 0; i < stringLength; i++) {
            final int start = stringBuilder.indexOf(searchChars, i);

            if(start == -1) {
                if(i == 0)
                    return string;

                return stringBuilder.toString();
            }

            stringBuilder.replace(start, start + searchCharsLength, replaceChars);
        }

        return stringBuilder.toString();
    }
}
