<template>
  <div class="recipe-edit-page">
    <h1>Rezept bearbeiten</h1>
    <form @submit.prevent="updateRecipeData">
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
        <label for="image">Bild aktualisieren (optional):</label>
        <input
          type="file"
          id="image"
          @change="onImageChange"
          accept="image/*"
        />
      </div>

      <button type="submit" class="submit-button">Änderungen speichern</button>
    </form>
  </div>
</template>

<script>
import axios from "axios";
export default {
  name: "RecipeEditPage",
  props: {
    id: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      token: localStorage.getItem("token"),
      categories: ["vegetarisch", "fleisch", "kuchen", "nudeln", "reis"],
      recipeImage: null,
      recipe: {
        id: null,
        name: "",
        price: null,
        duration: null,
        preparation: "",
        ingredients: [],
        category: "",
        difficultyLevel: "",
      },
    };
  },
  async created() {
    await this.loadRecipeData();
  },
  methods: {
    async loadRecipeData() {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/recipes/${this.id}`,
          {
            headers: { Authorization: `Bearer ${this.token}` },
          }
        );
        const loadedRecipe = response.data;

        this.recipe.id = loadedRecipe.id;
        this.recipe.name = loadedRecipe.name;
        this.recipe.price = loadedRecipe.price;
        this.recipe.duration = loadedRecipe.duration;
        this.recipe.preparation = loadedRecipe.preparation;
        this.recipe.ingredients = loadedRecipe.ingredients;
        this.recipe.category = loadedRecipe.category;
        this.recipe.difficultyLevel = loadedRecipe.difficultyLevel;
      } catch (error) {
        console.error("Fehler beim Laden des Rezeptes:", error);
        alert("Fehler beim Laden des Rezeptes!");
      }
    },
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
    async updateRecipeData() {
      try {
        const updatedData = {
          id: this.recipe.id,
          name: this.recipe.name,
          price: this.recipe.price,
          duration: this.recipe.duration,
          difficultyLevel: this.recipe.difficultyLevel,
          category: this.recipe.category,
          preparation: this.recipe.preparation,
          ingredients: this.recipe.ingredients,
        };

        const response = await axios.put(
          `http://localhost:8080/api/recipes/${this.recipe.id}`,
          updatedData,
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
              "Content-Type": "application/json",
            },
          }
        );

        const updatedRecipe = response.data;

        if (this.recipeImage) {
          const formData = new FormData();
          formData.append("image", this.recipeImage);
          await axios.post(
            `http://localhost:8080/api/recipes/${updatedRecipe.id}/upload-recipe-image`,
            formData,
            {
              headers: {
                Authorization: `Bearer ${this.token}`,
              },
            }
          );
        }

        alert("Rezept erfolgreich aktualisiert!");
        this.$router.push({ name: "Detail", params: { id: updatedRecipe.id } });
      } catch (error) {
        console.error("Fehler beim Aktualisieren des Rezepts:", error);
        alert("Fehler beim Aktualisieren des Rezepts!");
      }
    },
  },
};
</script>

<style>
.recipe-edit-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

textarea {
  resize: vertical;
}

.submit-button {
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.submit-button:hover {
  background-color: #218838;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

li input {
  margin-right: 10px;
  flex: 1;
}

.add-button {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.add-button:hover {
  background-color: #0056b3;
}

.remove-button {
  padding: 5px 10px;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.remove-button:hover {
  background-color: #c82333;
}
</style>
