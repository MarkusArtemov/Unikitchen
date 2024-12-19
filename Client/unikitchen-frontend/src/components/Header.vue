<template>
  <div class="start-page">
    <header class="header">
      <div class="logo-container">
        <div class="header-image">
          <!-- Logo image -->
          <img src="../assets/style/kitchen_image.jpeg" alt="Logo" />
        </div>
        <h1>Uniküchen</h1>
      </div>

      <!-- Burger menu button to toggle navigation menu -->
      <div class="burger-menu" @click="toggleMenu">
        <span></span>
        <span></span>
        <span></span>
      </div>

      <nav :class="{ open: menuOpen }">
        <!-- Navigation links based on login status -->
        <router-link to="/">Home</router-link>
        <router-link v-if="isLoggedIn" to="/recipe">Rezepte</router-link>
        <router-link v-if="isLoggedIn" to="/account">Account</router-link>
        <router-link v-if="!isLoggedIn" to="/login">Login</router-link>
        <router-link v-if="!isLoggedIn" to="/register">Registrieren</router-link>
        <!-- Logout button if user is logged in -->
        <button v-if="isLoggedIn" @click="handleLogout" class="logout-button">
          Logout
        </button>
      </nav>
    </header>
  </div>
</template>

<script>
export default {
  name: "HeaderComponent",
  data() {
    return {
      /**
       * Flag to track if the menu is open or closed.
       * @type {boolean}
       */
      menuOpen: false,

      /**
       * Flag to track if the user is logged in.
       * @type {boolean}
       */
      isLoggedIn: false,
    };
  },
  provide() {
    return {
      /**
       * Provides the login status to child components.
       * @returns {boolean} True if user is logged in, false otherwise.
       */
      isLoggedIn: () => this.isLoggedIn,
    };
  },
  created() {
    this.checkLoginStatus();
  },
  methods: {
    /**
     * Toggles the visibility of the mobile menu.
     */
    toggleMenu() {
      this.menuOpen = !this.menuOpen;
    },

    /**
     * Checks if the user is logged in by verifying the presence of a token in local storage.
     */
    checkLoginStatus() {
      this.isLoggedIn = !!localStorage.getItem("token");
    },

    /**
     * Handles user logout by removing the token and user information from local storage.
     * Also redirects the user to the home page after logout.
     */
    handleLogout() {
      const confirmed = confirm("Möchten Sie sich wirklich abmelden?");
      if (!confirmed) {
        return;
      }
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      this.isLoggedIn = false;
      this.$router.push("/");
    },
  },
  watch: {
    /**
     * Watches for route changes and re-checks the login status.
     */
    $route() {
      this.checkLoginStatus();
    },
  },
};
</script>

<style scoped>
.start-page {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #e4e4e4;
  color: #101010;
  position: sticky;
  top: 0;
  width: 100%;
  z-index: 9999;
  font-family: Arial, sans-serif;
  box-sizing: border-box;
}

.logo-container {
  display: flex;
  align-items: center;
}

.header-image img {
  max-width: 50px;
  height: auto;
  border-radius: 8px;
  margin-right: 10px;
}

h1 {
  margin: 0;
  font-size: 1.5em;
  font-weight: 600;
}

.burger-menu {
  display: none;
  flex-direction: column;
  justify-content: space-between;
  height: 20px;
  cursor: pointer;
}

.burger-menu span {
  display: block;
  width: 25px;
  height: 3px;
  background-color: #101010;
  margin: 3px 0;
  border-radius: 2px;
}

nav {
  display: flex;
  align-items: center;
}

nav a {
  color: #101010;
  margin-left: 15px;
  text-decoration: none;
  font-size: 1em;
  font-weight: 500;
}

nav a:hover {
  text-decoration: underline;
}

.logout-button {
  background: none;
  border: none;
  color: #101010;
  cursor: pointer;
  font-size: 1em;
  margin-left: 15px;
}

.logout-button:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .burger-menu {
    display: flex;
  }

  nav {
    position: absolute;
    top: 60px;
    right: 20px;
    flex-direction: column;
    background-color: #e4e4e4;
    padding: 15px;
    border-radius: 5px;
    display: none;
  }

  nav.open {
    display: flex;
  }

  nav a {
    margin: 10px 0;
    font-size: 1.1em;
  }

  .logout-button {
    margin: 10px 0;
  }

  .content {
    flex-direction: column;
  }
}
</style>
