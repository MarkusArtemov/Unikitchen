package com.dreamteam.unikitchen.facade;

import com.dreamteam.unikitchen.dto.FavoriteDTO;
import com.dreamteam.unikitchen.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FavoriteFacade {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteFacade(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    public void addFavorite(Long recipeId, String username) {
        favoriteService.addFavorite(recipeId, username);
    }

    public void removeFavorite(Long recipeId, String username) {
        favoriteService.removeFavorite(recipeId, username);
    }

    public List<FavoriteDTO> getFavoritesByUser(String username) {
        return favoriteService.getFavoritesByUser(username);
    }
}
