<template>
  <div>
    <h1>{{ recipe.name }}</h1>
    <p>{{ recipe.preparation }}</p>
    <p><strong>Schwierigkeitsgrad:</strong> {{ recipe.difficultyLevel }}</p>
    <p><strong>Zubereitungszeit:</strong> {{ recipe.duration }} Minuten</p>
    <p><strong>Zutaten:</strong></p>
    <ul>
      <li v-for="ingredient in recipe.ingredients" :key="ingredient.id">
        {{ ingredient.name }} - {{ ingredient.amount }}
      </li>
    </ul>
    <p><strong>Bewertungen:</strong> Noch keine Bewertungen</p>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "DetailPage",
  data() {
    return {
      recipe: {},
    };
  },
  async created() {
    const recipeId = this.$route.params.id;
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get(`/api/recipes/${recipeId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      console.log(response.data);
      this.recipe = response.data;
    } catch (error) {
      console.error("Fehler beim Abrufen der Rezeptdetails:", error);
    }
  },
};
</script>

<style></style>
