import type { Category } from "../../../types/category";
import type { Order } from "../../../types/order";
import type { Product } from "../../../types/product";
import { Rol } from "../../../types/rol";
import { getAuthorizedUser, logOut } from "../../../utils/auth";

const API_BASE_URL = "http://localhost:8080";

const user = getAuthorizedUser(Rol.ADMIN);
const userName = document.getElementById("user-name") as HTMLSpanElement;
const logoutButton = document.getElementById(
  "logout-button"
) as HTMLButtonElement;

const categoriesCount = document.getElementById(
  "categories-count"
) as HTMLParagraphElement;
const productsCount = document.getElementById(
  "products-count"
) as HTMLParagraphElement;
const ordersCount = document.getElementById(
  "orders-count"
) as HTMLParagraphElement;
const availableProductsCount = document.getElementById(
  "available-products-count"
) as HTMLParagraphElement;
const quickSummaryRevenue = document.getElementById(
  "quick-summary-revenue"
) as HTMLParagraphElement;
const quickSummaryPending = document.getElementById(
  "quick-summary-pending"
) as HTMLParagraphElement;
const quickSummaryPreparing = document.getElementById(
  "quick-summary-preparing"
) as HTMLParagraphElement;
const quickSummaryCompleted = document.getElementById(
  "quick-summary-completed"
) as HTMLParagraphElement;

if (user) {
  userName.textContent = `${user.nombre}`;
  logoutButton.addEventListener("click", logOut);
}

const fetchJson = async <T>(path: string): Promise<T> => {
  const response = await fetch(`${API_BASE_URL}${path}`);

  if (!response.ok) {
    throw new Error(`Request failed for ${path}`);
  }

  return (await response.json()) as T;
};

const formatCurrency = (value: number) => `$${value.toFixed(2)}`;

const loadStats = async () => {
  try {
    const [categories, products, orders] = await Promise.all([
      fetchJson<Category[]>("/categorias"),
      fetchJson<Product[]>("/productos"),
      fetchJson<Order[]>("/pedidos")
    ]);

    const activeProducts = products.filter((product) => product.disponible);
    const pendingOrders = orders.filter((order) => order.estado === "PENDIENTE");
    const preparingOrders = orders.filter(
      (order) => order.estado === "CONFIRMADO"
    );
    const completedOrders = orders.filter((order) => order.estado === "TERMINADO");
    const totalRevenue = orders.reduce((sum, order) => sum + order.total, 0);

    categoriesCount.textContent = String(categories.length);
    productsCount.textContent = String(products.length);
    ordersCount.textContent = String(orders.length);
    availableProductsCount.textContent = String(activeProducts.length);
    quickSummaryRevenue.textContent = formatCurrency(totalRevenue);
    quickSummaryPending.textContent = String(pendingOrders.length);
    quickSummaryPreparing.textContent = String(preparingOrders.length);
    quickSummaryCompleted.textContent = String(completedOrders.length);
  } catch {
    categoriesCount.textContent = "-";
    productsCount.textContent = "-";
    ordersCount.textContent = "-";
    availableProductsCount.textContent = "-";
    quickSummaryRevenue.textContent = "-";
    quickSummaryPending.textContent = "-";
    quickSummaryPreparing.textContent = "-";
    quickSummaryCompleted.textContent = "-";
  }
};

loadStats();
