import type { Category } from "../../../types/category";
import type { ErrorResponse } from "../../../types/error";
import type { Product } from "../../../types/product";
import { Rol } from "../../../types/rol";
import { getAuthorizedUser, logOut } from "../../../utils/auth";

const API_BASE_URL = "http://localhost:8080";

const user = getAuthorizedUser(Rol.ADMIN);
const userName = document.getElementById("user-name") as HTMLSpanElement;
const logoutButton = document.getElementById(
  "logout-button"
) as HTMLButtonElement;
const openProductModalButton = document.getElementById(
  "open-product-modal"
) as HTMLButtonElement;
const productDialog = document.getElementById(
  "product-dialog"
) as HTMLDialogElement;
const productDialogTitle = document.getElementById(
  "product-dialog-title"
) as HTMLHeadingElement;
const closeProductModalButton = document.getElementById(
  "close-product-modal"
) as HTMLButtonElement;
const productForm = document.getElementById("product-form") as HTMLFormElement;
const productSubmitButton = document.getElementById(
  "product-submit-button"
) as HTMLButtonElement;
const productNameInput = document.getElementById(
  "product-name"
) as HTMLInputElement;
const productPriceInput = document.getElementById(
  "product-price"
) as HTMLInputElement;
const productDescriptionInput = document.getElementById(
  "product-description"
) as HTMLTextAreaElement;
const productStockInput = document.getElementById(
  "product-stock"
) as HTMLInputElement;
const productCategorySelect = document.getElementById(
  "product-category"
) as HTMLSelectElement;
const productImageInput = document.getElementById(
  "product-image"
) as HTMLInputElement;
const productAvailableInput = document.getElementById(
  "product-available"
) as HTMLInputElement;
const productFormStatus = document.getElementById(
  "product-form-status"
) as HTMLDivElement;
const productsPageStatus = document.getElementById(
  "products-page-status"
) as HTMLParagraphElement;
const productsTableBody = document.getElementById(
  "products-table-body"
) as HTMLTableSectionElement;

type ProductFormMode = "create" | "edit";

let productFormMode: ProductFormMode = "create";
let selectedProductId: number | null = null;
let loadedProducts: Product[] = [];
let loadedCategories: Category[] = [];

const fetchJson = async <T>(path: string): Promise<T> => {
  const response = await fetch(`${API_BASE_URL}${path}`);

  if (!response.ok) {
    throw new Error(`Request failed for ${path}`);
  }

  return (await response.json()) as T;
};

const renderEmptyState = (message: string) => `
  <tr>
    <td colspan="8" class="px-6 py-10 text-center text-[14px] text-[#7b7b7b]">
      ${message}
    </td>
  </tr>
`;

const setFormStatus = (message: string, isError = false) => {
  productFormStatus.textContent = message;
  productFormStatus.className = isError
    ? "rounded-md bg-danger px-4 py-3 text-[13px] font-medium text-white"
    : "rounded-md bg-success px-4 py-3 text-[13px] font-medium text-white";
};

const clearFormStatus = () => {
  productFormStatus.textContent = "";
  productFormStatus.className =
    "hidden rounded-md px-4 py-3 text-[13px] font-medium";
};

const setPageStatus = (message: string, isError = false) => {
  productsPageStatus.textContent = message;
  productsPageStatus.className = isError
    ? "ml-[110px] mt-4 text-[13px] font-medium text-danger"
    : "ml-[110px] mt-4 text-[13px] font-medium text-success";
};

const clearPageStatus = () => {
  productsPageStatus.textContent = "";
  productsPageStatus.className =
    "ml-[110px] mt-4 hidden text-[13px] font-medium";
};

const formatCurrency = (value: number) => `$${value.toFixed(2)}`;

const renderCategoryOptions = (selectedCategoryId?: number) => {
  if (!loadedCategories.length) {
    productCategorySelect.innerHTML = `
      <option value="">No hay categorías disponibles</option>
    `;
    productCategorySelect.disabled = true;
    return;
  }

  productCategorySelect.disabled = false;
  productCategorySelect.innerHTML = [
    '<option value="">Seleccionar categoría</option>',
    ...loadedCategories.map(
      (category) => `
        <option value="${category.id}" ${category.id === selectedCategoryId ? "selected" : ""}>
          ${category.nombre}
        </option>
      `
    )
  ].join("");
};

const loadCategories = async () => {
  if (loadedCategories.length) {
    return;
  }

  try {
    loadedCategories = await fetchJson<Category[]>("/categorias");
  } catch {
    loadedCategories = [];
  }
};

const openProductModal = async (mode: ProductFormMode, product?: Product) => {
  productFormMode = mode;
  selectedProductId = product?.id ?? null;

  productDialogTitle.textContent =
    mode === "create" ? "Nuevo Producto" : "Editar Producto";
  productSubmitButton.textContent = mode === "create" ? "Guardar" : "Editar";

  clearFormStatus();
  productForm.reset();

  await loadCategories();

  if (product) {
    productNameInput.value = product.nombre;
    productPriceInput.value = String(product.precio);
    productDescriptionInput.value = product.descripcion ?? "";
    productStockInput.value = String(product.stock);
    productImageInput.value = product.imagen ?? "";
    productAvailableInput.checked = product.disponible;
    renderCategoryOptions(product.categoria.id);
  } else {
    productAvailableInput.checked = true;
    renderCategoryOptions();
  }

  productDialog.showModal();
  productNameInput.focus();
};

const closeProductModal = () => {
  clearFormStatus();
  productFormMode = "create";
  selectedProductId = null;
  productDialogTitle.textContent = "Nuevo Producto";
  productSubmitButton.textContent = "Guardar";
  productDialog.close();
};

