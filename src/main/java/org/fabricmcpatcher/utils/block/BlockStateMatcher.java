package org.fabricmcpatcher.utils.block;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.fabricmcpatcher.resource.PropertiesFile;
import org.fabricmcpatcher.utils.MCPatcherUtils;

import java.util.*;

public class BlockStateMatcher {
    private final String fullString;
    private final ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();

    protected final Block block;
    protected Object data;


    private final Map<Property<?>, Set<Comparable<?>>> propertyMap = new HashMap<>();

    protected BlockStateMatcher(PropertiesFile source, String metaString, Block block, String metadataList, Map<String, String> properties) {
        this.fullString = BlockAPI.getBlockName(block) + metaString;
        this.block = block;

        BlockState state = block.getDefaultState();
        if (properties.isEmpty() && !MCPatcherUtils.isNullOrEmpty(metadataList)) {
            translateProperties(block, MCPatcherUtils.parseIntegerList(metadataList, 0, 15), properties);
            if (!properties.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    if (sb.length() > 0) {
                        sb.append(':');
                    }
                    sb.append(entry.getKey()).append('=').append(entry.getValue());
                }
                source.warning("expanded %s:%s to %s", BlockAPI.getBlockName(block), metadataList, sb);
            }
        }
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String name = entry.getKey();
            boolean foundProperty = false;
            for (Property<?> property : state.getProperties()) {
                if (name.equals(property.getName())) {
                    foundProperty = true;
                    Set<Comparable<?>> valueSet = propertyMap.get(property);
                    if (valueSet == null) {
                        valueSet = new HashSet<>();
                        propertyMap.put(property, valueSet);
                    }
                    if (Integer.class.isAssignableFrom(property.getValueClass())) {
                        parseIntegerValues(property, valueSet, entry.getValue());
                    } else {
                        for (String s : entry.getValue().split("\\s*,\\s*")) {
                            if (s.equals("")) {
                                continue;
                            }
                            Comparable propertyValue = parseNonIntegerValue(property, s);
                            if (propertyValue == null) {
                                source.warning("unknown value %s for block %s property %s",
                                        s, BlockAPI.getBlockName(block), property.getName()
                                );
                                source.warning("must be one of:%s", getPropertyValues(property));
                            } else {
                                valueSet.add(propertyValue);
                            }
                        }
                    }
                }
            }
            if (!foundProperty) {
                source.warning("unknown property %s for block %s", name, BlockAPI.getBlockName(block));
            }
        }
    }

    final public Block getBlock() {
        return block;
    }

    final public Object getData() {
        return data;
    }

    final public void setData(Object data) {
        this.data = data;
    }

    final public Object getThreadData() {
        return threadLocal.get();
    }

    final public void setThreadData(Object data) {
        threadLocal.set(data);
    }

    @Override
    final public String toString() {
        return fullString;
    }


    private static void translateProperties(Block block, int[] metadataList, Map<String, String> properties) {
        if (BlockAPI.getBlockName(block).equals("minecraft:log")) {
            StringBuilder sb = new StringBuilder();
            for (int i : metadataList) {
                sb.append(i).append(',');
                if ((i & 0xc) == 0) {
                    i &= 0x3;
                    sb.append(i | 0x4).append(',');
                    sb.append(i | 0x8).append(',');
                }
            }
            metadataList = MCPatcherUtils.parseIntegerList(sb.toString(), 0, 15);
        }
        Map<Property<?>, Set<Comparable<?>>> tmpMap = new HashMap<>();
        for (int i : metadataList) {
            try {
                BlockState blockState = block.getStateFromMetadata(i);
                for (Property<?> property : blockState.getProperties()) {
                    Set<Comparable<?>> values = tmpMap.get(property);
                    if (values == null) {
                        values = new HashSet<>();
                        tmpMap.put(property, values);
                    }
                    values.add(blockState.get(property));
                }
            } catch (IllegalArgumentException e) {
                // ignore invalid metadata
            }
        }
        for (Property<?> property : block.getDefaultState().getProperties()) {
            Set<Comparable<?>> values = tmpMap.get(property);
            if (values != null && values.size() > 0 && values.size() < property.getValues().size()) {
                StringBuilder sb = new StringBuilder();
                for (Comparable<?> value : values) {
                    if (sb.length() > 0) {
                        sb.append(',');
                    }
                    sb.append(propertyValueToString(value));
                }
                properties.put(property.getName(), sb.toString());
            }
        }
    }

    private static void parseIntegerValues(Property<?> property, Set<Comparable<?>> valueSet, String values) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Comparable<?> c : valueSet) {
            min = Math.min(min, (Integer) c);
            max = Math.max(max, (Integer) c);
        }
        for (int i : MCPatcherUtils.parseIntegerList(values, min, max)) {
            valueSet.add(i);
        }
    }

    private static Comparable parseNonIntegerValue(Property<?> property, String value) {
        for (Comparable propertyValue : property.getValues()) {
            if (value.equals(propertyValueToString(propertyValue))) {
                return propertyValue;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static String getPropertyValues(Property<?> property) {
        StringBuilder sb = new StringBuilder();
        List<Comparable> values = new ArrayList<>(property.getValues());
        Collections.sort(values);
        for (Comparable<?> value : values) {
            sb.append(' ').append(propertyValueToString(value));
        }
        return sb.toString();
    }

    public static String propertyValueToString(Comparable<?> propertyValue) {
        if (propertyValue instanceof INamed) {
            return ((INamed) propertyValue).getName();
        } else {
            return propertyValue.toString();
        }
    }

    public boolean match(BlockRenderView blockAccess, int i, int j, int k) {
        return match(blockAccess.getBlockState(new BlockPos(i, j, k)));
    }

    public boolean match(Block block, int metadata) {
        throw new UnsupportedOperationException("match by metadata");
    }

    public boolean matchBlockState(Object blockState) {
        return match((BlockState) blockState);
    }

    public boolean isUnfiltered() {
        return propertyMap.isEmpty();
    }

    private boolean match(BlockState state) {
        if (state == null || state.getBlock() != block) {
            return false;
        }
        for (Map.Entry<Property<?>, Set<Comparable<?>>> entry : propertyMap.entrySet()) {
            Property<?> property = entry.getKey();
            Set<Comparable<?>> values = entry.getValue();
            if (!values.contains(state.get(property))) {
                return false;
            }
        }
        return true;
    }
}