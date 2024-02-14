package com.onlyex.naxtech.api.utils;

import com.onlyex.naxtech.api.recipes.research.builder.*;
import com.onlyex.naxtech.common.ConfigHolder;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IDataItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.ingredients.nbtmatch.NBTCondition;
import gregtech.api.recipes.ingredients.nbtmatch.NBTMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import static com.onlyex.naxtech.api.recipes.NTRecipeMaps.RESEARCH_RECIPES;
import static com.onlyex.naxtech.api.utils.ResearchId.*;

public class AALManager {

    /**
     * 首先判断stack的getItem是否是一个MetaItem（元数据物品），如果不是则直接返回false，表示不是数据项。
     * 如果是MetaItem，则将其强制转换为MetaItem<?>类型的变量metaItem。
     * 利用metaItem的getItem方法，从stack中获取一个MetaItem<?>.MetaValueItem对象valueItem。
     * 如果valueItem为null，表示无法获取到MetaItem的值，直接返回false，表示不是数据项。
     * 接下来，遍历valueItem的所有行为（behaviours），对于每个行为，判断是否是一个IDataItem（数据项）。
     * 如果是IDataItem，判断该数据项是否需要数据存储器（requireDataBank）。
     * 如果不需要数据存储器，或者isDataBank为true，则返回true，表示是一个数据项。
     * 如果未找到匹配的IDataItem行为，或者需要数据存储器但isDataBank为false，则返回false，表示不是数据项。
     */
    public static boolean isStackDataItem(@NotNull ItemStack stack, boolean isDataBank) {
        if (stack.getItem() instanceof MetaItem<?> metaItem) {
            MetaItem<?>.MetaValueItem valueItem = metaItem.getItem(stack);
            if (valueItem == null) return true;
            for (IItemBehaviour behaviour : valueItem.getBehaviours()) {
                if (behaviour instanceof IDataItem dataItem) {
                    return dataItem.requireDataBank() && !isDataBank;
                }
            }
        }
        return true;
    }

    /**
     * Create the default research recipe
     *通过访问ConfigHolder.machines.enableResearch来检查是否启用了研究功能。
     * 如果没有启用研究功能，则直接返回，不执行后续操作。
     * 接着，使用builder的getRecipeEntries方法获取所有的配方条目，然后依次遍历这些条目。
     * 对于每个条目，通过调用对应的getter方法获取研究ID（researchId）、
     * 研究物品堆栈（researchStack）、数据物品堆栈（dataStack）、忽略NBT标签（ignoreNBT）、
     * 持续时间（duration）、EUt值（eUt）和RWUt值（RWUt）。
     * 然后，调用createDefaultResearchRecipe方法，并传入上述获取到的参数，用于创建默认的研究配方。
     * 这段代码的作用是通过Builder模式创建默认的研究配方。
     * 根据配置文件中的设置，如果启用了研究功能，并且存在配方条目，就依次创建对应的默认研究配方。
     */
    public static void createResearchRecipe(@NotNull AALRecipeBuilder builder) {
        if (!ConfigHolder.machines.enableResearch) return;

        for (AALRecipeBuilder.ResearchRecipeEntry entry : builder.getRecipeEntries()) {
            createResearchRecipe(
                    entry.getResearchId(),
                    entry.getResearchStack(),
                    entry.getDataStack(),
                    entry.getIgnoreNBT(),
                    entry.getDuration(),
                    entry.getEUt(),
                    entry.getCWUt(),
                    entry.getRWUt()
            );
        }
    }

    public static void createResearchRecipe(
            @NotNull String researchId, @NotNull ItemStack researchItem, @NotNull ItemStack dataItem,
                                            boolean ignoreNBT, int duration, int EUt, int CWUt, int RWUt) {
        if (!ConfigHolder.machines.enableResearch) return;

        NBTTagCompound compound = NTUtility.getOrCreateNbtCompound(dataItem);
        writeResearchToNBT(compound, researchId);

        if (RWUt > 0) {
            RecipeBuilder<?> researchBuilder = RESEARCH_RECIPES.recipeBuilder()
                    .inputNBT(dataItem.getItem(), 1, dataItem.getMetadata(), NBTMatcher.ANY, NBTCondition.ANY)
                    .outputs(dataItem)
                    .EUt(EUt)
                    .CWUt(CWUt)
                    .totalCWU(duration)
                    .RWUt(RWUt)
                    .totalRWU(duration);

            if (ignoreNBT) {
                researchBuilder.inputNBT(researchItem.getItem(), 1, researchItem.getMetadata(), NBTMatcher.ANY,
                        NBTCondition.ANY);
            } else {
                researchBuilder.inputs(researchItem);
            }

            researchBuilder.buildAndRegister();
        }
    }

