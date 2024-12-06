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

    <div class="content">
      <component
        :is="currentComponent"
        :token="token"
        :user="user"
        :categories="categories"
        @bio-updated="handleBioUpdated"
        @recipe-created="handleRecipeCreated"
      ></component>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import AccountInfo from "@/components/AccountInfo.vue";
import AccountFavorites from "@/components/AccountFavorites.vue";
import AccountMyRecipes from "@/components/AccountMyRecipes.vue";
import AccountCreateRecipe from "@/components/AccountCreateRecipe.vue";

export default {
  name: "AccountPage",
  components: {
    AccountInfo,
    AccountFavorites,
    AccountMyRecipes,
    AccountCreateRecipe,
  },
  data() {
    return {
      token: localStorage.getItem("token"),
      categories: ["vegetarisch", "fleisch", "kuchen", "nudeln", "reis"],
      profileImage: null,
      user: {
        username: "",
        bio: "",
      },
      activeSection: "account",
    };
  },
  computed: {
    currentComponent() {
      switch (this.activeSection) {
        case "favorites":
          return "AccountFavorites";
        case "myRecipes":
          return "AccountMyRecipes";
        case "createRecipe":
          return "AccountCreateRecipe";
        default:
          return "AccountInfo";
      }
    },
  },
  async created() {
    const sectionFromQuery = this.$route.query.section;
    if (sectionFromQuery) {
      this.activeSection = sectionFromQuery;
    }

    await this.loadUserData();
    await this.loadProfileImage();
  },
  methods: {
    setActiveSection(section) {
      this.activeSection = section;
      this.$router.push({ query: { section } });
    },
    async loadUserData() {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/users/current-user",
          {
            headers: { Authorization: `Bearer ${this.token}` },
          }
        );
        this.user = response.data;
      } catch (error) {
        console.error("Error loading user data:", error);
      }
    },
    async loadProfileImage() {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/users/current/profile-image",
          {
            headers: { Authorization: `Bearer ${this.token}` },
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
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    async onProfileImageChange(event) {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append("image", file);

      try {
        await axios.post(
          "http://localhost:8080/api/users/current/profile-image",
          formData,
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
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
    handleBioUpdated(updatedBio) {
      this.user.bio = updatedBio;
    },
    handleRecipeCreated() {
      this.setActiveSection("myRecipes");
    },
  },
};
</script>

<style>
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
.form-group textarea,
.form-group select {
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

.recipes-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
}
</style>
