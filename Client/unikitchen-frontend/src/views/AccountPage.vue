<template>
  <!-- Main container for the Account page -->
  <div class="account-page">
    <!-- Profile section: Displays user avatar, upload button, and username -->
    <div class="profile-section">
      <div class="profile-circle">
        <!-- If a profile image exists, display it; otherwise, show a default avatar -->
        <img v-if="profileImage" :src="profileImage" alt="Profile Picture" />
        <div v-else class="default-avatar">◕‿◕</div>
      </div>
      <!-- Button to trigger profile image upload -->
      <button class="upload-button" @click="triggerFileInput">
        Foto hochladen
      </button>
      <!-- Hidden file input triggered by the upload button -->
      <input
          type="file"
          ref="fileInput"
          class="hidden-file-input"
          @change="onProfileImageChange"
      />
      <!-- Display the username of the logged-in user -->
      <p class="username">{{ user.username }}</p>
    </div>

    <!-- Navigation menu for different sections -->
    <div class="menu">
      <!-- Button for the 'Favorites' section -->
      <button
          @click="setActiveSection('favorites')"
          :class="{ active: activeSection === 'favorites' }"
      >
        Favoriten
      </button>
      <!-- Button for the 'My Recipes' section -->
      <button
          @click="setActiveSection('myRecipes')"
          :class="{ active: activeSection === 'myRecipes' }"
      >
        Meine Rezepte
      </button>
      <!-- Button for creating a new recipe -->
      <button
          @click="setActiveSection('createRecipe')"
          :class="{ active: activeSection === 'createRecipe' }"
      >
        Rezept erstellen
      </button>
      <!-- Button for the 'Account Info' section -->
      <button
          @click="setActiveSection('account')"
          :class="{ active: activeSection === 'account' }"
      >
        Account
      </button>
    </div>

    <!-- Dynamic content section based on the active section -->
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
  name: "AccountPage", // Component name
  components: {
    AccountInfo, // Component for account information
    AccountFavorites, // Component for user's favorite recipes
    AccountMyRecipes, // Component for user's own recipes
    AccountCreateRecipe, // Component for creating a new recipe
  },
  data() {
    return {
      token: localStorage.getItem("token"), // Token for authenticated API requests
      categories: ["vegetarian", "meat", "cake", "pasta", "rice"], // Recipe categories
      profileImage: null, // URL for the user's profile image
      user: {
        username: "", // Logged-in user's username
        bio: "", // User's biography
      },
      activeSection: "account", // Currently active section of the account page
    };
  },
  computed: {
    /**
     * Dynamically determines the component to display based on the active section.
     * @returns {string} The name of the component to render.
     */
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
    // Load user data and profile image when the component is created
    const sectionFromQuery = this.$route.query.section;
    if (sectionFromQuery) {
      this.activeSection = sectionFromQuery;
    }

    await this.loadUserData();
    await this.loadProfileImage();
  },
  methods: {
    /**
     * Sets the active section of the page and updates the URL query parameter.
     * @param {string} section - The section to activate.
     */
    setActiveSection(section) {
      this.activeSection = section;
      this.$router.push({ query: { section } });
    },
    /**
     * Fetches the current user's data from the server.
     */
    async loadUserData() {
      try {
        const response = await axios.get("/api/users/current-user", {
          headers: { Authorization: `Bearer ${this.token}` },
        });
        this.user = response.data;
      } catch (error) {
        console.error("Error loading user data:", error);
      }
    },
    /**
     * Fetches the profile image of the current user from the server.
     */
    async loadProfileImage() {
      try {
        const response = await axios.get("/api/users/current/profile-image", {
          headers: { Authorization: `Bearer ${this.token}` },
          responseType: "blob",
        });

        if (this.profileImage) {
          URL.revokeObjectURL(this.profileImage);
        }
        this.profileImage = URL.createObjectURL(response.data);
      } catch (error) {
        console.error("Error loading profile image:", error);
      }
    },
    /**
     * Triggers the hidden file input for uploading a profile image.
     */
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    /**
     * Handles profile image changes and uploads the new image to the server.
     * @param {Event} event - The change event from the file input.
     */
    async onProfileImageChange(event) {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append("image", file);

      try {
        await axios.post("/api/users/current/profile-image", formData, {
          headers: {
            Authorization: `Bearer ${this.token}`,
            "Content-Type": "multipart/form-data",
          },
        });
        event.target.value = null;
        this.loadProfileImage();
      } catch (error) {
        console.error("Error uploading profile image:", error);
      }
    },
    /**
     * Updates the user's biography when the bio is modified.
     * @param {string} updatedBio - The updated biography.
     */
    handleBioUpdated(updatedBio) {
      this.user.bio = updatedBio;
    },
    /**
     * Handles the creation of a new recipe and switches to the 'My Recipes' section.
     */
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
</style>