    public static void createGOResearchRecipe(@NotNull GOAALRecipeBuilder builder) {
        if (!ConfigHolder.machines.enableResearch) return;

        for (GOAALRecipeBuilder.GOResearchRecipeEntry entry : builder.getGORecipeEntries()) {
            createGOResearchRecipe(
                    entry.getResearchId(),
                    entry.getResearchStack(),
                    entry.getDataStack(),
                    entry.getIgnoreNBT(),
                    entry.getDuration(),
                    entry.getEUt(),
                    entry.getCWUt(),
                    entry.getRWUt(),
                    entry.getGORWUt()
            );
        }
    }

    public static void createGOResearchRecipe(
            @NotNull String goresearchId, @NotNull ItemStack researchItem, @NotNull ItemStack dataItem,
            boolean ignoreNBT, int duration, int EUt, int CWUt, int RWUt, int GORWUt) {
        if (!ConfigHolder.machines.enableResearch) return;

        NBTTagCompound compound = NTUtility.getOrCreateNbtCompound(dataItem);
        writeGOResearchToNBT(compound, goresearchId);

        if (GORWUt > 0) {
            RecipeBuilder<?> researchBuilder = RESEARCH_RECIPES.recipeBuilder()
                    .inputNBT(dataItem.getItem(), 1, dataItem.getMetadata(), NBTMatcher.ANY, NBTCondition.ANY)
                    .outputs(dataItem)
                    .EUt(EUt)
                    .CWUt(CWUt)
                    .totalCWU(duration)
                    .RWUt(RWUt)
                    .totalRWU(duration)
                    .GORWUt(GORWUt)
                    .totalGORWU(duration);

            if (ignoreNBT) {
                researchBuilder.inputNBT(researchItem.getItem(), 1, researchItem.getMetadata(), NBTMatcher.ANY,
                        NBTCondition.ANY);
            } else {
                researchBuilder.inputs(researchItem);
            }

            researchBuilder.buildAndRegister();
        }
    }

    public static void createOPResearchRecipe(@NotNull OPAALRecipeBuilder builder) {
        if (!ConfigHolder.machines.enableResearch) return;

        for (OPAALRecipeBuilder.OPResearchRecipeEntry entry : builder.getOPRecipeEntries()) {
            createOPResearchRecipe(
                    entry.getResearchId(),
                    entry.getResearchStack(),
                    entry.getDataStack(),
                    entry.getIgnoreNBT(),
                    entry.getDuration(),
                    entry.getEUt(),
                    entry.getCWUt(),
                    entry.getRWUt(),
                    entry.getGORWUt(),
                    entry.getOPRWUt()
            );
        }
    }

    public static void createOPResearchRecipe(
            @NotNull String opresearchId, @NotNull ItemStack researchItem, @NotNull ItemStack dataItem,
                                              boolean ignoreNBT, int duration, int EUt, int CWUt, int RWUt, int GORWUt, int OPRWUt) {
        if (!ConfigHolder.machines.enableResearch) return;

        NBTTagCompound compound = NTUtility.getOrCreateNbtCompound(dataItem);
        writeOPResearchToNBT(compound, opresearchId);

        if (OPRWUt > 0) {
            RecipeBuilder<?> researchBuilder = RESEARCH_RECIPES.recipeBuilder()
                    .inputNBT(dataItem.getItem(), 1, dataItem.getMetadata(), NBTMatcher.ANY, NBTCondition.ANY)
                    .outputs(dataItem)
                    .EUt(EUt)
                    .CWUt(CWUt)
                    .totalCWU(duration)
                    .RWUt(RWUt)
                    .totalRWU(duration)
                    .GORWUt(GORWUt)
                    .totalGORWU(duration)
                    .OPRWUt(OPRWUt)
                    .totalOPRWU(duration);

            if (ignoreNBT) {
                researchBuilder.inputNBT(researchItem.getItem(), 1, researchItem.getMetadata(), NBTMatcher.ANY,
                        NBTCondition.ANY);
            } else {
                researchBuilder.inputs(researchItem);
            }

            researchBuilder.buildAndRegister();
        }
    }

