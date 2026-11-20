package dev.mco.internal.jei;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemBucketMilk;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import java.util.Collection;

public final class TransposerContainerCache {
    private static final int PROBE_AMOUNT = 1000;
    private static final Object UNFILLABLE = new Object();
    private static final String OPENBLOCKS_TANK_HANDLER = "openblocks.common.item.ItemTankBlock$FluidHandler";
    private static final String FORESTRY_FLUID_HANDLER = "forestry.core.items.FluidHandlerItemForestry";

    private static Thread ownerThread;
    private static ProbeEntry[] ownerProbes;

    private TransposerContainerCache() {
    }

    public static synchronized void beginLoad() {
        ownerThread = Thread.currentThread();
        ownerProbes = null;
    }

    public static synchronized void endLoad() {
        ownerThread = null;
        ownerProbes = null;
    }

    public static Recipes getFillRecipes(ItemStack container) {
        IFluidHandlerItem handler = container.copy().getCapability(
                CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        return probe(container, handler, getProbesForCurrentCall());
    }

    static Recipes probe(ItemStack container, IFluidHandlerItem handler, Iterable<Fluid> fluids) {
        return probe(container, handler, buildProbes(fluids));
    }

    private static Recipes probe(ItemStack container, IFluidHandlerItem handler, ProbeEntry[] probes) {
        Recipes result = new Recipes();
        if (handler == null || probes.length == 0) {
            return result;
        }

        Object targetFluid = resolveTargetFluidOrUnfillable(container, handler);
        if (targetFluid == UNFILLABLE) {
            return result;
        }

        for (ProbeEntry entry : probes) {
            if (targetFluid != null && entry.resolvedFluid != targetFluid) {
                continue;
            }
            FluidStack probeStack = entry.probeStack;
            int filled = handler.fill(probeStack, true);
            if (probeStack.amount != PROBE_AMOUNT || probeStack.tag != null) {
                probeStack = new FluidStack(entry.rawFluid, PROBE_AMOUNT);
                entry.probeStack = probeStack;
            }
            if (filled <= 0) continue;

            result.inputs.add(container);
            result.outputs.add(handler.getContainer().copy());
            result.fluids.add(new FluidStack(entry.rawFluid, filled));
            handler.drain(PROBE_AMOUNT, true);
        }
        return result;
    }

    private static synchronized ProbeEntry[] getProbesForCurrentCall() {
        if (Thread.currentThread() != ownerThread) {
            return buildProbes(FluidRegistry.getRegisteredFluids().values());
        }
        if (ownerProbes == null) {
            ownerProbes = buildProbes(FluidRegistry.getRegisteredFluids().values());
        }
        return ownerProbes;
    }

    private static ProbeEntry[] buildProbes(Iterable<Fluid> fluids) {
        int size = fluids instanceof Collection ? ((Collection<?>) fluids).size() : 16;
        java.util.List<ProbeEntry> list = new java.util.ArrayList<>(size);
        for (Fluid fluid : fluids) {
            if (fluid != null) {
                list.add(new ProbeEntry(fluid));
            }
        }
        return list.toArray(new ProbeEntry[0]);
    }

    private static Object resolveTargetFluidOrUnfillable(ItemStack container, IFluidHandlerItem handler) {
        Class<?> handlerClass = handler.getClass();
        if (handlerClass == FluidBucketWrapper.class) {
            if (container.getCount() != 1
                    || container.getItem() instanceof ItemBucketMilk
                    || ((FluidBucketWrapper) handler).getFluid() != null) {
                return UNFILLABLE;
            }
            return null;
        }

        if (handlerClass == FluidHandlerItemStackSimple.class
                || handlerClass == FluidHandlerItemStackSimple.Consumable.class
                || handlerClass == FluidHandlerItemStackSimple.SwapEmpty.class
                || FORESTRY_FLUID_HANDLER.equals(handlerClass.getName())) {
            if (container.getCount() != 1 || ((FluidHandlerItemStackSimple) handler).getFluid() != null) {
                return UNFILLABLE;
            }
            IFluidTankProperties[] props = handler.getTankProperties();
            if (props != null && props.length == 1 && props[0].getCapacity() > PROBE_AMOUNT) {
                return UNFILLABLE;
            }
            return null;
        }

        if (handlerClass == FluidHandlerItemStack.class
                || handlerClass == FluidHandlerItemStack.Consumable.class
                || handlerClass == FluidHandlerItemStack.SwapEmpty.class) {
            if (container.getCount() != 1) {
                return UNFILLABLE;
            }
            FluidStack contained = ((FluidHandlerItemStack) handler).getFluid();
            return narrowSingleTankFluid(handler, contained);
        }

        if (OPENBLOCKS_TANK_HANDLER.equals(handlerClass.getName())) {
            int count = container.getCount();
            if (count == 0 || PROBE_AMOUNT / count == 0) {
                return UNFILLABLE;
            }
            IFluidTankProperties[] props = handler.getTankProperties();
            if (props != null && props.length == 1) {
                return narrowSingleTankFluid(props[0].getContents(), props[0].getCapacity());
            }
        }

        return null;
    }

    private static Object narrowSingleTankFluid(IFluidHandlerItem handler, FluidStack contained) {
        if (contained == null) {
            return null;
        }
        if (contained.tag != null) {
            return UNFILLABLE;
        }
        IFluidTankProperties[] props = handler.getTankProperties();
        if (props != null && props.length == 1 && contained.amount >= props[0].getCapacity()) {
            return UNFILLABLE;
        }
        return contained.getFluid();
    }

    private static Object narrowSingleTankFluid(FluidStack contained, int capacity) {
        if (contained == null) {
            return null;
        }
        if (contained.tag != null || contained.amount >= capacity) {
            return UNFILLABLE;
        }
        return contained.getFluid();
    }

    private static final class ProbeEntry {
        final Fluid rawFluid;
        final Fluid resolvedFluid;
        FluidStack probeStack;

        ProbeEntry(Fluid rawFluid) {
            this.rawFluid = rawFluid;
            this.probeStack = new FluidStack(rawFluid, PROBE_AMOUNT);
            this.resolvedFluid = this.probeStack.getFluid();
        }
    }

    public static final class Recipes {
        public final List<ItemStack> inputs = new ArrayList<>();
        public final List<ItemStack> outputs = new ArrayList<>();
        public final List<FluidStack> fluids = new ArrayList<>();
    }
}
