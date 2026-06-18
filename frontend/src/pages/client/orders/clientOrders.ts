import { Rol } from "../../../types/rol";
import { getAuthorizedUser, logOut } from "../../../utils/auth";

const user = getAuthorizedUser(Rol.USUARIO);
const logoutButton = document.getElementById("logout-button") as HTMLButtonElement;

if (user) {
  const userName = document.getElementById("user-name") as HTMLSpanElement;
  userName.textContent = `${user.nombre} ${user.apellido}`;
}

logoutButton.addEventListener("click", logOut);

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