    public static void createSPResearchRecipe(@NotNull SPAALRecipeBuilder builder) {
        if (!ConfigHolder.machines.enableResearch) return;

        for (SPAALRecipeBuilder.SPResearchRecipeEntry entry : builder.getSPRecipeEntries()) {
            createSPResearchRecipe(
                    entry.getResearchId(),
                    entry.getResearchStack(),
                    entry.getDataStack(),
                    entry.getIgnoreNBT(),
                    entry.getDuration(),
                    entry.getEUt(),
                    entry.getCWUt(),
                    entry.getRWUt(),
                    entry.getGORWUt(),
                    entry.getOPRWUt(),
                    entry.getSPRWUt()
            );
        }
    }

    public static void createSPResearchRecipe(
            @NotNull String spresearchId, @NotNull ItemStack researchItem, @NotNull ItemStack dataItem,
                                              boolean ignoreNBT, int duration, int EUt, int CWUt, int RWUt, int GORWUt, int OPRWUt,
                                              int SPRWUt) {
        if (!ConfigHolder.machines.enableResearch) return;

        NBTTagCompound compound = NTUtility.getOrCreateNbtCompound(dataItem);
        writeSPResearchToNBT(compound, spresearchId);

        if (SPRWUt > 0) {
            RecipeBuilder<?> researchBuilder = RESEARCH_RECIPES.recipeBuilder()
                    .inputNBT(dataItem.getItem(), 1, dataItem.getMetadata(), NBTMatcher.ANY, NBTCondition.ANY)
                    .outputs(dataItem)
                    .EUt(EUt)
                    .CWUt(CWUt)
                    .totalCWU(duration)
                    .RWUt(RWUt)
                    .totalRWU(duration)
                    .GORWUt(GORWUt)
                    .totalGORWU(duration)
                    .OPRWUt(OPRWUt)
                    .totalOPRWU(duration)
                    .SPRWUt(SPRWUt)
                    .totalSPRWU(duration);

            if (ignoreNBT) {
                researchBuilder.inputNBT(researchItem.getItem(), 1, researchItem.getMetadata(), NBTMatcher.ANY,
                        NBTCondition.ANY);
            } else {
                researchBuilder.inputs(researchItem);
            }

            researchBuilder.buildAndRegister();
        }
    }

    public static void createCOResearchRecipe(@NotNull COAALRecipeBuilder builder) {
        if (!ConfigHolder.machines.enableResearch) return;

        for (COAALRecipeBuilder.COResearchRecipeEntry entry : builder.getCORecipeEntries()) {
            createCOResearchRecipe(
                    entry.getResearchId(),
                    entry.getResearchStack(),
                    entry.getDataStack(),
                    entry.getIgnoreNBT(),
                    entry.getDuration(),
                    entry.getEUt(),
                    entry.getCWUt(),
                    entry.getRWUt(),
                    entry.getGORWUt(),
                    entry.getOPRWUt(),
                    entry.getSPRWUt(),
                    entry.getCORWUt()
            );
        }
    }

