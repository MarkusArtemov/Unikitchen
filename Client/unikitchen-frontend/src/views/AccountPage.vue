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
      <!-- Favorites Section -->
      <div v-if="activeSection === 'favorites'">
        <h3>Favoriten</h3>
        <ul>
          <li v-for="recipe in favoriteRecipes" :key="recipe.id">{{ recipe.name }}</li>
        </ul>
      </div>

      <!-- My Recipes Section -->
      <div v-else-if="activeSection === 'myRecipes'">
        <h3>Meine Rezepte</h3>
        <ul>
          <li v-for="recipe in myRecipes" :key="recipe.id">{{ recipe.name }}</li>
        </ul>
      </div>

      <!-- Account Section -->
      <div v-else-if="activeSection === 'account'">
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
        const response = await axios.get('http://localhost:8080/api/users/current-user', {
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
        const response = await axios.get('http://localhost:8080/api/users/current/profile-image', {
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
        await axios.post('http://localhost:8080/api/users/current/profile-image', formData, {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'multipart/form-data',
          },
        });
        event.target.value = null; // Clear the input after upload
        this.loadProfileImage(); // Reload profile image after successful upload
      } catch (error) {
        console.error('Error uploading profile image:', error);
      }
    },
    async updateBio() {
      try {
        const token = localStorage.getItem('token');
        await axios.put('http://localhost:8080/api/users/current-user',
            { bio: this.user.bio },
            {
              headers: { Authorization: `Bearer ${token}` },
            });
        console.log('Bio updated successfully');
      } catch (error) {
        console.error('Error updating bio:', error);
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

.bio-section {
  margin-top: 10px;
}

</style>
