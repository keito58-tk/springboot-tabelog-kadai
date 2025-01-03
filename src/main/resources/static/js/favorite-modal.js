// お気に入りの解除用フォーム
const removeFavoriteForm = document.forms.removeFavoriteForm;

// 解除の確認メッセージ
const removeMessage = document.getElementById('removeFavoriteModalLabel');

// お気に入りの解除用モーダルを開くときの処理
document.getElementById('removeFavoriteModal').addEventListener('show.bs.modal', (event) => {
    let removeButton = event.relatedTarget;
    let favoriteId = removeButton.dataset.favoriteId;
    let storeName = removeButton.dataset.storeName;

    removeFavoriteForm.action = `/favorites/${favoriteId}/delete`;
    removeMessage.textContent = `「${storeName}」のお気に入りを解除してもよろしいですか？`
});