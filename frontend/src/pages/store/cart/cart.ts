import { Rol } from "../../../types/rol";
import { getAuthorizedUser, logOut } from "../../../utils/auth";

const user = getAuthorizedUser(Rol.USUARIO);
const logoutButton = document.getElementById("logout-button") as HTMLButtonElement;

if (user) {
  const userName = document.getElementById("user-name") as HTMLSpanElement;
  userName.textContent = `${user.nombre} ${user.apellido}`;
}

logoutButton.addEventListener("click", logOut);

const checkoutDialog = document.getElementById(
  "checkout-dialog"
) as HTMLDialogElement;
const openCheckoutButton = document.getElementById(
  "open-checkout"
) as HTMLButtonElement;
const closeCheckoutButton = document.getElementById(
  "close-checkout"
) as HTMLButtonElement;
const checkoutForm = document.getElementById(
  "checkout-form"
) as HTMLFormElement;

openCheckoutButton.addEventListener("click", () => {
  checkoutDialog.showModal();
});

closeCheckoutButton.addEventListener("click", () => {
  checkoutDialog.close();
});

checkoutDialog.addEventListener("click", (event: PointerEvent) => {
  if (event.target === checkoutDialog) {
    checkoutDialog.close();
  }
});

checkoutForm.addEventListener("submit", (event: SubmitEvent) => {
  event.preventDefault();
  checkoutDialog.close();
});
