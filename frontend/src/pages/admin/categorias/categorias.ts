import type { Category } from "../../../types/category";
import { Rol } from "../../../types/rol";
import { getAuthorizedUser, logOut } from "../../../utils/auth";

const API_BASE_URL = "http://localhost:8080";

const user = getAuthorizedUser(Rol.ADMIN);
const userName = document.getElementById("user-name") as HTMLSpanElement;
const logoutButton = document.getElementById(
  "logout-button"
) as HTMLButtonElement;
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
          <td class="px-6 py-5 text-[#5f6b76]">${category.descripcion}</td>
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

if (user) {
  userName.textContent = `${user.nombre}`;
  logoutButton.addEventListener("click", logOut);
}

void loadCategories();
