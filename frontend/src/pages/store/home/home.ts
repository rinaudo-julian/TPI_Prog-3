import { Rol } from "../../../types/rol";
import type { Category } from "../../../types/category";
import type { Product } from "../../../types/product";
import { getAuthorizedUser, logOut } from "../../../utils/auth";

const API_BASE_URL = "http://localhost:8080";

const user = getAuthorizedUser(Rol.USUARIO);
const productSearch = document.getElementById(
  "product-search"
) as HTMLInputElement;
const productSort = document.getElementById(
  "product-sort"
) as HTMLSelectElement;
const categoriesNav = document.getElementById("categories-nav") as HTMLElement;
const logoutButton = document.getElementById(
  "logout-button"
) as HTMLButtonElement;
const userName = document.getElementById("user-name") as HTMLSpanElement;
const productsGrid = document.getElementById("products-grid") as HTMLElement;
const productsCount = document.getElementById(
  "products-count"
) as HTMLSpanElement;
const productsStatus = document.getElementById(
  "products-status"
) as HTMLParagraphElement;
let loadedProducts: Product[] = [];
let loadedCategories: Category[] = [];
let currentSearchQuery = "";
let currentSortValue = "default";
let activeCategoryId: number | null = null;

const renderProductCard = (product: Product) => {
  const cardClass = product.disponible
    ? "block w-full overflow-hidden rounded-lg bg-white shadow-[0_4px_16px_rgba(0,0,0,0.08)] transition hover:-translate-y-0.5 hover:shadow-[0_8px_22px_rgba(0,0,0,0.12)]"
    : "block w-full overflow-hidden rounded-lg bg-white opacity-50 shadow-[0_4px_16px_rgba(0,0,0,0.08)] pointer-events-none";

  const availabilityBadge = product.disponible
    ? '<span class="rounded-full bg-success px-4 py-1.5 text-[11px] font-semibold text-white">Ver detalle</span>'
    : '<span class="rounded-full bg-warning px-4 py-1.5 text-[11px] font-semibold text-white">No disponible</span>';

  const productMarkup = `
    <article>
      <div>
        <img
          src="${product.imagen}"
          alt="${product.nombre}"
          class="h-52 w-full object-cover"
        />
      </div>

      <div class="border-t border-[#ececec] p-4">
        <p class="text-[10px] font-semibold text-[#8d8d8d]">
          ${product.categoria.nombre}
        </p>
        <h2 class="mt-2 text-[13px] font-bold text-text-strong">
          ${product.nombre}
        </h2>
        <p class="mt-1 text-[10px] text-[#a3a3a3]">
          ${product.descripcion}
        </p>

        <div class="mt-4 flex items-center justify-between gap-3">
          <span class="text-[16px] font-bold text-primary-soft">
            $${product.precio.toFixed(2)}
          </span>
          ${availabilityBadge}
        </div>

        ${
          product.disponible
            ? '<p class="mt-3 text-[11px] text-[#7a7a7a]">Disponible para comprar</p>'
            : '<p class="mt-3 text-[11px] text-[#7a7a7a]">Producto sin stock</p>'
        }
      </div>
    </article>
  `;

  return product.disponible
    ? `<a href="../productDetail/productDetail.html?product_id=${product.id}" class="${cardClass}">${productMarkup}</a>`
    : `<div aria-disabled="true" class="${cardClass}">${productMarkup}</div>`;
};

const renderProducts = (products: Product[]) => {
  productsCount.textContent = `${products.length} producto${products.length === 1 ? "" : "s"}`;
  productsGrid.innerHTML = products.map(renderProductCard).join("");
};

