package com.example.tabelog.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.repository.FavoriteRepository;

@Service
public class FavoriteService {
	private final FavoriteRepository favoriteRepository;
	 
    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    // 指定したidを持つお気に入りを取得する
    public Optional<Favorite> findFavoriteById(Integer id) {
        return favoriteRepository.findById(id);
    }

    // 指定した店舗とユーザーが紐づいたお気に入りを取得する
    public Favorite findFavoriteByStoreAndUser(Store store, User user) {
        return favoriteRepository.findByStoreAndUser(store, user);
    }

    // 指定したユーザーのすべてのお気に入りを作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<Favorite> findFavoritesByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
        return favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    // お気に入りのレコード数を取得する
    public long countFavorites() {
        return favoriteRepository.count();
    }

    // 新しいお気に入りを作成し、データベースに保存
    @Transactional
    public void createFavorite(Store store, User user) {
        Favorite favorite = new Favorite();

        // 店舗とユーザーを設定
        favorite.setStore(store);
        favorite.setUser(user);

        // 新しいお気に入りをデータベースに保存
        favoriteRepository.save(favorite);
    }

    // 指定されたお気に入りをデータベースから削除
    @Transactional
    public void deleteFavorite(Favorite favorite) {
        favoriteRepository.delete(favorite);
    }

    // 指定したユーザーが指定した店舗をすでにお気に入りに追加済みかどうかをチェックする
    public boolean isFavorite(Store store, User user) {
        return favoriteRepository.findByStoreAndUser(store, user) != null;
    }
}
