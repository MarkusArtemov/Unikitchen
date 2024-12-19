import axios from "axios";

// This service module handles recipe-related API calls and image fetching.

// Fetch recipe image and assign base64 to recipe.imageSrc
export async function fetchRecipeImage(recipe) {
  try {
    const response = await axios.get(`/api/recipes/${recipe.id}/recipe-image`, {
      responseType: "arraybuffer",
    });

    const base64Image = btoa(
      new Uint8Array(response.data).reduce(
        (data, byte) => data + String.fromCharCode(byte),
        ""
      )
    );
    recipe.imageSrc = `data:image/jpeg;base64,${base64Image}`;
  } catch (error) {
    console.error("Fehler beim Abrufen des Rezeptbildes:", error);
  }
}

// Create a new recipe (RecipeCreationRequest DTO)
export async function createRecipe(token, creationRequest) {
  const response = await axios.post("/api/recipes", creationRequest, {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });
  return response.data;
}

// Update a recipe (RecipeUpdateRequest DTO)
export async function updateRecipe(token, recipeId, updateRequest) {
  const response = await axios.put(`/api/recipes/${recipeId}`, updateRequest, {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });
  return response.data;
}

// Upload recipe image
export async function uploadRecipeImage(token, recipeId, file) {
  const formData = new FormData();
  formData.append("image", file);
  await axios.post(`/api/recipes/${recipeId}/upload-recipe-image`, formData, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
}

// Get recipe details (RecipeDetailsResponse)
export async function fetchRecipeDetails(token, recipeId) {
  const response = await axios.get(`/api/recipes/${recipeId}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;
}

// Delete recipe
export async function deleteRecipe(token, recipeId) {
  await axios.delete(`/api/recipes/${recipeId}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
}

// Fetch last recipes (List<RecipeOverviewResponse>)
export async function fetchLastRecipes(token) {
  const headers = {};
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  const response = await axios.get("/api/recipes/lastRecipes", { headers });
  return response.data;
}

// Fetch filtered recipes (paged RecipeOverviewResponse)
export async function fetchFilteredRecipes(token, params) {
  const headers = {};
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  const response = await axios.get("/api/recipes/filtered", {
    headers,
    params,
  });
  return response.data;
}

// Fetch user's own recipes (List<RecipeOverviewResponse>)
export async function fetchUserRecipes(token) {
  const response = await axios.get("/api/recipes/user", {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;
}

// Toggle favorite status
export async function toggleFavorite(token, recipeId) {
  const response = await axios.put(`/api/favorites/toggle/${recipeId}`, null, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
}

// Fetch favorites (List<RecipeOverviewResponse>)
export async function fetchFavorites(token) {
  const response = await axios.get("/api/favorites/current", {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;
}

// Fetch user rating (returns { ratingValue: number })
export async function fetchUserRating(token, recipeId) {
  try {
    const response = await axios.get(`/api/ratings/recipe/${recipeId}/user`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  } catch (error) {
    // If a 500 error occurs, we can assume no rating exists yet.
    if (error.response && error.response.status === 500) {
      console.warn("Kein Rating gefunden, setze Standardwert.");
      return null;
    }
    throw error;
  }
}

// Submit rating
export async function submitRating(token, recipeId, ratingValue) {
  await axios.post(`/api/ratings/recipe/${recipeId}`, null, {
    params: { ratingValue: ratingValue },
    headers: { Authorization: `Bearer ${token}` },
  });
}
