package com.onlyex.naxtech.api.recipes.research.builder;

import com.onlyex.naxtech.api.recipes.recipeproperties.research.ResearchProperty;
import com.onlyex.naxtech.api.recipes.recipeproperties.research.ResearchPropertyData;
import com.onlyex.naxtech.api.recipes.research.ResearchRecipeBuilder;
import com.onlyex.naxtech.api.utils.NTLog;
import com.onlyex.naxtech.common.ConfigHolder;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.util.EnumValidationResult;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

public class GOResearchLineRecipeBuilder extends RecipeBuilder<GOResearchLineRecipeBuilder> {
    private final Collection<GOResearchRecipeEntry> recipeGOEntries = new ArrayList<>();
    private boolean generatingRecipes = true;

    public GOResearchLineRecipeBuilder() {}

    @SuppressWarnings("unused")
    public GOResearchLineRecipeBuilder(Recipe recipe, RecipeMap<GOResearchLineRecipeBuilder> recipeMap) {
        super(recipe, recipeMap);
    }

    public GOResearchLineRecipeBuilder(@NotNull GOResearchLineRecipeBuilder builder) {
        super(builder);
        this.recipeGOEntries.addAll(builder.getGORecipeEntries());
        this.generatingRecipes = builder.generatingRecipes;
    }

    @Override
    public GOResearchLineRecipeBuilder copy() {
        return new GOResearchLineRecipeBuilder(this);
    }

    private boolean applyResearchProperty(ResearchPropertyData.ResearchEntry researchEntry) {
        if (!ConfigHolder.machines.enableResearch) return false;
        if (researchEntry == null) {
            NTLog.logger.error("研究线研究条目不能为空。", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
            return false;
        }

        if (!generatingRecipes) {
            NTLog.logger.error("使用researchWithoutRecipe（）时无法生成配方",
                    new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
            return false;
        }

        if (recipePropertyStorage != null && recipePropertyStorage.hasRecipeProperty(ResearchProperty.getInstance())) {
            ResearchPropertyData property = recipePropertyStorage.getRecipePropertyValue(ResearchProperty.getInstance(),
                    null);
            if (property == null) throw new IllegalStateException("该属性的值为空");
            property.add(researchEntry);
            return true;
        }

        ResearchPropertyData property = new ResearchPropertyData();
        if (applyProperty(ResearchProperty.getInstance(), property)) {
            property.add(researchEntry);
            return true;
        }

        return false;
    }

    /**
     * 为研究站生成研究配方。
     */
    //
    public GOResearchLineRecipeBuilder stationGOResearch(UnaryOperator<ResearchRecipeBuilder.StationRecipeBuilder> goresearch) {
        GOResearchRecipeEntry entry = goresearch.apply(new ResearchRecipeBuilder.StationRecipeBuilder()).goresearch();
        if (applyResearchProperty(new ResearchPropertyData.ResearchEntry(entry.researchId, entry.dataStack))) {
            this.recipeGOEntries.add(entry);
        }
        return this;
    }

    @NotNull
    public Collection<GOResearchRecipeEntry> getGORecipeEntries() {
        return this.recipeGOEntries;
    }

    /**
     * 用于生成包含研究数据的数据项的自动生成研究配方的条目。
     */

    public static class GOResearchRecipeEntry {

        private final String researchId;
        private final List<GTRecipeInput> inputs;
        private final ItemStack researchStack;
        private final ItemStack dataStack;
        private final boolean ignoreNBT;
        private final int duration;
        private final int EUt;
        private final int CWUt;
        private final int RWUt;
        private final int GORWUt;

        /**
         * @param researchId    要存储的研究的id
         * @param researchStack 要扫描以进行研究的堆栈
         * @param dataStack     包含数据的堆栈
         * @param duration      配方的持续时间
         * @param EUt           配方的EUt
         * @param RWUt          如果在研究站，这个配方每tick需要多少RWUt
         *                      <p>
         *                      默认情况下，将在Research chStack输入上忽略NBT。如果需要NBT匹配，请参阅
         *
         */
        public GOResearchRecipeEntry(@NotNull String researchId, @NotNull List<GTRecipeInput> inputs, @NotNull ItemStack researchStack,
                                   @NotNull ItemStack dataStack, int duration, int EUt,
                                     int CWUt,int RWUt, int GORWUt) {
            this.researchId = researchId;
            this.inputs = inputs;
            this.researchStack = researchStack;
            this.dataStack = dataStack;
            this.duration = duration;
            this.EUt = EUt;
            this.CWUt = CWUt;
            this.RWUt = RWUt;
            this.GORWUt = GORWUt;
            this.ignoreNBT = true;
        }

        /**
         * @param researchId    要存储的研究的id
         * @param researchStack 要扫描以进行研究的堆栈
         * @param dataStack     包含数据的堆栈
         * @param duration      配方的持续时间
         * @param EUt           配方的EUt
         * @param RWUt          如果在研究站，这个配方每tick需要多少RWUt
         */
        public GOResearchRecipeEntry(@NotNull String researchId, @NotNull List<GTRecipeInput> inputs, @NotNull ItemStack researchStack,
                                     @NotNull ItemStack dataStack, boolean ignoreNBT, int duration, int EUt,
                                     int CWUt, int RWUt, int GORWUt) {
            this.researchId = researchId;
            this.inputs = inputs;
            this.researchStack = researchStack;
            this.dataStack = dataStack;
            this.ignoreNBT = ignoreNBT;
            this.duration = duration;
            this.EUt = EUt;
            this.CWUt = CWUt;
            this.RWUt = RWUt;
            this.GORWUt = GORWUt;

        }

        @NotNull
        public String getResearchId() {
            return this.researchId;
        }
        @NotNull
        public List<GTRecipeInput> getInputs() {
            return inputs;
        }
        @NotNull
        public ItemStack getResearchStack() {
            return researchStack;
        }

        @NotNull
        public ItemStack getDataStack() {
            return dataStack;
        }

        public boolean getIgnoreNBT() {
            return ignoreNBT;
        }

        public int getDuration() {
            return duration;
        }

        public int getEUt() {
            return EUt;
        }
        public int getCWUt() {
            return CWUt;
        }
        public int getRWUt() {return RWUt;}
        public int getGORWUt() {
            return GORWUt;
        }
    }
}