const renderProducts = (products: Product[]) => {
  loadedProducts = products;
  clearPageStatus();

  if (!products.length) {
    productsTableBody.innerHTML = renderEmptyState(
      "No hay productos para mostrar"
    );
    return;
  }

  productsTableBody.innerHTML = products
    .map(
      (product, index) => `
        <tr class="${index % 2 === 0 ? "bg-white" : "bg-[#fafafa]"} border-b border-[#eef1f5] align-top">
          <td class="px-6 py-5">
            <img
              src="${product.imagen}"
              alt="${product.nombre}"
              class="h-10 w-10 rounded-md object-cover"
            />
          </td>
          <td class="px-6 py-5 font-medium text-text-strong">${product.nombre}</td>
          <td class="px-6 py-5 text-[#5f6b76]">${product.descripcion ?? ""}</td>
          <td class="px-6 py-5 font-medium text-text-strong">${formatCurrency(product.precio)}</td>
          <td class="px-6 py-5 text-[#5f6b76]">${product.categoria.nombre}</td>
          <td class="px-6 py-5 text-[#5f6b76]">${product.stock}</td>
          <td class="px-6 py-5">
            <span class="inline-flex rounded-full px-3 py-1 text-[11px] font-semibold text-white ${
              product.disponible ? "bg-success" : "bg-danger"
            }">
              ${product.disponible ? "Disponible" : "No disponible"}
            </span>
          </td>
          <td class="px-6 py-5">
            <div class="flex flex-wrap gap-3">
              <button
                type="button"
                data-action="edit"
                data-product-id="${product.id}"
                class="cursor-pointer rounded-md bg-gray-soft px-5 py-2 text-[13px] font-semibold text-[#4f5e67] hover:bg-gray-soft-hover"
              >
                Editar
              </button>
              <button
                type="button"
                data-action="delete"
                data-product-id="${product.id}"
                class="cursor-pointer rounded-md bg-danger px-5 py-2 text-[13px] font-semibold text-white hover:bg-danger-hover"
              >
                Eliminar
              </button>
            </div>
          </td>
        </tr>
      `
    )
    .join("");
};

const loadProducts = async () => {
  productsTableBody.innerHTML = "";
  clearPageStatus();

  try {
    const products = await fetchJson<Product[]>("/productos");
    renderProducts(products);
  } catch {
    productsTableBody.innerHTML = renderEmptyState(
      "No se pudieron cargar los productos"
    );
  }
};

const extractErrorMessage = async (
  response: Response,
  fallbackMessage: string
) => {
  try {
    const errorResponse = (await response.json()) as Partial<ErrorResponse>;
    return errorResponse.message ?? fallbackMessage;
  } catch {
    return fallbackMessage;
  }
};

const saveProduct = async () => {
  const nombre = productNameInput.value.trim();
  const precio = Number(productPriceInput.value);
  const descripcion = productDescriptionInput.value.trim();
  const stock = Number(productStockInput.value);
  const imagen = productImageInput.value.trim();
  const disponible = productAvailableInput.checked;
  const categoryValue = productCategorySelect.value;

  if (!categoryValue) {
    setFormStatus("Debés seleccionar una categoría", true);
    return;
  }

  const idCategoria = Number(categoryValue);

  const requestBody = {
    nombre,
    precio,
    descripcion: descripcion || undefined,
    stock,
    imagen: imagen || undefined,
    disponible,
    idCategoria
  };

  try {
    const url =
      productFormMode === "edit" && selectedProductId !== null
        ? `${API_BASE_URL}/productos/${selectedProductId}`
        : `${API_BASE_URL}/productos`;

    const response = await fetch(url, {
      method: productFormMode === "edit" ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      const errorMessage = await extractErrorMessage(
        response,
        productFormMode === "edit"
          ? "No se pudo editar el producto"
          : "No se pudo crear el producto"
      );
      setFormStatus(errorMessage, true);
      return;
    }

    closeProductModal();
    await loadProducts();
  } catch {
    setFormStatus(
      productFormMode === "edit"
        ? "No se pudo editar el producto"
        : "No se pudo crear el producto",
      true
    );
  }
};

const deleteProduct = async (productId: number) => {
  try {
    const response = await fetch(`${API_BASE_URL}/productos/${productId}`, {
      method: "DELETE"
    });

    if (!response.ok) {
      setPageStatus("No se pudo eliminar el producto", true);
      return;
    }

    await loadProducts();
  } catch {
    setPageStatus("No se pudo eliminar el producto", true);
  }
};

if (user) {
  userName.textContent = `${user.nombre}`;
  logoutButton.addEventListener("click", logOut);
}

openProductModalButton.addEventListener("click", () => {
  openProductModal("create");
});

closeProductModalButton.addEventListener("click", closeProductModal);

productDialog.addEventListener("click", (event: MouseEvent) => {
  if (event.target === productDialog) {
    closeProductModal();
  }
});

productsTableBody.addEventListener("click", (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  const button = target.closest<HTMLButtonElement>("button[data-action]");

  if (!button) {
    return;
  }

  const productId = Number(button.dataset.productId);

  if (Number.isNaN(productId)) {
    return;
  }

  const product = loadedProducts.find((item) => item.id === productId);

  if (!product) {
    return;
  }

  switch (button.dataset.action) {
    case "edit":
      openProductModal("edit", product);
      break;
    case "delete":
      deleteProduct(productId);
      break;
  }
});

productForm.addEventListener("submit", (event: SubmitEvent) => {
  event.preventDefault();
  saveProduct();
});

loadProducts();
