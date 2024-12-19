<template>
  <div class="section">
    <h3>Meine Rezepte</h3>
    <div class="recipes-grid">
      <MenuCard
        v-for="recipe in myRecipes"
        :key="recipe.id"
        :recipe="recipe"
        :to="{ name: 'Detail', params: { id: recipe.id } }"
      />
    </div>
  </div>
</template>

<script>
import { fetchUserRecipes, fetchRecipeImage } from "@/services/RecipeService";
import MenuCard from "@/components/MenuCard.vue";

export default {
  name: "AccountMyRecipes",
  components: { MenuCard },
  props: {
    token: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      myRecipes: [],
      loading: true,
      error: false,
    };
  },
  async created() {
    try {
      const recipes = await fetchUserRecipes(this.token);
      this.myRecipes = recipes;
      for (const recipe of this.myRecipes) {
        await fetchRecipeImage(recipe);
      }
      this.loading = false;
    } catch (error) {
      console.error("Error loading user recipes:", error);
      this.loading = false;
      this.error = true;
    }
  },
};
</script>

<style scoped>
.section {
  padding: 20px;
}
h3 {
  text-align: center;
}
.recipes-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
}
</style>
