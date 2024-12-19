<template>
  <div class="start-page">
    <header class="header">
      <div class="logo-container">
        <div class="header-image">
          <img src="../assets/style/kitchen_image.jpeg" alt="Logo" />
        </div>
        <h1>Uniküchen</h1>
      </div>

      <div class="burger-menu" @click="toggleMenu">
        <span></span>
        <span></span>
        <span></span>
      </div>

      <nav :class="{ open: menuOpen }">
        <router-link to="/">Home</router-link>
        <router-link v-if="isLoggedIn" to="/recipe">Rezepte</router-link>
        <router-link v-if="isLoggedIn" to="/account">Account</router-link>
        <router-link v-if="!isLoggedIn" to="/login">Login</router-link>
        <router-link v-if="!isLoggedIn" to="/register"
          >Registrieren</router-link
        >
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
      menuOpen: false,
      isLoggedIn: false,
    };
  },
  provide() {
    return {
      isLoggedIn: () => this.isLoggedIn,
    };
  },
  created() {
    this.checkLoginStatus();
  },
  methods: {
    toggleMenu() {
      this.menuOpen = !this.menuOpen;
    },
    checkLoginStatus() {
      this.isLoggedIn = !!localStorage.getItem("token");
    },
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
