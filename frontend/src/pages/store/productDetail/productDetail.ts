import { Rol } from "../../../types/rol";
import type { Product } from "../../../types/product";
import { addProductToCart, getCart, getCartCount } from "../../../utils/cart";
import { getAuthorizedUser, logOut } from "../../../utils/auth";
import { navigate } from "../../../utils/routes";

const API_BASE_URL = "http://localhost:8080";

const user = getAuthorizedUser(Rol.USUARIO);
const logoutButton = document.getElementById(
  "logout-button"
) as HTMLButtonElement;
const backButton = document.getElementById("back-button") as HTMLButtonElement;
const userName = document.getElementById("user-name") as HTMLSpanElement;
const cartCount = document.getElementById(
  "cart-count"
) as HTMLSpanElement | null;
const productStatus = document.getElementById(
  "product-status"
) as HTMLParagraphElement;
const productDetail = document.getElementById("product-detail") as HTMLElement;
const ADD_TO_CART_LABEL = "Agregar al carrito";
const ADD_TO_CART_SUCCESS_LABEL = "Agregado correctamente";
const ADD_TO_CART_FEEDBACK_TIMEOUT = 1000;
let addToCartFeedbackTimeoutId: number | undefined;

const renderProductDetail = (product: Product) => {
  productDetail.innerHTML = `
    <div class="w-full overflow-hidden rounded-xl bg-white shadow-[0_10px_26px_rgba(0,0,0,0.08)]">
      <div class="aspect-4/3">
        <img
          src="${`/src/assets/products/${product.imagen}`}"
          alt="${product.nombre}"
          class="h-full w-full rounded-lg object-cover"
        />
      </div>
    </div>

    <div class="pt-1">
      <h1 class="text-[30px] font-bold tracking-tight text-[#333]">
        ${product.nombre}
      </h1>

      <p class="mt-4 text-[22px] font-bold text-primary-soft">
        $${product.precio.toFixed(2)}
      </p>

      <span
        class="mt-3 inline-flex rounded-full px-4 py-1 text-[11px] font-semibold text-white ${
          product.disponible ? "bg-success" : "bg-warning"
        }"
      >
        ${product.disponible ? `Disponible (stock: ${product.stock})` : "No disponible"}
      </span>

      <p class="mt-4 text-[13px] text-[#a0a0a0]">
        ${product.descripcion}
      </p>

      <div class="mt-6">
        <p class="text-[13px] font-semibold text-text-strong">
          Categoría: ${product.categoria.nombre}
        </p>
        <p class="mt-1 text-[13px] text-[#a0a0a0]">
          ${product.categoria.descripcion}
        </p>
      </div>

      <div class="mt-6">
        <p class="text-[13px] font-semibold text-text-strong">Cantidad:</p>
        <div class="mt-3 flex items-center gap-3">
          <button
            id="quantity-minus"
            type="button"
            class="cursor-pointer flex h-8 w-8 items-center justify-center rounded-md bg-gray-soft text-[18px] font-bold text-[#7c8b94] transition disabled:cursor-not-allowed disabled:opacity-50"
          >
            -
          </button>
          <input
            id="quantity-input"
            type="number"
            value="1"
            min="1"
            max="${product.stock}"
            step="1"
            inputmode="numeric"
            class="h-8 w-16 rounded-md border border-border bg-white text-center text-[14px] font-semibold text-text-strong outline-none"
          />
          <button
            id="quantity-plus"
            type="button"
            class="cursor-pointer flex h-8 w-8 items-center justify-center rounded-md bg-gray-soft text-[18px] font-bold text-[#7c8b94] transition disabled:cursor-not-allowed disabled:opacity-50"
          >
            +
          </button>
        </div>
        <p id="quantity-feedback" class="mt-2 text-[11px] text-[#7a7a7a]"></p>
      </div>

      <div class="mt-8 flex flex-wrap gap-3">
        <button
          id="add-to-cart-button"
          class="cursor-pointer h-11 min-w-[320px] rounded-md bg-primary px-6 text-[14px] font-semibold text-white shadow-[0_8px_20px_rgba(255,106,26,0.28)] transition hover:bg-primary-hover disabled:cursor-not-allowed disabled:opacity-50"
        >
          Agregar al carrito
        </button>
      </div>
    </div>
  `;

  const quantityInput = document.getElementById(
    "quantity-input"
  ) as HTMLInputElement | null;
  const minusButton = document.getElementById(
    "quantity-minus"
  ) as HTMLButtonElement | null;
  const plusButton = document.getElementById(
    "quantity-plus"
  ) as HTMLButtonElement | null;
  const quantityFeedback = document.getElementById(
    "quantity-feedback"
  ) as HTMLParagraphElement | null;
  const addToCartButton = document.getElementById(
    "add-to-cart-button"
  ) as HTMLButtonElement | null;

  if (
    !quantityInput ||
    !minusButton ||
    !plusButton ||
    !quantityFeedback ||
    !addToCartButton
  ) {
    return;
  }

  if (product.stock === 0) {
    quantityInput.disabled = true;
    minusButton.disabled = true;
    plusButton.disabled = true;
    addToCartButton.disabled = true;
    quantityFeedback.textContent = "Sin stock disponible";
    return;
  }

  quantityFeedback.textContent = `Máximo disponible: ${product.stock}`;

  const updateButtons = () => {
    minusButton.disabled = Number(quantityInput.value) <= 0;
    plusButton.disabled = Number(quantityInput.value) >= product.stock;
  };

  minusButton.addEventListener("click", () => {
    const nextValue = Math.max(1, Number(quantityInput.value) - 1);
    quantityInput.value = String(nextValue);
    updateButtons();
  });

  plusButton.addEventListener("click", () => {
    const nextValue = Math.min(product.stock, Number(quantityInput.value) + 1);
    quantityInput.value = String(nextValue);
    updateButtons();
  });

  quantityInput.addEventListener("input", () => {
    const rawValue = Number(quantityInput.value);

    if (Number.isNaN(rawValue)) {
      quantityInput.value = "1";
    } else if (rawValue < 1) {
      quantityInput.value = "1";
    } else if (rawValue > product.stock) {
      quantityInput.value = String(product.stock);
    }

    updateButtons();
  });

  addToCartButton.addEventListener("click", () => {
    const selectedQuantity = Number(quantityInput.value);
    const existingCartItem = getCart().find((item) => item.id === product.id);
    const currentQuantity = existingCartItem?.quantity ?? 0;

    if (currentQuantity + selectedQuantity > product.stock) {
      quantityFeedback.textContent =
        "Ya tenés la cantidad límite de compra para este producto.";
      return;
    }

    addProductToCart(product, selectedQuantity);
    if (addToCartFeedbackTimeoutId) {
      window.clearTimeout(addToCartFeedbackTimeoutId);
    }
    addToCartButton.textContent = ADD_TO_CART_SUCCESS_LABEL;
    addToCartFeedbackTimeoutId = window.setTimeout(() => {
      addToCartButton.textContent = ADD_TO_CART_LABEL;
    }, ADD_TO_CART_FEEDBACK_TIMEOUT);
    quantityFeedback.textContent = `Máximo disponible: ${product.stock}`;

    if (cartCount) {
      cartCount.textContent = String(getCartCount());
    }
  });

  updateButtons();
};

