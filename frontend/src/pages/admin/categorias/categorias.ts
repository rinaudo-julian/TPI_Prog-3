import type { Category } from "../../../types/category";
import type { ErrorResponse } from "../../../types/error";
import { Rol } from "../../../types/rol";
import { getAuthorizedUser, logOut } from "../../../utils/auth";

const API_BASE_URL = "http://localhost:8080";

const user = getAuthorizedUser(Rol.ADMIN);
const userName = document.getElementById("user-name") as HTMLSpanElement;
const logoutButton = document.getElementById(
  "logout-button"
) as HTMLButtonElement;
const openCategoryModalButton = document.getElementById(
  "open-category-modal"
) as HTMLButtonElement;
const categoryDialog = document.getElementById(
  "category-dialog"
) as HTMLDialogElement;
const categoryDialogTitle = document.getElementById(
  "category-dialog-title"
) as HTMLHeadingElement;
const closeCategoryModalButton = document.getElementById(
  "close-category-modal"
) as HTMLButtonElement;
const categoryForm = document.getElementById(
  "category-form"
) as HTMLFormElement;
const categorySubmitButton = document.getElementById(
  "category-submit-button"
) as HTMLButtonElement;
const categoryNameInput = document.getElementById(
  "category-name"
) as HTMLInputElement;
const categoryDescriptionInput = document.getElementById(
  "category-description"
) as HTMLTextAreaElement;
const categoryFormStatus = document.getElementById(
  "category-form-status"
) as HTMLDivElement;
const categoriesPageStatus = document.getElementById(
  "categories-page-status"
) as HTMLParagraphElement;
const categoriesTableBody = document.getElementById(
  "categories-table-body"
) as HTMLTableSectionElement;

type CategoryFormMode = "create" | "edit";

let categoryFormMode: CategoryFormMode = "create";
let selectedCategoryId: number | null = null;
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
    <td colspan="3" class="px-6 py-10 text-center text-[14px] text-[#7b7b7b]">
      ${message}
    </td>
  </tr>