    public static void createCOResearchRecipe(
            @NotNull String coresearchId, @NotNull ItemStack researchItem, @NotNull ItemStack dataItem,
                                              boolean ignoreNBT, int duration, int EUt, int CWUt, int RWUt, int GORWUt, int OPRWUt,
                                              int SPRWUt, int CORWUt) {
        if (!ConfigHolder.machines.enableResearch) return;

        NBTTagCompound compound = NTUtility.getOrCreateNbtCompound(dataItem);
        writeCOResearchToNBT(compound, coresearchId);

        if (CORWUt > 0) {
            RecipeBuilder<?> researchBuilder = RESEARCH_RECIPES.recipeBuilder()
                    .inputNBT(dataItem.getItem(), 1, dataItem.getMetadata(), NBTMatcher.ANY, NBTCondition.ANY)
                    .outputs(dataItem)
                    .EUt(EUt)
                    .CWUt(CWUt)
                    .totalCWU(duration)
                    .RWUt(RWUt)
                    .totalRWU(duration)
                    .GORWUt(GORWUt)
                    .totalGORWU(duration)
                    .OPRWUt(OPRWUt)
                    .totalOPRWU(duration)
                    .SPRWUt(SPRWUt)
                    .totalSPRWU(duration)
                    .CORWUt(CORWUt)
                    .totalCORWU(duration);

            if (ignoreNBT) {
                researchBuilder.inputNBT(researchItem.getItem(), 1, researchItem.getMetadata(), NBTMatcher.ANY,
                        NBTCondition.ANY);
            } else {
                researchBuilder.inputs(researchItem);
            }

            researchBuilder.buildAndRegister();
        }
    }

    public static void createSCAResearchRecipe(@NotNull SCAAALRecipeBuilder builder) {
        if (!ConfigHolder.machines.enableResearch) return;

        for (SCAAALRecipeBuilder.SCAResearchRecipeEntry entry : builder.getSCARecipeEntries()) {
            createSCAResearchRecipe(
                    entry.getResearchId(),
                    entry.getResearchStack(),
                    entry.getDataStack(),
                    entry.getIgnoreNBT(),
                    entry.getDuration(),
                    entry.getEUt(),
                    entry.getCWUt(),
                    entry.getRWUt(),
                    entry.getGORWUt(),
                    entry.getOPRWUt(),
                    entry.getSPRWUt(),
                    entry.getCORWUt(),
                    entry.getSCARWUt()
            );
        }
    }

    public static void createSCAResearchRecipe(
            @NotNull String scaresearchId, @NotNull ItemStack researchItem, @NotNull ItemStack dataItem,
                                               boolean ignoreNBT, int duration, int EUt, int CWUt, int RWUt, int GORWUt, int OPRWUt,
                                               int SPRWUt, int CORWUt, int SCARWUt) {
        if (!ConfigHolder.machines.enableResearch) return;

        NBTTagCompound compound = NTUtility.getOrCreateNbtCompound(dataItem);
        writeSCAResearchToNBT(compound, scaresearchId);

        if (SCARWUt > 0) {
            RecipeBuilder<?> researchBuilder = RESEARCH_RECIPES.recipeBuilder()
                    .inputNBT(dataItem.getItem(), 1, dataItem.getMetadata(), NBTMatcher.ANY, NBTCondition.ANY)
                    .outputs(dataItem)
                    .EUt(EUt)
                    .CWUt(CWUt)
                    .totalCWU(duration)
                    .RWUt(RWUt)
                    .totalRWU(duration)
                    .GORWUt(GORWUt)
                    .totalGORWU(duration)
                    .OPRWUt(OPRWUt)
                    .totalOPRWU(duration)
                    .SPRWUt(SPRWUt)
                    .totalSPRWU(duration)
                    .CORWUt(CORWUt)
                    .totalCORWU(duration)
                    .SCARWUt(SCARWUt)
                    .totalSCARWU(duration);

            if (ignoreNBT) {
                researchBuilder.inputNBT(researchItem.getItem(), 1, researchItem.getMetadata(), NBTMatcher.ANY,
                        NBTCondition.ANY);
            } else {
                researchBuilder.inputs(researchItem);
            }

            researchBuilder.buildAndRegister();
        }
    }

    public static void createSCHResearchRecipe(@NotNull SCHAALRecipeBuilder builder) {
        if (!ConfigHolder.machines.enableResearch) return;

        for (SCHAALRecipeBuilder.SCHResearchRecipeEntry entry : builder.getSCHRecipeEntries()) {
            createSCHResearchRecipe(
                    entry.getResearchId(),
                    entry.getResearchStack(),
                    entry.getDataStack(),
                    entry.getIgnoreNBT(),
                    entry.getDuration(),
                    entry.getEUt(),
                    entry.getCWUt(),
                    entry.getRWUt(),
                    entry.getGORWUt(),
                    entry.getOPRWUt(),
                    entry.getSPRWUt(),
                    entry.getCORWUt(),
                    entry.getSCARWUt(),
                    entry.getSCHRWUt()
            );
        }
    }

