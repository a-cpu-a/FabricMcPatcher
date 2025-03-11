package org.fabricmcpatcher.mixins.color;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.color.ColorizeItem;
import org.fabricmcpatcher.utils.id.EntityIdUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SpawnEggItem.class)
public class SpawnEggItemMixin {

    @Shadow @Final private EntityType<?> type;
/*
    @WrapOperation(method = "getColor",at= @At(value = "FIELD", target = "Lnet/minecraft/item/SpawnEggItem;primaryColor:I"))
    int getColorPrimaryColor(SpawnEggItem instance, Operation<Integer> original) {

        return ColorizeItem.colorizeSpawnerEgg(original.call(instance),type,0);
    }
    @WrapOperation(method = "getColor",at= @At(value = "FIELD", target = "Lnet/minecraft/item/SpawnEggItem;secondaryColor:I"))
    int getColorSecondaryColor(SpawnEggItem instance, Operation<Integer> original) {

        return ColorizeItem.colorizeSpawnerEgg(original.call(instance),type,1);
    }*/ //TODO

    @Inject(method = "<init>",at=@At(value = "RETURN"))
    void initReturn(EntityType type, Item.Settings settings, CallbackInfo ci) {
        List<String> names = new ArrayList<>();
        Identifier id = Registries.ENTITY_TYPE.getId(type);
        names.add(id.toString());
        if(id.getNamespace().equals("minecraft"))
            names.add(id.getPath());

        if(EntityIdUtils.type2OldId.containsKey(type))
            names.add(EntityIdUtils.type2OldId.get(type));

        if(EntityIdUtils.type2Name.containsKey(type))
            names.add(EntityIdUtils.type2Name.get(type));

        ColorizeItem.setupSpawnerEgg(names,type,0,0);//rip, no defaults into log
    }
}
