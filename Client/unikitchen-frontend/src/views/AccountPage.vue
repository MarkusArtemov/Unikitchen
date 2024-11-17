<template>
  <div class="account-page">
    <!-- Profile Section with Image and Upload Button -->
    <div class="profile-section">
      <div class="profile-circle">
        <img v-if="profileImage" :src="profileImage" alt="Profilbild" />
        <div v-else class="default-avatar">🧑‍💻</div>
      </div>
      <button class="upload-button" @click="triggerFileInput">Foto hochladen</button>
      <input type="file" ref="fileInput" class="hidden-file-input" @change="onProfileImageChange" />
      <p>{{ user.username }}</p>
    </div>

    <!-- Navigation Menu -->
    <div class="menu">
      <button @click="setActiveSection('favorites')">Favoriten</button>
      <button @click="setActiveSection('myRecipes')">Meine Rezepte</button>
      <button @click="setActiveSection('createRecipe')">Neues Rezept</button>
      <button @click="setActiveSection('account')">Account</button>
    </div>

    <!-- Content Section -->
    <div class="content">
      <!-- Create Recipe Section -->
      <div v-if="activeSection === 'createRecipe'">
        <h3>Neues Rezept erstellen</h3>
        <form @submit.prevent="submitRecipe">
          <div class="form-group">
            <label for="name">Rezeptname:</label>
            <input type="text" id="name" v-model="recipe.name" required />
          </div>

          <div class="form-group">
            <label for="price">Preis (in €):</label>
            <input type="number" id="price" v-model="recipe.price" required step="0.01" />
          </div>

          <div class="form-group">
            <label for="instructions">Zubereitung:</label>
            <textarea id="instructions" v-model="recipe.instructions" required></textarea>
          </div>

          <div class="form-group">
            <label>Zutaten:</label>
            <ul>
              <li v-for="(ingredient, index) in recipe.ingredients" :key="index">
                <input v-model="ingredient.name" placeholder="Zutat" required />
                <input v-model="ingredient.amount" placeholder="Menge" required />
                <button type="button" @click="removeIngredient(index)">Entfernen</button>
              </li>
            </ul>
            <button type="button" @click="addIngredient">Zutat hinzufügen</button>
          </div>

          <button type="submit">Rezept speichern</button>
        </form>
      </div>

      <!-- My Recipes Section -->
      <div v-if="activeSection === 'myRecipes'">
        <h3>Meine Rezepte</h3>
        <ul>
          <li v-for="(recipe, index) in myRecipes" :key="index">{{ recipe.name }}</li>
        </ul>
      </div>

      <!-- Account Section -->
      <div v-else-if="activeSection === 'account'">
        <h3>Kontoinformationen</h3>
        <form @submit.prevent="updateAccount">
          <div class="form-group">
            <label for="firstName">Vorname:</label>
            <input
                type="text"
                id="firstName"
                v-model="user.firstName"
                required
            />
          </div>

          <div class="form-group">
            <label for="lastName">Nachname:</label>
            <input
                type="text"
                id="lastName"
                v-model="user.lastName"
                required
            />
          </div>

          <div class="form-group">
            <label for="bio">Bio:</label>
            <textarea
                id="bio"
                v-model="user.bio"
            ></textarea>
          </div>

          <button type="submit">Änderungen speichern</button>
        </form>
      </div>

      <!-- Favorites Section -->
      <div v-if="activeSection === 'favorites'">
        <h3>Favoriten</h3>
        <p>Keine Favoriten vorhanden.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      profileImage: null,
      user: {
        username: '',
        bio: '',
      },
      activeSection: 'account',
      favoriteRecipes: [],
      myRecipes: [],
      recipe: {
        name: '',
        price: null,
        instructions: '',
        ingredients: [{ name: '', amount: '' }],
      },
    };
  },
  created() {
    this.loadUserData();
    this.loadProfileImage();
  },
  methods: {
    setActiveSection(section) {
      this.activeSection = section;
    },
    async loadUserData() {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('/api/users/current-user', {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.user = response.data;
      } catch (error) {
        console.error('Error loading user data:', error);
      }
    },
    async loadProfileImage() {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('/api/users/current/profile-image', {
          headers: { Authorization: `Bearer ${token}` },
          responseType: 'blob',
        });

        if (this.profileImage) {
          URL.revokeObjectURL(this.profileImage);
        }
        this.profileImage = URL.createObjectURL(response.data);
      } catch (error) {
        console.error('Error loading profile image:', error);
      }
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    async onProfileImageChange(event) {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append('image', file);

      try {
        const token = localStorage.getItem('token');
        await axios.post('/api/users/current/profile-image', formData, {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'multipart/form-data',
          },
        });
        event.target.value = null;
        this.loadProfileImage();
      } catch (error) {
        console.error('Error uploading profile image:', error);
      }
    },
    addIngredient() {
      this.recipe.ingredients.push({ name: '', amount: '' });
    },
    removeIngredient(index) {
      this.recipe.ingredients.splice(index, 1);
    },
    async submitRecipe() {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.post('/api/recipes', this.recipe, {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });
        console.log('Recipe saved successfully:', response.data);
        this.recipe = {
          name: '',
          price: null,
          instructions: '',
          ingredients: [{ name: '', amount: '' }],
        };
        alert('Rezept gespeichert!');
      } catch (error) {
        console.error('Error submitting recipe:', error);
        alert('Fehler beim Speichern des Rezepts!');
      }
    },
  },
};
</script>

<style scoped>
.account-page {
  padding: 20px;
  text-align: center;
}

.profile-section {
  margin-bottom: 20px;
}

.profile-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
  overflow: hidden;
}

.profile-circle img {
  width: 100%;
  height: auto;
}

.default-avatar {
  font-size: 3em;
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
  margin-bottom: 20px;
}

.menu button {
  margin: 0 10px;
  padding: 10px 20px;
  border: none;
  background-color: #007bff;
  color: white;
  border-radius: 5px;
  cursor: pointer;
}

.menu button:hover {
  background-color: #0056b3;
}

.content {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 8px;
  margin-top: 5px;
}

button {
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #218838;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: flex;
  margin-bottom: 10px;
}

li input {
  margin-right: 10px;
}
</style>
