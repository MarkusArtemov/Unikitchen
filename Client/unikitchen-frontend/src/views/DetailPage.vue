<!-- src/views/DetailPage.vue -->
<template>
  <div>
    <h1>{{ recipe.name }}</h1>
    <p>{{ recipe.description }}</p>
    <p><strong>Schwierigkeit:</strong> {{ recipe.difficulty }}</p>
    <p><strong>Zubereitungszeit:</strong> {{ recipe.preparationTime }} Minuten</p>
    <p><strong>Zutaten:</strong></p>
    <ul>
      <li v-for="ingredient in recipe.ingredients" :key="ingredient.id">{{ ingredient.name }} - {{ ingredient.amount }}</li>
    </ul>
    <p><strong>Bewertungen:</strong> {{ recipe.averageRating }} / 5</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'DetailPage',
  data() {
    return {
      recipe: {},
    };
  },
  async created() {
    const recipeId = this.$route.params.id;
    try {
      const response = await axios.get(`/api/recipes/${recipeId}`);
      this.recipe = response.data;
    } catch (error) {
      console.error("Fehler beim Abrufen der Rezeptdetails:", error);
    }
  },
};
</script>

<style>

</style>
