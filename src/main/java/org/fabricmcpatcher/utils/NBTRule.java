package org.fabricmcpatcher.utils;

import net.minecraft.nbt.*;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

abstract public class NBTRule {
    public static final String NBT_RULE_PREFIX = "nbt.";
    public static final String NBT_RULE_SEPARATOR = ".";
    public static final String NBT_RULE_WILDCARD = "*";
    public static final String NBT_REGEX_PREFIX = "regex:";
    public static final String NBT_IREGEX_PREFIX = "iregex:";
    public static final String NBT_GLOB_PREFIX = "pattern:";
    public static final String NBT_IGLOB_PREFIX = "ipattern:";

    private final String[] tagName;
    private final Integer[] tagIndex;

    public static NBTRule create(String tag, String value) {
        if (tag == null || value == null || !tag.startsWith(NBT_RULE_PREFIX)) {
            return null;
        }
        try {
            tag = tag.substring(NBT_RULE_PREFIX.length());
            if (value.startsWith(NBT_REGEX_PREFIX)) {
                return new Regex(tag, value.substring(NBT_REGEX_PREFIX.length()), true);
            } else if (value.startsWith(NBT_IREGEX_PREFIX)) {
                return new Regex(tag, value.substring(NBT_IREGEX_PREFIX.length()), false);
            } else if (value.startsWith(NBT_GLOB_PREFIX)) {
                return new Glob(tag, value.substring(NBT_GLOB_PREFIX.length()), true);
            } else if (value.startsWith(NBT_IGLOB_PREFIX)) {
                return new Glob(tag, value.substring(NBT_IGLOB_PREFIX.length()), false);
            } else {
                return new Exact(tag, value);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    protected NBTRule(String tag, String value) {
        tagName = tag.split(Pattern.quote(NBT_RULE_SEPARATOR));
        tagIndex = new Integer[tagName.length];
        for (int i = 0; i < tagName.length; i++) {
            if (NBT_RULE_WILDCARD.equals(tagName[i])) {
                tagName[i] = null;
                tagIndex[i] = null;
            } else {
                try {
                    tagIndex[i] = Integer.valueOf(tagName[i]);
                } catch (NumberFormatException e) {
                    tagIndex[i] = -1;
                }
            }
        }
    }

    public final boolean match(NbtCompound nbt) {
        return nbt != null && match(nbt, 0);
    }

    private boolean match(NbtCompound nbt, int index) {
        if (index >= tagName.length) {
            return false;
        } else if (tagName[index] == null) {
            for (String nbtBase : nbt.getKeys()) {
                if (match1(nbt.get(nbtBase), index + 1)) {
                    return true;
                }
            }
            return false;
        } else {
            NbtElement nbtBase = nbt.get(tagName[index]);
            return match1(nbtBase, index + 1);
        }
    }

    private boolean match(AbstractNbtList<?> nbt, int index) {
        if (index >= tagIndex.length) {
            return false;
        } else if (tagIndex[index] == null) {
            for (int i = 0; i < nbt.size(); i++) {
                if (match1(nbt.get(i), index + 1)) {
                    return true;
                }
            }
            return false;
        } else {
            int tagNum = tagIndex[index];
            return tagNum >= 0 && tagNum < nbt.size() && match1(nbt.get(tagNum), index + 1);
        }
    }

    private boolean match1(NbtElement nbt, int index) {
        if (nbt == null) {
            return false;
        } else if (nbt instanceof NbtCompound) {
            return match((NbtCompound) nbt, index);
        } else if (nbt instanceof AbstractNbtList<?>) {
            return match((AbstractNbtList<?>) nbt, index);
        } else if (index < tagName.length) {
            return false;
        } else if (nbt instanceof NbtString) {
            return match((NbtString) nbt);
        } else if (nbt instanceof NbtInt) {
            return match((NbtInt) nbt);
        } else if (nbt instanceof NbtDouble) {
            return match((NbtDouble) nbt);
        } else if (nbt instanceof NbtFloat) {
            return match((NbtFloat) nbt);
        } else if (nbt instanceof NbtLong) {
            return match((NbtLong) nbt);
        } else if (nbt instanceof NbtShort) {
            return match((NbtShort) nbt);
        } else if (nbt instanceof NbtByte) {
            return match((NbtByte) nbt);
        } else {
            return false;
        }
    }

    protected boolean match(NbtByte nbt) {
        return false;
    }

    protected boolean match(NbtDouble nbt) {
        return false;
    }

    protected boolean match(NbtFloat nbt) {
        return false;
    }

    protected boolean match(NbtInt nbt) {
        return false;
    }

    protected boolean match(NbtLong nbt) {
        return false;
    }

    protected boolean match(NbtShort nbt) {
        return false;
    }

    protected boolean match(NbtString nbt) {
        return false;
    }

    private static final class Exact extends NBTRule {
        private final Byte byteValue;
        private final Double doubleValue;
        private final Float floatValue;
        private final Integer integerValue;
        private final Long longValue;
        private final Short shortValue;
        private final String stringValue;

        Exact(String tag, String value) {
            super(tag, value);
            stringValue = value;

            doubleValue = parse(Double.class, value);
            if (doubleValue == null) {
                floatValue = null;
            } else {
                floatValue = doubleValue.floatValue();
            }

            longValue = parse(Long.class, value);
            if (longValue == null) {
                byteValue = null;
                integerValue = null;
                shortValue = null;
            } else {
                byteValue = longValue.byteValue();
                integerValue = longValue.intValue();
                shortValue = longValue.shortValue();
            }
        }

        private static <T extends Number> T parse(Class<T> cl, String value) {
            try {
                Method valueOf = cl.getDeclaredMethod("valueOf", String.class);
                Object result = valueOf.invoke(null, value);
                if (result != null && cl.isAssignableFrom(result.getClass())) {
                    return cl.cast(result);
                }
            } catch (Throwable e) {
            }
            return null;
        }

        @Override
        protected boolean match(NbtByte nbt) {
            return byteValue != null && byteValue == nbt.byteValue();
        }

        @Override
        protected boolean match(NbtDouble nbt) {
            return doubleValue != null && doubleValue == nbt.doubleValue();
        }

        @Override
        protected boolean match(NbtFloat nbt) {
            return floatValue != null && floatValue == nbt.floatValue();
        }

        @Override
        protected boolean match(NbtInt nbt) {
            return integerValue != null && integerValue == nbt.intValue();
        }

        @Override
        protected boolean match(NbtLong nbt) {
            return longValue != null && longValue == nbt.longValue();
        }

        @Override
        protected boolean match(NbtShort nbt) {
            return shortValue != null && shortValue == nbt.shortValue();
        }

        @Override
        protected boolean match(NbtString nbt) {
            return nbt.asString().equals(stringValue);
        }
    }

    private static final class Regex extends NBTRule {
        private final Pattern pattern;

        Regex(String tag, String value, boolean caseSensitive) {
            super(tag, value);
            pattern = Pattern.compile(value, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
        }

        @Override
        protected boolean match(NbtString nbt) {
            return pattern.matcher(nbt.asString()).matches();
        }
    }

    private static final class Glob extends NBTRule {
        private static final char STAR = '*';
        private static final char SINGLE = '?';
        private static final char ESCAPE = '\\';

        private final String glob;
        private final boolean caseSensitive;

        protected Glob(String tag, String value, boolean caseSensitive) {
            super(tag, value);
            this.caseSensitive = caseSensitive;
            if (!caseSensitive) {
                value = value.toLowerCase();
            }
            glob = value;
        }

        @Override
        protected boolean match(NbtString nbt) {
            String value = nbt.asString();
            return matchPartial(value, 0, value.length(), 0, glob.length());
        }

        private boolean matchPartial(String value, int curV, int maxV, int curG, int maxG) {
            for (; curG < maxG; curG++, curV++) {
                char g = glob.charAt(curG);
                if (g == STAR) {
                    while (true) {
                        if (matchPartial(value, curV, maxV, curG + 1, maxG)) {
                            return true;
                        }
                        if (curV >= maxV) {
                            break;
                        }
                        curV++;
                    }
                    return false;
                } else if (curV >= maxV) {
                    break;
                } else if (g == SINGLE) {
                    continue;
                }
                if (g == ESCAPE && curG + 1 < maxG) {
                    curG++;
                    g = glob.charAt(curG);
                }
                if (!matchChar(g, value.charAt(curV))) {
                    return false;
                }
            }
            return curG == maxG && curV == maxV;
        }

        private boolean matchChar(char a, char b) {
            return a == (caseSensitive ? b : Character.toLowerCase(b));
        }
    }
}