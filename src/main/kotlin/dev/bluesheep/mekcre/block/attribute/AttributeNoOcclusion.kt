package dev.bluesheep.mekcre.block.attribute

import mekanism.common.block.attribute.Attribute
import net.minecraft.world.level.block.state.BlockBehaviour

object AttributeNoOcclusion : Attribute {
    override fun adjustProperties(props: BlockBehaviour.Properties) {
        props.noOcclusion()
    }
}