`;

const setCategoryFormStatus = (message: string, isError = false) => {
  categoryFormStatus.textContent = message;
  categoryFormStatus.className = isError
    ? "rounded-md bg-danger px-4 py-3 text-[13px] font-medium text-white"
    : "rounded-md bg-success px-4 py-3 text-[13px] font-medium text-white";
};

const clearCategoryFormStatus = () => {
  categoryFormStatus.textContent = "";
  categoryFormStatus.className =
    "hidden rounded-md px-4 py-3 text-[13px] font-medium";
};

const setPageStatus = (message: string, isError = false) => {
  categoriesPageStatus.textContent = message;
  categoriesPageStatus.className = isError
    ? "ml-[110px] mt-4 text-[13px] font-medium text-danger"
    : "ml-[110px] mt-4 text-[13px] font-medium text-success";
};

const clearPageStatus = () => {
  categoriesPageStatus.textContent = "";
  categoriesPageStatus.className =
    "ml-[110px] mt-4 hidden text-[13px] font-medium";
};

const openCategoryModal = (mode: CategoryFormMode, category?: Category) => {
  categoryFormMode = mode;
  selectedCategoryId = category?.id ?? null;

  categoryDialogTitle.textContent =
    mode === "create" ? "Nueva Categoría" : "Editar Categoría";
  categorySubmitButton.textContent = mode === "create" ? "Guardar" : "Editar";

  clearCategoryFormStatus();
  categoryForm.reset();

  if (category) {
    categoryNameInput.value = category.nombre;
    categoryDescriptionInput.value = category.descripcion ?? "";
  }

  categoryDialog.showModal();
  categoryNameInput.focus();
};

const closeCategoryModal = () => {
  clearCategoryFormStatus();
  categoryFormMode = "create";
  selectedCategoryId = null;
  categoryDialogTitle.textContent = "Nueva Categoría";
  categorySubmitButton.textContent = "Guardar";
  categoryDialog.close();
};

const renderCategories = (categories: Category[]) => {
  loadedCategories = categories;
  clearPageStatus();

  if (!categories.length) {
    categoriesTableBody.innerHTML = renderEmptyState(
      "No hay categorías para mostrar"
    );
    return;
  }

  categoriesTableBody.innerHTML = categories
    .map(
      (category, index) => `
        <tr class="${index % 2 === 0 ? "bg-white" : "bg-[#fafafa]"} border-b border-[#eef1f5]">
          <td class="px-6 py-5 font-medium text-text-strong">${category.nombre}</td>
          <td class="px-6 py-5 text-[#5f6b76]">${category.descripcion ?? ""}</td>
          <td class="px-6 py-5">
            <div class="flex flex-wrap gap-3">
              <button
                type="button"
                data-action="edit"
                data-category-id="${category.id}"
                class="cursor-pointer rounded-md bg-gray-soft hover:bg-gray-soft-hover px-5 py-2 text-[13px] font-semibold text-[#4f5e67]"
              >
                Editar
              </button>
              <button
                type="button"
                data-action="delete"
                data-category-id="${category.id}"
                class="cursor-pointer rounded-md bg-danger hover:bg-danger-hover px-5 py-2 text-[13px] font-semibold text-white"
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

const loadCategories = async () => {
  categoriesTableBody.innerHTML = "";
  clearPageStatus();

  try {
    const categories = await fetchJson<Category[]>("/categorias");
    renderCategories(categories);
  } catch {
    categoriesTableBody.innerHTML = renderEmptyState(
      "No se pudieron cargar las categorías"
    );
  }
};

const deleteCategory = async (categoryId: number) => {
  try {
    const response = await fetch(`${API_BASE_URL}/categorias/${categoryId}`, {
      method: "DELETE"
    });

    if (!response.ok) {
      let errorMessage = "No se pudo eliminar la categoría";

      try {
        const errorResponse = (await response.json()) as Partial<ErrorResponse>;
        errorMessage = errorResponse.message ?? errorMessage;
      } catch {
        // keep default error message
      }

      setPageStatus(errorMessage, true);
      return;
    }

    await loadCategories();
  } catch {
    setPageStatus("No se pudo eliminar la categoría", true);
  }
};

const createCategory = async () => {
  const nombre = categoryNameInput.value.trim();
  const descripcion = categoryDescriptionInput.value.trim();

  if (!nombre) {
    categoryForm.reportValidity();
    return;
  }

  const requestBody = {
    nombre,
    descripcion: descripcion || undefined
  };

  try {
    const url =
      categoryFormMode === "edit" && selectedCategoryId !== null
        ? `${API_BASE_URL}/categorias/${selectedCategoryId}`
        : `${API_BASE_URL}/categorias`;

    const response = await fetch(url, {
      method: categoryFormMode === "edit" ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      let errorMessage =
        categoryFormMode === "edit"
          ? "No se pudo editar la categoría"
          : "No se pudo crear la categoría";

      try {
        const errorResponse = (await response.json()) as Partial<ErrorResponse>;
        errorMessage = errorResponse.message ?? errorMessage;
      } catch {
        // keep default error message
      }

      setCategoryFormStatus(errorMessage, true);
      return;
    }

    closeCategoryModal();
    await loadCategories();
  } catch {
    setCategoryFormStatus(
      categoryFormMode === "edit"
        ? "No se pudo editar la categoría"
        : "No se pudo crear la categoría",
      true
    );
  }
};

if (user) {
  userName.textContent = `${user.nombre}`;
  logoutButton.addEventListener("click", logOut);
}

openCategoryModalButton.addEventListener("click", () => {
  openCategoryModal("create");
});
closeCategoryModalButton.addEventListener("click", closeCategoryModal);

categoryDialog.addEventListener("click", (event: MouseEvent) => {
  if (event.target === categoryDialog) {
    closeCategoryModal();
  }
});

categoriesTableBody.addEventListener("click", (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  const button = target.closest<HTMLButtonElement>("button[data-action]");

  if (!button) {
    return;
  }

  const categoryId = Number(button.dataset.categoryId);

  if (Number.isNaN(categoryId)) {
    return;
  }

  const category = loadedCategories.find((item) => item.id === categoryId);

  if (!category) {
    return;
  }

  switch (button.dataset.action) {
    case "edit":
      openCategoryModal("edit", category);
      break;
    case "delete":
      deleteCategory(categoryId);
      break;
  }
});

categoryForm.addEventListener("submit", (event: SubmitEvent) => {
  event.preventDefault();

  if (!categoryForm.checkValidity()) {
    categoryForm.reportValidity();
    return;
  }

  void createCategory();
});

void loadCategories();
