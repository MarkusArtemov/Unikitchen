<template>
  <div class="section">
    <h3>Meine Rezepte</h3>
    <div class="recipes-grid">
      <!-- Iterate over myRecipes and display each recipe in a MenuCard -->
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
    /**
     * The authentication token for making API requests.
     * @type {string}
     * @required
     */
    token: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      /**
       * List of the user's recipes.
       * @type {Array<Object>}
       */
      myRecipes: [],

      /**
       * Loading state to track if recipes are being fetched.
       * @type {boolean}
       */
      loading: true,

      /**
       * Error state to handle potential fetch errors.
       * @type {boolean}
       */
      error: false,
    };
  },
  async created() {
    try {
      // Fetch the user's recipes
      const recipes = await fetchUserRecipes(this.token);
      this.myRecipes = recipes;

      // Fetch images for each recipe
      for (const recipe of this.myRecipes) {
        await fetchRecipeImage(recipe);
      }

      // Set loading to false after data is fetched
      this.loading = false;
    } catch (error) {
      console.error("Error loading user recipes:", error);

      // Handle error state
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
