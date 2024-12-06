<template>
  <div class="section">
    <h3>Neues Rezept erstellen</h3>
    <form @submit.prevent="submitRecipe">
      <div class="form-group">
        <label for="name">Rezeptname:</label>
        <input type="text" id="name" v-model="recipe.name" required />
      </div>
      <div class="form-group">
        <label for="price">Preis (in €):</label>
        <input
          type="number"
          id="price"
          v-model="recipe.price"
          required
          step="0.01"
        />
      </div>
      <div class="form-group">
        <label for="duration">Dauer (in Minuten):</label>
        <input
          type="number"
          id="duration"
          v-model="recipe.duration"
          required
          step="1"
        />
      </div>
      <div class="form-group">
        <label for="preparation">Zubereitung:</label>
        <textarea
          id="preparation"
          v-model="recipe.preparation"
          required
        ></textarea>
      </div>
      <div class="form-group">
        <label>Zutaten:</label>
        <ul>
          <li v-for="(ingredient, index) in recipe.ingredients" :key="index">
            <input v-model="ingredient.name" placeholder="Zutat" required />
            <input
              type="number"
              v-model="ingredient.quantity"
              placeholder="Menge"
              required
              step="0.01"
            />
            <input
              v-model="ingredient.unit"
              placeholder="Einheit (z.B. g, ml)"
              required
            />
            <button
              type="button"
              @click="removeIngredient(index)"
              class="remove-button"
            >
              Entfernen
            </button>
          </li>
        </ul>
        <button type="button" @click="addIngredient" class="add-button">
          Zutat hinzufügen
        </button>
      </div>
      <div class="form-group">
        <label for="category">Kategorie:</label>
        <select id="category" v-model="recipe.category" required>
          <option v-for="cat in categories" :key="cat" :value="cat">
            {{ cat }}
          </option>
        </select>
      </div>
      <div class="form-group">
        <label for="difficultyLevel">Schwierigkeitsgrad:</label>
        <select id="difficultyLevel" v-model="recipe.difficultyLevel" required>
          <option value="EINFACH">EINFACH</option>
          <option value="MITTEL">MITTEL</option>
          <option value="SCHWIERIG">SCHWIERIG</option>
        </select>
      </div>
      <div class="form-group">
        <label for="image">Bild hochladen (optional):</label>
        <input
          type="file"
          id="image"
          @change="onImageChange"
          accept="image/*"
        />
      </div>
      <button type="submit" class="submit-button">Rezept speichern</button>
    </form>
  </div>
</template>

<script>
import axios from "axios";
export default {
  name: "AccountCreateRecipe",
  props: {
    token: {
      type: String,
      required: true,
    },
    categories: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      recipeImage: null,
      recipe: {
        name: "",
        price: null,
        duration: null,
        preparation: "",
        ingredients: [{ name: "", quantity: null, unit: "" }],
        category: "",
        difficultyLevel: "",
      },
    };
  },
  methods: {
    addIngredient() {
      this.recipe.ingredients.push({ name: "", quantity: null, unit: "" });
    },
    removeIngredient(index) {
      this.recipe.ingredients.splice(index, 1);
    },
    onImageChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.recipeImage = file;
      }
    },
    async submitRecipe() {
      try {
        const response = await axios.post(
          "http://localhost:8080/api/recipes",
          this.recipe,
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
              "Content-Type": "application/json",
            },
          }
        );
        const createdRecipe = response.data;

        if (this.recipeImage) {
          const formData = new FormData();
          formData.append("image", this.recipeImage);
          await axios.post(
            `http://localhost:8080/api/recipes/${createdRecipe.id}/upload-recipe-image`,
            formData,
            {
              headers: {
                Authorization: `Bearer ${this.token}`,
              },
            }
          );
        }

        alert("Rezept erfolgreich gespeichert!");
        this.$emit("recipe-created", createdRecipe);
        this.recipe = {
          name: "",
          price: null,
          duration: null,
          preparation: "",
          ingredients: [{ name: "", quantity: null, unit: "" }],
          category: "",
          difficultyLevel: "",
        };
        this.recipeImage = null;
      } catch (error) {
        console.error("Error submitting recipe:", error);
        alert("Fehler beim Speichern des Rezepts!");
      }
    },
  },
};
</script>
