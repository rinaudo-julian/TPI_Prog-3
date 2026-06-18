const orderCard = document.getElementById("order-card") as HTMLElement;
const orderDetailDialog = document.getElementById(
  "order-detail-dialog"
) as HTMLDialogElement;
const closeOrderDetailButton = document.getElementById(
  "close-order-detail"
) as HTMLButtonElement;

const openDialog = () => {
  orderDetailDialog.showModal();
};

orderCard.addEventListener("click", openDialog);

orderCard.addEventListener("keydown", (event: KeyboardEvent) => {
  if (event.key === "Enter" || event.key === " ") {
    event.preventDefault();
    openDialog();
  }
});

closeOrderDetailButton.addEventListener("click", () => {
  orderDetailDialog.close();
});

orderDetailDialog.addEventListener("click", (event: PointerEvent) => {
  if (event.target === orderDetailDialog) {
    orderDetailDialog.close();
  }
});
