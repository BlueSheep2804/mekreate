package dev.bluesheep.mekcre.util

import com.takenokoshi.mekaddonlib.registration.MachineDeferredRegister
import com.takenokoshi.mekaddonlib.registration.MachineRegistryObject
import mekanism.common.registration.impl.BlockRegistryObject
import mekanism.common.tier.FactoryTier
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

interface FactoryType<MACHINE: MachineRegistryObject<*, *, *, *>> {
    val name: String
    val factories: Map<FactoryTier, MACHINE>

    fun getUpgradeBaseMachine(tier: FactoryTier): BlockRegistryObject<*, *>

    fun register(registry: MachineDeferredRegister)

    fun registerScreen(event: RegisterMenuScreensEvent)
}
