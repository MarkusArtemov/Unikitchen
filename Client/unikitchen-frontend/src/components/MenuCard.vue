<template>
  <router-link :to="to" class="menu-card no-link">
    <!-- Bildbereich -->
    <div class="image-container">
      <div v-if="!recipe.imageSrc" class="placeholder-container"></div>
      <img
        v-else
        :src="recipe.imageSrc"
        alt="Rezeptbild"
        class="recipe-image"
      />
    </div>

    <!-- Info-Bereich -->
    <div class="info-container">
      <h3 class="recipe-title">{{ recipe.name }}</h3>
      <div class="recipe-attributes">
        <span>{{ recipe.category }}</span>
        <span>{{ recipe.duration }} Min</span>
        <span>{{ recipe.price }} €</span>
        <span>{{ recipe.difficultyLevel }}</span>
      </div>
    </div>
  </router-link>
</template>

<script>
export default {
  name: "MenuCard",
  props: {
    recipe: {
      type: Object,
      required: true,
    },
    to: {
      type: [String, Object],
      required: false,
    },
  },
};
</script>

<style scoped>
.menu-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #ccc;
  border-radius: 10px;
  overflow: hidden;
  text-align: center;
  transition: transform 0.3s;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  min-width: 200px;
}

.menu-card:hover {
  transform: scale(1.05);
}

.image-container {
  width: 100%;
  height: 150px;
  overflow: hidden;
  background-color: #f7f7f7;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.recipe-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-container {
  width: 100%;
  height: 100%;
  background-color: #e9ecef;
  min-width: 100px;
  min-height: 100px;
}

.info-container {
  padding: 10px;
  width: 100%;
}

.recipe-title {
  font-size: 1.1rem;
  font-weight: bold;
  margin: 0;
  margin-bottom: 10px;
  color: #333;
}

.recipe-attributes {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  font-size: 0.85rem;
  color: #555;
  justify-content: center;
}

.recipe-attributes span {
  background-color: #e9ecef;
  padding: 5px 10px;
  border-radius: 12px;
  white-space: nowrap;
}

.no-link {
  color: inherit;
  text-decoration: none;
}
</style>