    public static void createSCHResearchRecipe(
            @NotNull String schresearchId, @NotNull ItemStack researchItem, @NotNull ItemStack dataItem,
                                               boolean ignoreNBT, int duration, int EUt, int CWUt, int RWUt, int GORWUt, int OPRWUt,
                                               int SPRWUt, int CORWUt, int SCARWUt, int SCHRWUt) {
        if (!ConfigHolder.machines.enableResearch) return;

        NBTTagCompound compound = NTUtility.getOrCreateNbtCompound(dataItem);
        writeSCHResearchToNBT(compound, schresearchId);

        if (SCHRWUt > 0) {
            RecipeBuilder<?> researchBuilder = RESEARCH_RECIPES.recipeBuilder()
                    .inputNBT(dataItem.getItem(), 1, dataItem.getMetadata(), NBTMatcher.ANY, NBTCondition.ANY)
                    .outputs(dataItem)
                    .EUt(EUt)
                    .CWUt(CWUt)
                    .totalCWU(duration)
                    .RWUt(RWUt)
                    .totalRWU(duration)
                    .GORWUt(GORWUt)
                    .totalGORWU(duration)
                    .OPRWUt(OPRWUt)
                    .totalOPRWU(duration)
                    .SPRWUt(SPRWUt)
                    .totalSPRWU(duration)
                    .CORWUt(CORWUt)
                    .totalCORWU(duration)
                    .SCARWUt(SCARWUt)
                    .totalSCARWU(duration)
                    .SCHRWUt(SCHRWUt)
                    .totalSCHRWU(duration);

            if (ignoreNBT) {
                researchBuilder.inputNBT(researchItem.getItem(), 1, researchItem.getMetadata(), NBTMatcher.ANY,
                        NBTCondition.ANY);
            } else {
                researchBuilder.inputs(researchItem);
            }

            researchBuilder.buildAndRegister();
        }
    }

    public static void createSDIResearchRecipe(@NotNull SDIAALRecipeBuilder builder) {
        if (!ConfigHolder.machines.enableResearch) return;

        for (SDIAALRecipeBuilder.SDIResearchRecipeEntry entry : builder.getSDIRecipeEntries()) {
            createSDIResearchRecipe(
                    entry.getResearchId(),
                    entry.getResearchStack(),
                    entry.getDataStack(),
                    entry.getIgnoreNBT(),
                    entry.getDuration(),
                    entry.getEUt(),
                    entry.getCWUt(),
                    entry.getRWUt(),
                    entry.getGORWUt(),
                    entry.getOPRWUt(),
                    entry.getSPRWUt(),
                    entry.getCORWUt(),
                    entry.getSCARWUt(),
                    entry.getSCHRWUt(),
                    entry.getSDIRWUt()
            );
        }
    }

    public static void createSDIResearchRecipe(
            @NotNull String sdiresearchId, @NotNull ItemStack researchItem, @NotNull ItemStack dataItem,
                                               boolean ignoreNBT, int duration, int EUt, int CWUt, int RWUt, int GORWUt, int OPRWUt,
                                               int SPRWUt, int CORWUt, int SCARWUt, int SCHRWUt, int SDIRWUt) {
        if (!ConfigHolder.machines.enableResearch) return;

        NBTTagCompound compound = NTUtility.getOrCreateNbtCompound(dataItem);
        writeSDIResearchToNBT(compound, sdiresearchId);

        if (SDIRWUt > 0) {
            RecipeBuilder<?> researchBuilder = RESEARCH_RECIPES.recipeBuilder()
                    .inputNBT(dataItem.getItem(), 1, dataItem.getMetadata(), NBTMatcher.ANY, NBTCondition.ANY)
                    .outputs(dataItem)
                    .EUt(EUt)
                    .CWUt(CWUt)
                    .totalCWU(duration)
                    .RWUt(RWUt)
                    .totalRWU(duration)
                    .GORWUt(GORWUt)
                    .totalGORWU(duration)
                    .OPRWUt(OPRWUt)
                    .totalOPRWU(duration)
                    .SPRWUt(SPRWUt)
                    .totalSPRWU(duration)
                    .CORWUt(CORWUt)
                    .totalCORWU(duration)
                    .SCARWUt(SCARWUt)
                    .totalSCARWU(duration)
                    .SCHRWUt(SCHRWUt)
                    .totalSCHRWU(duration)
                    .SDIRWUt(SDIRWUt)
                    .totalSDIRWU(duration);

            if (ignoreNBT) {
                researchBuilder.inputNBT(researchItem.getItem(), 1, researchItem.getMetadata(), NBTMatcher.ANY,
                        NBTCondition.ANY);
            } else {
                researchBuilder.inputs(researchItem);
            }

            researchBuilder.buildAndRegister();
        }
    }


