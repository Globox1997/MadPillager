package net.madpillager.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.madpillager.entity.MadPillagerEntity;
import net.madpillager.entity.MadTntEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnRestriction.Location;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;

public class EntityInit {

    public static final EntityType<MadPillagerEntity> MAD_PILLAGER_ENTITY = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MadPillagerEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.95F))
            .build();
    public static final EntityType<MadTntEntity> MAD_TNT_ENTITY = FabricEntityTypeBuilder.<MadTntEntity>create(SpawnGroup.MISC, MadTntEntity::new).fireImmune()
            .dimensions(EntityDimensions.fixed(0.98f, 0.98f)).build();

    public static void init() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier("madpillager", "mad_pillager"), MAD_PILLAGER_ENTITY);
        Registry.register(Registry.ENTITY_TYPE, new Identifier("madpillager", "mad_tnt"), MAD_TNT_ENTITY);

        FabricDefaultAttributeRegistry.register(MAD_PILLAGER_ENTITY, MadPillagerEntity.createMadPillagerAttributes());

        Registry.register(Registry.ITEM, new Identifier("madpillager", "spawn_mad_pillager"), new SpawnEggItem(MAD_PILLAGER_ENTITY, 1586736, 0x959B9B, new Item.Settings().group(ItemGroup.MISC)));

        SpawnRestriction.register(EntityInit.MAD_PILLAGER_ENTITY, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
    }

}
