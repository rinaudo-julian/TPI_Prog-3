import { Rol } from "../../../types/rol";
import type { CartItem } from "../../../types/cart";
import { FormasPago } from "../../../types/formasPago";
import type { ErrorResponse } from "../../../types/error";
import { getAuthorizedUser, logOut } from "../../../utils/auth";
import {
  clearCart,
  decrementCartItem,
  getCart,
  getCartCount,
  getCartSubtotal,
  getCartTotal,
  incrementCartItem,
  removeCartItem,
} from "../../../utils/cart";

const user = getAuthorizedUser(Rol.USUARIO);
const logoutButton = document.getElementById("logout-button") as HTMLButtonElement;
const cartCount = document.getElementById("cart-count") as HTMLSpanElement | null;
const cartItems = document.getElementById("cart-items") as HTMLElement | null;
const cartSubtotal = document.getElementById("cart-subtotal") as HTMLSpanElement | null;
const cartTotal = document.getElementById("cart-total") as HTMLSpanElement | null;
const checkoutTotal = document.getElementById("checkout-total") as HTMLSpanElement | null;
const clearCartButton = document.getElementById("clear-cart-button") as HTMLButtonElement | null;
const openCheckoutButton = document.getElementById("open-checkout") as HTMLButtonElement;
const checkoutDialog = document.getElementById("checkout-dialog") as HTMLDialogElement;
const closeCheckoutButton = document.getElementById("close-checkout") as HTMLButtonElement;
const checkoutForm = document.getElementById("checkout-form") as HTMLFormElement;
const checkoutPaymentSelect = document.getElementById(
  "checkout-payment"
) as HTMLSelectElement | null;
const checkoutStatus = document.getElementById(
  "checkout-status"
) as HTMLParagraphElement | null;
const checkoutPhoneInput = document.getElementById(
  "checkout-phone"
) as HTMLInputElement | null;
const checkoutAddressInput = document.getElementById(
  "checkout-address"
) as HTMLTextAreaElement | null;
const confirmPaymentButton = document.getElementById(
  "confirm-payment-button"
) as HTMLButtonElement | null;
const CHECKOUT_SUCCESS_CLOSE_DELAY = 2000;

const formatCurrency = (value: number) => `$${value.toFixed(2)}`;
const API_BASE_URL = "http://localhost:8080";

const renderCartItem = (item: CartItem) => `
  <article
    class="flex flex-wrap items-center gap-4 rounded-lg bg-white px-4 py-3 shadow-[0_4px_14px_rgba(0,0,0,0.08)]"
  >
    <img
      src="/src/assets/products/${item.imagen}"
      alt="${item.nombre}"
      class="h-12 w-12 rounded-md object-cover"
    />

    <div class="min-w-0 flex-1">
      <p class="text-[13px] font-semibold text-text-strong">
        ${item.nombre}
      </p>
      <p class="mt-0.5 text-[10px] text-[#a3a3a3]">
        ${item.descripcion}
      </p>
      <p class="mt-1 text-[14px] font-bold text-primary-soft">
        ${formatCurrency(item.precio)} c/u
      </p>
      <p class="mt-1 text-[11px] text-[#7a7a7a]">
        Disponible: ${item.stock}
      </p>
    </div>

    <div class="flex items-center gap-2 text-[14px] font-semibold text-primary-soft">
      <button
        type="button"
        data-action="decrement"
        data-item-id="${item.id}"
        class="cursor-pointer flex h-7 w-7 items-center justify-center rounded-sm bg-primary text-[16px] text-white transition hover:bg-primary-hover"
        aria-label="Disminuir cantidad de ${item.nombre}"
      >
        -
      </button>
      <span class="w-6 text-center text-text-strong">${item.quantity}</span>
      <button
        type="button"
        data-action="increment"
        data-item-id="${item.id}"
        ${item.quantity >= item.stock ? 'disabled aria-disabled="true"' : ""}
        class="cursor-pointer flex h-7 w-7 items-center justify-center rounded-sm bg-primary text-[16px] text-white transition hover:bg-primary-hover disabled:cursor-not-allowed disabled:opacity-50"
        aria-label="Aumentar cantidad de ${item.nombre}"
      >
        +
      </button>
    </div>

    <div class="min-w-23.5 text-right text-[15px] font-bold text-primary-soft">
      ${formatCurrency(item.precio * item.quantity)}
    </div>

    <button
      type="button"
      data-action="remove"
      data-item-id="${item.id}"
      class="cursor-pointer flex h-8 items-center justify-center rounded-md bg-danger px-4 text-[14px] text-white transition hover:bg-danger-hover"
    >
      Eliminar
    </button>
  </article>
`;

const renderEmptyCart = () => `
  <div class="rounded-lg border border-dashed border-border bg-white px-6 py-10 text-center text-[#7b7b7b] shadow-[0_4px_14px_rgba(0,0,0,0.05)]">
    <p class="text-[16px] font-semibold text-text-strong">Tu carrito está vacío</p>
    <p class="mt-2 text-[13px]">Agregá productos desde el detalle para verlos acá.</p>
  </div>
`;

const renderPaymentOptions = () => {
  if (!checkoutPaymentSelect) {
    return;
  }

  const paymentOptions = Object.values(FormasPago)
    .map(
      (paymentMethod) => `
        <option value="${paymentMethod}">${paymentMethod}</option>
      `
    )
    .join("");

  checkoutPaymentSelect.innerHTML = `
    <option value="" selected disabled>Seleccione una opción</option>
    ${paymentOptions}
  `;
};

