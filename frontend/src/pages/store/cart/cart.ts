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
