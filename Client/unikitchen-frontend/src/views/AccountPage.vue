<template>
  <div class="account-page">
    <div class="profile-section">
      <div class="profile-circle">
        <img v-if="profileImage" :src="profileImage" alt="Profilbild" />
        <div v-else class="default-avatar">🧑‍💻</div>
      </div>
      <button class="upload-button" @click="triggerFileInput">
        Foto hochladen
      </button>
      <input
        type="file"
        ref="fileInput"
        class="hidden-file-input"
        @change="onProfileImageChange"
      />
      <p class="username">{{ user.username }}</p>
    </div>

    <!-- Navigation Menu -->
    <div class="menu">
      <button
        @click="setActiveSection('favorites')"
        :class="{ active: activeSection === 'favorites' }"
      >
        Favoriten
      </button>
      <button
        @click="setActiveSection('myRecipes')"
        :class="{ active: activeSection === 'myRecipes' }"
      >
        Meine Rezepte
      </button>
      <button
        @click="setActiveSection('createRecipe')"
        :class="{ active: activeSection === 'createRecipe' }"
      >
        Neues Rezept
      </button>
      <button
        @click="setActiveSection('account')"
        :class="{ active: activeSection === 'account' }"
      >
        Account
      </button>
    </div>

    <!-- Content Section -->
    <div class="content">
      <!-- Create Recipe Section -->
      <div v-if="activeSection === 'createRecipe'" class="section">
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
            <label for="instructions">Zubereitung:</label>
            <textarea
              id="instructions"
              v-model="recipe.instructions"
              required
            ></textarea>
          </div>

          <div class="form-group">
            <label>Zutaten:</label>
            <ul>
              <li
                v-for="(ingredient, index) in recipe.ingredients"
                :key="index"
              >
                <input v-model="ingredient.name" placeholder="Zutat" required />
                <input
                  v-model="ingredient.amount"
                  placeholder="Menge"
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

          <!-- Kategorie Dropdown -->
          <div class="form-group">
            <p>Kategorie: {{ recipe.category }}</p>
            <select id="category" v-model="recipe.category" required>
              <option v-for="cat in categories" :key="cat" :value="cat">
                {{ cat }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="difficultyLevel">Schwierigkeitsgrad:</label>
            <select
              id="difficultyLevel"
              v-model="recipe.difficultyLevel"
              required
            >
              <option value="EASY">Einfach</option>
              <option value="MEDIUM">Mittel</option>
              <option value="HARD">Schwer</option>
            </select>
          </div>

          <form @submit.prevent="submitRecipe">
            <button type="submit" class="submit-button">
              Rezept speichern
            </button>
          </form>
        </form>
      </div>

      <!-- Favorites Section -->
      <div v-if="activeSection === 'favorites'" class="section">
        <h3>Favoriten</h3>
        <div class="favorites-grid">
          <MenuCard
            v-for="favorite in favoriteRecipes"
            :key="favorite.recipeId"
            :recipe="{
              id: favorite.recipeId,
              name: favorite.recipeName,
              price: favorite.price,
              duration: favorite.duration,
              difficultyLevel: favorite.difficultyLevel,
              category: favorite.category,
              imageSrc:
                favorite.imageSrc || getFullImagePath(favorite.recipeImagePath),
            }"
            :to="{ name: 'Detail', params: { id: favorite.recipeId } }"
          />
        </div>
      </div>

      <!-- My Recipes Section -->
      <div v-if="activeSection === 'myRecipes'" class="section">
        <h3>Meine Rezepte</h3>
        <div v-if="myRecipes.length === 0">
          <p>Du hast noch keine Rezepte erstellt.</p>
        </div>
        <div v-else class="my-recipes-grid">
          <MenuCard
            v-for="recipe in myRecipes"
            :key="recipe.id"
            :recipe="{
              id: recipe.id,
              name: recipe.name,
              price: recipe.price,
              duration: recipe.duration,
              difficultyLevel: recipe.difficultyLevel,
              category: recipe.category,
              imageSrc:
                recipe.imageSrc || getFullImagePath(recipe.recipeImagePath),
            }"
            :to="{ name: 'Detail', params: { id: recipe.id } }"
          />
        </div>
      </div>

      <!-- Account Section -->
      <div v-else-if="activeSection === 'account'" class="section">
        <h3>Kontoinformationen</h3>
        <p>Benutzername: {{ user.username }}</p>

        <!-- Bio Section with Edit Option -->
        <div class="bio-section">
          <label for="bio">Bio:</label>
          <textarea id="bio" v-model="user.bio"></textarea>
          <button @click="updateBio">Bio speichern</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import MenuCard from "@/components/MenuCard.vue";