    /*public static class DataStickCopyScannerLogic implements IScannerRecipeMap.ICustomScannerLogic {

        private static final int EUT = 2;
        private static final int DURATION = 100;

        *//*
         * 在 createCustomRecipe 方法中，根据输入的物品列表的大小判断是否有足够的输入来创建配方。
         * 如果输入物品列表的大小大于 1，则尝试使用 createDataRecipe 方法创建数据配方，并且可以优先选择第一个物品来覆盖第二个物品。
         * 如果创建成功，则返回配方对象。如果没有足够的输入，则返回 null。
         *//*
        @Override
        public Recipe createCustomRecipe(long voltage, List<ItemStack> inputs, List<FluidStack> fluidInputs,
                                         boolean exactVoltage) {
            if (inputs.size() > 1) {
                // 双向尝试数据配方，优先覆盖第一个
                Recipe recipe = createDataRecipe(inputs.get(0), inputs.get(1));
                if (recipe != null) return recipe;

                return createDataRecipe(inputs.get(1), inputs.get(0));
            }
            return null;
        }

        *//*
         *createDataRecipe 方法用于创建数据配方。
         * 它首先检查第二个物品是否有 NBTTagCompound，如果没有则返回 null。
         * 然后，它检查第一个和第二个物品是否均为数据物品，如果不是则返回 null。
         * 接下来，它创建一个新的 ItemStack 对象作为输出，将第一个物品的复制和第二个物品的 NBTTagCompound 复制设置到输出中，
         * 并使用 RecipeMaps.SCANNER_RECIPES.recipeBuilder() 创建一个 RecipeBuilder 对象来构建配方，
         * 指定输入、不会被消耗的物品、输出、持续时间和 EUt，最后使用 build() 方法构建配方并返回结果。
         *//*
        @Nullable
        private Recipe createDataRecipe(@NotNull ItemStack first, @NotNull ItemStack second) {
            NBTTagCompound compound = second.getTagCompound();
            if (compound == null) return null;

            // 两者都必须是数据项
            if (isStackDataItem(first, true)) return null;
            if (isStackDataItem(second, true)) return null;

            ItemStack output = first.copy();
            output.setTagCompound(compound.copy());
            return RecipeMaps.SCANNER_RECIPES.recipeBuilder()
                    .inputs(first)
                    .notConsumable(second)
                    .outputs(output)
                    .duration(DURATION).EUt(EUT).build().getResult();
        }

        @Nullable
        @Override
        public List<Recipe> getRepresentativeRecipes() {
            ItemStack copiedStick = NTMetaItems.RESEARCH_DATA_CARD.getStackForm();
            copiedStick.setTranslatableName("naxtech.scanner.copy_stick_from");
            ItemStack emptyStick = NTMetaItems.RESEARCH_DATA_CARD.getStackForm();
            emptyStick.setTranslatableName("naxtech.scanner.copy_stick_empty");
            ItemStack resultStick = NTMetaItems.RESEARCH_DATA_CARD.getStackForm();
            resultStick.setTranslatableName("naxtech.scanner.copy_stick_to");
            return Collections.singletonList(
                    RecipeMaps.SCANNER_RECIPES.recipeBuilder()
                            .inputs(emptyStick)
                            .notConsumable(copiedStick)
                            .outputs(resultStick)
                            .duration(DURATION).EUt(EUT)
                            .build().getResult());
        }
    }*/

    private AALManager() {}
}