const updateCheckoutButtonState = () => {
  if (
    !confirmPaymentButton ||
    !checkoutPhoneInput ||
    !checkoutAddressInput ||
    !checkoutPaymentSelect
  ) {
    return;
  }

  const isFormValid =
    checkoutPhoneInput.validity.valid &&
    checkoutAddressInput.validity.valid &&
    checkoutPaymentSelect.validity.valid;

  confirmPaymentButton.disabled = !isFormValid;
};

const setCheckoutStatus = (message: string, isError = false) => {
  if (!checkoutStatus) {
    return;
  }

  checkoutStatus.textContent = message;
  checkoutStatus.className = isError
    ? "text-[13px] text-danger"
    : "text-[13px] text-success";
};

const openCheckoutModal = () => {
  setCheckoutStatus("");
  checkoutDialog.showModal();
};

const syncSummary = () => {
  if (cartCount) {
    cartCount.textContent = String(getCartCount());
  }

  if (cartSubtotal) {
    cartSubtotal.textContent = formatCurrency(getCartSubtotal());
  }

  if (cartTotal) {
    cartTotal.textContent = formatCurrency(getCartTotal());
  }

  if (checkoutTotal) {
    checkoutTotal.textContent = formatCurrency(getCartTotal());
  }
};

const renderCart = () => {
  const cart = getCart();

  syncSummary();

  if (!cartItems) {
    return;
  }

  if (cart.length === 0) {
    cartItems.innerHTML = renderEmptyCart();
    openCheckoutButton.disabled = true;

    if (clearCartButton) {
      clearCartButton.disabled = true;
    }

    return;
  }

  cartItems.innerHTML = cart.map(renderCartItem).join("");
  openCheckoutButton.disabled = false;

  if (clearCartButton) {
    clearCartButton.disabled = false;
  }
};

if (user) {
  const userName = document.getElementById("user-name") as HTMLSpanElement;
  userName.textContent = `${user.nombre} ${user.apellido}`;
}

logoutButton.addEventListener("click", logOut);

cartItems?.addEventListener("click", (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  const button = target.closest<HTMLButtonElement>("button[data-action]");

  if (!button) {
    return;
  }

  const itemId = Number(button.dataset.itemId);

  if (Number.isNaN(itemId)) {
    return;
  }

  switch (button.dataset.action) {
    case "increment":
      incrementCartItem(itemId);
      break;
    case "decrement":
      decrementCartItem(itemId);
      break;
    case "remove":
      removeCartItem(itemId);
      break;
  }

  renderCart();
});

clearCartButton?.addEventListener("click", () => {
  clearCart();
  renderCart();
});

openCheckoutButton.addEventListener("click", () => {
  if (getCart().length === 0) {
    return;
  }

  openCheckoutModal();
});

closeCheckoutButton.addEventListener("click", () => {
  setCheckoutStatus("");
  checkoutDialog.close();
});

checkoutDialog.addEventListener("click", (event: MouseEvent) => {
  if (event.target === checkoutDialog) {
    setCheckoutStatus("");
    checkoutDialog.close();
  }
});

checkoutForm.addEventListener("submit", (event: SubmitEvent) => {
  event.preventDefault();

  if (!checkoutForm.checkValidity()) {
    checkoutForm.reportValidity();
    return;
  }

  const userId = user?.id;

  if (!userId) {
    setCheckoutStatus("No se pudo identificar el usuario", true);
    return;
  }

  const cart = getCart();

  if (cart.length === 0) {
    setCheckoutStatus("El carrito está vacío", true);
    return;
  }

  const formData = new FormData(checkoutForm);
  const telefono = String(formData.get("phone") ?? "").trim();
  const direccion = String(formData.get("address") ?? "").trim();
  const formaPago = String(formData.get("payment") ?? "");
  const notaAdicional = String(formData.get("notes") ?? "").trim();

  const requestBody = {
    estado: "PENDIENTE",
    formaPago,
    telefono,
    direccion,
    notaAdicional,
    idUsuario: userId,
    detallePedido: cart.map((item) => ({
      idProducto: item.id,
      cantidad: item.quantity,
    })),
  };

  (async () => {
    try {
      if (confirmPaymentButton) {
        confirmPaymentButton.disabled = true;
      }

      setCheckoutStatus("Procesando pedido...");

      const response = await fetch(`${API_BASE_URL}/pedidos`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        let errorMessage = "No se pudo confirmar el pedido";

        try {
          const errorResponse = (await response.json()) as Partial<ErrorResponse>;
          errorMessage = errorResponse.message ?? errorMessage;
        } catch {
          // keep default error message
        }

        setCheckoutStatus(errorMessage, true);
        updateCheckoutButtonState();
        return;
      }

      clearCart();
      renderCart();
      checkoutForm.reset();
      renderPaymentOptions();
      updateCheckoutButtonState();
      setCheckoutStatus("Pedido confirmado correctamente");
      window.setTimeout(() => {
        setCheckoutStatus("");
        checkoutDialog.close();
      }, CHECKOUT_SUCCESS_CLOSE_DELAY);
    } catch {
      setCheckoutStatus("No se pudo confirmar el pedido", true);
      updateCheckoutButtonState();
    }
  })();
});

checkoutPhoneInput?.addEventListener("input", updateCheckoutButtonState);
checkoutAddressInput?.addEventListener("input", updateCheckoutButtonState);
checkoutPaymentSelect?.addEventListener("change", updateCheckoutButtonState);

renderPaymentOptions();
renderCart();
updateCheckoutButtonState();