const renderErrorState = (message: string) => {
  productDetail.innerHTML = `
    <div class="rounded-xl border border-danger/30 bg-danger/10 p-6 text-danger">
      <p class="text-[18px] font-bold">No se pudo cargar el producto</p>
      <p class="mt-2 text-[13px]">${message}</p>
    </div>
  `;
};

const loadProduct = async () => {
  const productId = new URLSearchParams(window.location.search).get(
    "product_id"
  );

  if (!productId) {
    productStatus.textContent = "No se encontró el producto solicitado";
    productDetail.innerHTML = "";
    return;
  }

  productStatus.textContent = "Cargando producto...";

  try {
    const response = await fetch(`${API_BASE_URL}/productos/${productId}`);

    if (!response.ok) {
      const error = (await response.json()) as { message?: string };
      productStatus.textContent =
        error.message ?? "No se pudo cargar el producto";
      renderErrorState(productStatus.textContent);
      return;
    }

    const product = (await response.json()) as Product;
    productStatus.textContent = "";
    renderProductDetail(product);
  } catch {
    productStatus.textContent = "No se pudo cargar el producto";
    renderErrorState(productStatus.textContent);
  }
};

if (user) {
  userName.textContent = `${user.nombre} ${user.apellido}`;
  logoutButton.addEventListener("click", logOut);
  backButton.addEventListener("click", () => navigate("home"));
  if (cartCount) {
    cartCount.textContent = String(getCartCount());
  }
  loadProduct();
}