const renderCategories = () => {
  const allCategoriesButton = `
    <button
      type="button"
      data-category-id="all"
      class="cursor-pointer flex w-full items-center gap-3 rounded-r-md border-l-2 px-4 py-3 text-left font-semibold transition ${
        activeCategoryId === null
          ? "border-primary bg-[#fff3ec] text-primary"
          : "border-transparent text-[#7b7b7b] hover:border-primary hover:bg-[#fff3ec] hover:text-primary"
      }"
    >
      <span>Todos los productos</span>
    </button>
  `;

  const categoriesButtons = loadedCategories
    .map(
      (category) => `
        <button
          type="button"
          data-category-id="${category.id}"
          class="cursor-pointer flex w-full items-center gap-3 rounded-r-md border-l-2 px-4 py-3 text-left font-semibold transition ${
            activeCategoryId === category.id
              ? "border-primary bg-[#fff3ec] text-primary"
              : "border-transparent text-[#7b7b7b] hover:border-primary hover:bg-[#fff3ec] hover:text-primary"
          }"
        >
          <span>${category.nombre}</span>
        </button>
      `
    )
    .join("");

  categoriesNav.innerHTML = `${allCategoriesButton}${categoriesButtons}`;

  categoriesNav
    .querySelectorAll<HTMLButtonElement>("button[data-category-id]")
    .forEach((button) => {
      button.addEventListener("click", async () => {
        const categoryId = button.dataset.categoryId;

        if (categoryId === "all") {
          activeCategoryId = null;
          await loadProducts();
          renderCategories();
          return;
        }

        activeCategoryId = Number(categoryId);
        currentSearchQuery = "";
        productSearch.value = "";
        await loadProductsByCategory(activeCategoryId);
        renderCategories();
      });
    });
};

const sortProducts = (products: Product[]) => {
  const sortedProducts = [...products];

  switch (currentSortValue) {
    case "price-asc":
      return sortedProducts.sort((a, b) => a.precio - b.precio);
    case "price-desc":
      return sortedProducts.sort((a, b) => b.precio - a.precio);
    case "name-asc":
      return sortedProducts.sort((a, b) => a.nombre.localeCompare(b.nombre));
    case "name-desc":
      return sortedProducts.sort((a, b) => b.nombre.localeCompare(a.nombre));
    default:
      return sortedProducts;
  }
};

const applyFilters = () => {
  const normalizedQuery = currentSearchQuery.trim().toLowerCase();
  let filteredProducts = loadedProducts;

  if (normalizedQuery) {
    filteredProducts = filteredProducts.filter((product) =>
      product.nombre.toLowerCase().includes(normalizedQuery)
    );
  }

  if (!filteredProducts.length) {
    productsCount.textContent = "0 productos";
    productsGrid.innerHTML = "";
    productsStatus.textContent = normalizedQuery
      ? `No se encontraron productos para "${currentSearchQuery.trim()}"`
      : "No hay productos para mostrar";
    return;
  }

  productsStatus.textContent = "";
  renderProducts(sortProducts(filteredProducts));
};

const loadProducts = async () => {
  productsStatus.textContent = "Cargando productos...";

  try {
    const response = await fetch(`${API_BASE_URL}/productos`);

    if (!response.ok) {
      productsStatus.textContent = "No se pudieron cargar los productos";
      return;
    }

    loadedProducts = (await response.json()) as Product[];
    productsStatus.textContent = "";
    applyFilters();
  } catch {
    productsStatus.textContent = "No se pudieron cargar los productos";
  }
};

const loadProductsByCategory = async (categoryId: number) => {
  productsStatus.textContent = "Cargando productos...";

  try {
    const response = await fetch(
      `${API_BASE_URL}/productos/categoria/${categoryId}`
    );

    if (!response.ok) {
      productsStatus.textContent = "No se pudieron cargar los productos";
      return;
    }

    loadedProducts = (await response.json()) as Product[];
    productsStatus.textContent = "";
    applyFilters();
  } catch {
    productsStatus.textContent = "No se pudieron cargar los productos";
  }
};

const loadCategories = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/categorias`);

    if (!response.ok) {
      productsStatus.textContent = "No se pudieron cargar las categorías";
      return;
    }

    loadedCategories = (await response.json()) as Category[];
    renderCategories();
  } catch {
    productsStatus.textContent = "No se pudieron cargar las categorías";
  }
};

if (user) {
  userName.textContent = `${user.nombre} ${user.apellido}`;
  logoutButton.addEventListener("click", logOut);
  productSearch.addEventListener("input", (event: InputEvent) => {
    const target = event.target as HTMLInputElement;
    currentSearchQuery = target.value;
    applyFilters();
  });
  productSort.addEventListener("change", (event: Event) => {
    const target = event.target as HTMLSelectElement;
    currentSortValue = target.value;
    applyFilters();
  });
  void loadCategories().then(() => loadProducts());
}