export default {
  components: {
    MenuCard,
  },
  data() {
    return {
      categories: [
        "Vegetarisch",
        "Vegan",
        "Fleisch",
        "Kuchen",
        "Nudeln",
        "Reis",
      ],
      profileImage: null,
      user: {
        username: "",
        bio: "",
      },
      activeSection: "account",
      favoriteRecipes: [],
      myRecipes: [],
      recipe: {
        name: "",
        price: null,
        instructions: "",
        ingredients: [{ name: "", amount: "" }],
        category: "",
        difficultyLevel: "",
        preparation: "",
      },
    };
  },
  created() {
    this.loadUserData();
    this.loadProfileImage();
    this.loadFavoriteRecipes();
    this.loadMyRecipes();
  },
  methods: {
    setActiveSection(section) {
      this.activeSection = section;
    },
    async loadUserData() {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(
          "http://localhost:8080/api/users/current-user",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        this.user = response.data;
      } catch (error) {
        console.error("Error loading user data:", error);
      }
    },
    async loadProfileImage() {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(
          "http://localhost:8080/api/users/current/profile-image",
          {
            headers: { Authorization: `Bearer ${token}` },
            responseType: "blob",
          }
        );

        if (this.profileImage) {
          URL.revokeObjectURL(this.profileImage);
        }
        this.profileImage = URL.createObjectURL(response.data);
      } catch (error) {
        console.error("Error loading profile image:", error);
      }
    },
    async loadMyRecipes() {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(
          "http://localhost:8080/api/recipes/user",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        this.myRecipes = response.data;
      } catch (error) {
        console.error("Fehler beim Laden der eigenen Rezepte:", error);
      }
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    async onProfileImageChange(event) {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append("image", file);

      try {
        const token = localStorage.getItem("token");
        await axios.post(
          "http://localhost:8080/api/users/current/profile-image",
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "multipart/form-data",
            },
          }
        );
        event.target.value = null;
        this.loadProfileImage();
      } catch (error) {
        console.error("Error uploading profile image:", error);
      }
    },
    addIngredient() {
      this.recipe.ingredients.push({ name: "", amount: "" });
    },
    removeIngredient(index) {
      this.recipe.ingredients.splice(index, 1);
    },
    methods: {
      async submitRecipe() {
        try {
          const token = localStorage.getItem("token");
          const data = this.recipe;

          const response = await axios.post(
            "http://localhost:8080/api/recipes",
            /*this.recipe*/ data,
            {
              headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
              },
            }
          );

          console.log("Recipe saved successfully:", response.data);
          this.recipe = {
            name: "",
            price: null,
            instructions: "",
            ingredients: [{ name: "", amount: "" }],
            category: "",
            difficultyLevel: "EASY",
            preparation: "",
          };
          this.recipe = {
            name: "",
            price: null,
            ingredients: [{ name: "", amount: "" }],
            category: "",
            difficultyLevel: "",
          };
          alert("Rezept erfolgreich gespeichert!");
        } catch (error) {
          console.error("Error submitting recipe:", error);
          alert("Fehler beim Speichern des Rezepts!");
        }
      },
    },

    async loadFavoriteRecipes() {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(
          "http://localhost:8080/api/favorites/current",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        this.favoriteRecipes = response.data;

        for (const favorite of this.favoriteRecipes) {
          const imagePath = await this.fetchRecipeImage(favorite.recipeId);
          favorite.imageSrc =
            imagePath || this.getFullImagePath(favorite.recipeImagePath);
        }
      } catch (error) {
        console.error("Fehler beim Laden der Favoriten:", error);
      }
    },
    async fetchRecipeImage(recipeId) {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/recipes/${recipeId}/recipe-image`,
          {
            responseType: "arraybuffer",
          }
        );
        return `data:image/jpeg;base64,${btoa(
          new Uint8Array(response.data).reduce(
            (data, byte) => data + String.fromCharCode(byte),
            ""
          )
        )}`;
      } catch (error) {
        console.error(
          `Fehler beim Laden des Bildes für Rezept ${recipeId}:`,
          error
        );
        return null;
      }
    },
    getFullImagePath(imagePath) {
      if (!imagePath) {
        return "path/to/default-image.jpg";
      }
      if (imagePath.startsWith("http") || imagePath.startsWith("data:image")) {
        return imagePath;
      }
      return `http://localhost:8080/${imagePath}`;
    },
    async updateBio() {
      try {
        const token = localStorage.getItem("token");
        await axios.put(
          "http://localhost:8080/api/users/current-user",
          { bio: this.user.bio },
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        console.log("Bio updated successfully");
      } catch (error) {
        console.error("Error updating bio:", error);
      }
    },
  },
};
</script>

<style scoped>
.account-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: #f4f4f9;
  min-height: 100vh;
}

.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.profile-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background-color: #e0e0e0;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  margin-bottom: 10px;
}

.profile-circle img {
  width: 100%;
  height: auto;
}

.default-avatar {
  font-size: 3em;
}

.username {
  font-weight: bold;
  font-size: 1.2em;
  color: #333;
}

.hidden-file-input {
  display: none;
}

.upload-button {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.upload-button:hover {
  background-color: #0056b3;
}

.menu {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.menu button {
  padding: 10px 20px;
  margin: 0 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.menu button.active {
  background-color: #0056b3;
}

.menu button:hover {
  background-color: #0056b3;
}

.content {
  width: 100%;
  max-width: 800px;
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.form-group textarea {
  resize: vertical;
}

button.submit-button {
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button.submit-button:hover {
  background-color: #218838;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

li input {
  width: 45%;
  margin-right: 10px;
}

.add-button {
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

.bio-section {
  margin-top: 10px;
}

.favorites-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
}
</style>
