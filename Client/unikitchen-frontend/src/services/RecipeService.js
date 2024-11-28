import axios from "axios";

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
