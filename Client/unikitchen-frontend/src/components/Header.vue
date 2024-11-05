<!-- components/Header.vue -->
<template>
  <header class="header">
    <h1>Uniküchen</h1>

    <!-- Burger-Menü-Button -->
    <div class="burger-menu" @click="toggleMenu">
      <span></span>
      <span></span>
      <span></span>
    </div>

    <nav :class="{ open: menuOpen }">
      <router-link to="/">Home</router-link>
      <router-link to="/login">Login</router-link>
      <router-link to="/recipe">Rezepte</router-link>
      <router-link to="/account">Account</router-link>
    </nav>
  </header>
</template>

<script>
export default {
  name: 'HeaderComponent',
  data() {
    return {
      menuOpen: false,
    };
  },
  methods: {
    toggleMenu() {
      this.menuOpen = !this.menuOpen;
    },
  },
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #e4e4e4;
  color: #101010;
  position: relative;
  font-family: Arial, sans-serif;
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
}
</style>
