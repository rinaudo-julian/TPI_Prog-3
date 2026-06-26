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
const closeCategoryModalButton = document.getElementById(
  "close-category-modal"
) as HTMLButtonElement;
const categoryForm = document.getElementById(
  "category-form"
) as HTMLFormElement;
const categoryNameInput = document.getElementById(
  "category-name"
) as HTMLInputElement;
const categoryDescriptionInput = document.getElementById(
  "category-description"
) as HTMLTextAreaElement;
const categoryFormStatus = document.getElementById(
  "category-form-status"
) as HTMLDivElement;
const categoriesTableBody = document.getElementById(
  "categories-table-body"
) as HTMLTableSectionElement;

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

const openCategoryModal = () => {
  clearCategoryFormStatus();
  categoryForm.reset();
  categoryDialog.showModal();
  categoryNameInput.focus();
};

const closeCategoryModal = () => {
  clearCategoryFormStatus();
  categoryDialog.close();
};

const renderCategories = (categories: Category[]) => {
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
                class="cursor-pointer rounded-md bg-gray-soft hover:bg-gray-soft-hover px-5 py-2 text-[13px] font-semibold text-[#4f5e67]"
              >
                Editar
              </button>
              <button
                type="button"
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

  try {
    const categories = await fetchJson<Category[]>("/categorias");
    renderCategories(categories);
  } catch {
    categoriesTableBody.innerHTML = renderEmptyState(
      "No se pudieron cargar las categorías"
    );
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
    const response = await fetch(`${API_BASE_URL}/categorias`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      let errorMessage = "No se pudo crear la categoría";

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
    setCategoryFormStatus("No se pudo crear la categoría", true);
  }
};

if (user) {
  userName.textContent = `${user.nombre}`;
  logoutButton.addEventListener("click", logOut);
}

openCategoryModalButton.addEventListener("click", openCategoryModal);
closeCategoryModalButton.addEventListener("click", closeCategoryModal);

categoryDialog.addEventListener("click", (event: MouseEvent) => {
  if (event.target === categoryDialog) {
    closeCategoryModal();
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
