// レビューの削除用フォーム
const deleteReviewForm = document.forms.deleteReviewForm;

// レビューの削除用モーダルを開くときの処理
document.getElementById('deleteReviewModal').addEventListener('show.bs.modal', (event) => {
    let deleteButton = event.relatedTarget;
    let storeId = deleteButton.dataset.storeId;
    let reviewId = deleteButton.dataset.reviewId;

    deleteReviewForm.action = `/stores/${storeId}/review/${reviewId}/delete`;
});