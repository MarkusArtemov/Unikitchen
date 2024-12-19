<template>
  <div class="section">
    <h3>Favoriten</h3>

    <!-- Recipes Grid displaying favorite recipes -->
    <div class="recipes-grid">
      <!-- Loop through favoriteRecipes and render each as a MenuCard -->
      <MenuCard
          v-for="favorite in favoriteRecipes"
          :key="favorite.id"
          :recipe="favorite"
          :to="{ name: 'Detail', params: { id: favorite.id } }"
      />
    </div>
  </div>
</template>

<script>
import {fetchFavorites, fetchRecipeImage} from "@/services/RecipeService";
import MenuCard from "@/components/MenuCard.vue";

export default {
  name: "AccountFavorites",
  components: {MenuCard},
  props: {
    /**
     * Authentication token for API requests.
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
       * List of the user's favorite recipes.
       * @type {Array<Object>}
       */
      favoriteRecipes: [],
    };
  },
  async created() {
    /**
     * Fetches the list of favorite recipes and their images when the component is created.
     * @async
     */
    const favorites = await fetchFavorites(this.token);
    this.favoriteRecipes = favorites;

    // Fetch images for each recipe
    for (const fav of this.favoriteRecipes) {
      await fetchRecipeImage(fav);
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
