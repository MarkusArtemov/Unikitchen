<template>
  <div class="section">
    <h3>Neues Rezept erstellen</h3>
    <form @submit.prevent="submitRecipe">
      <!-- Recipe Name Input -->
      <div class="form-group">
        <label for="name">Rezeptname:</label>
        <input type="text" id="name" v-model="recipe.name" required />
      </div>

      <!-- Price Input (in EUR) -->
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

      <!-- Duration Input (in minutes) -->
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

      <!-- Preparation Textarea -->
      <div class="form-group">
        <label for="preparation">Zubereitung:</label>
        <textarea
            id="preparation"
            v-model="recipe.preparation"
            required
        ></textarea>
      </div>

      <!-- Ingredients List -->
      <div class="form-group">
        <label>Zutaten:</label>
        <ul>
          <!-- Loop through ingredients and display input fields -->
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
        <!-- Add new ingredient button -->
        <button type="button" @click="addIngredient" class="add-button">
          Zutat hinzufügen
        </button>
      </div>

      <!-- Category Selection -->
      <div class="form-group">
        <label for="category">Kategorie:</label>
        <select id="category" v-model="recipe.category" required>
          <option v-for="cat in categories" :key="cat" :value="cat">
            {{ cat }}
          </option>
        </select>
      </div>

      <!-- Difficulty Level Selection -->
      <div class="form-group">
        <label for="difficultyLevel">Schwierigkeitsgrad:</label>
        <select id="difficultyLevel" v-model="recipe.difficultyLevel" required>
          <option value="EINFACH">EINFACH</option>
          <option value="MITTEL">MITTEL</option>
          <option value="SCHWIERIG">SCHWIERIG</option>
        </select>
      </div>

      <!-- Image Upload Section -->
      <div class="form-group">
        <label for="image">Bild hochladen:</label>
        <input
            type="file"
            id="image"
            @change="onImageChange"
            accept="image/*"
            required
        />
        <div v-if="imagePreview" class="image-preview">
          <img :src="imagePreview" alt="Bildvorschau" />
        </div>
        <p v-if="imageError" class="error-message">{{ imageError }}</p>
      </div>

      <!-- Submit Button -->
      <button type="submit" class="submit-button">Rezept speichern</button>
    </form>
  </div>
</template>


<script>
import { createRecipe, uploadRecipeImage } from "@/services/RecipeService";

export default {
  name: "AccountCreateRecipe",
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
    /**
     * List of available categories for recipes.
     * @type {Array<string>}
     * @required
     */
    categories: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      recipeImage: null,
      imagePreview: null,
      imageError: null,
      recipe: {
        name: "",
        price: null,
        duration: null,
        difficultyLevel: "",
        category: "",
        preparation: "",
        ingredients: [{ name: "", quantity: null, unit: "" }],
      },
    };
  },
  methods: {
    /**
     * Adds a new ingredient to the ingredients list.
     */
    addIngredient() {
      this.recipe.ingredients.push({ name: "", quantity: null, unit: "" });
    },
    /**
     * Removes an ingredient from the ingredients list by index.
     * @param {number} index - The index of the ingredient to remove.
     */
    removeIngredient(index) {
      this.recipe.ingredients.splice(index, 1);
    },
    /**
     * Handles image file input change, generating a preview.
     * @param {Event} event - The file input change event.
     */
    onImageChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.recipeImage = file;
        this.imageError = null;
        this.imagePreview = URL.createObjectURL(file);
      } else {
        this.recipeImage = null;
        this.imagePreview = null;
      }
    },
    /**
     * Submits the recipe form, including the image upload.
     * @async
     */
    async submitRecipe() {
      if (!this.recipeImage) {
        this.imageError = "Bitte wählen Sie ein Bild aus.";
        return;
      }
      try {
        const createdRecipe = await createRecipe(this.token, this.recipe);
        if (this.recipeImage) {
          await uploadRecipeImage(
              this.token,
              createdRecipe.id,
              this.recipeImage
          );
        }
        alert("Rezept erfolgreich gespeichert!");
        this.$emit("recipe-created", createdRecipe);
        this.resetForm();
      } catch (error) {
        console.error("Error submitting recipe:", error);
        alert("Fehler beim Speichern des Rezepts!");
      }
    },
    resetForm() {
      this.recipe = {
        name: "",
        price: null,
        duration: null,
        difficultyLevel: "",
        category: "",
        preparation: "",
        ingredients: [{ name: "", quantity: null, unit: "" }],
      };
      this.recipeImage = null;
      this.imagePreview = null;
      this.imageError = null;
    },
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
.form-group {
  margin-bottom: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
}
input[type="text"],
input[type="number"],
select,
textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
textarea {
  resize: vertical;
}
.add-button,
.remove-button,
.submit-button {
  margin-top: 10px;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.remove-button {
  background-color: #dc3545;
}
.add-button:hover,
.submit-button:hover {
  background-color: #0056b3;
}
.remove-button:hover {
  background-color: #c82333;
}
.image-preview {
  margin-top: 10px;
  text-align: center;
}
.image-preview img {
  max-width: 200px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
.error-message {
  color: red;
  margin-top: 5px;
  font-size: 14px;
}
</style>
