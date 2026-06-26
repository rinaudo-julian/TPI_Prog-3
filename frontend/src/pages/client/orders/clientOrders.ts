import { Rol } from "../../../types/rol";
import { getAuthorizedUser, logOut } from "../../../utils/auth";
import { getCartCount } from "../../../utils/cart";
import type { ErrorResponse } from "../../../types/error";
import type { Order, OrderDetail, OrderStatus } from "../../../types/order";

const API_BASE_URL = "http://localhost:8080";

const ORDER_STATUS_META: Record<
  OrderStatus,
  { badge: string; panel: string; messageTitle: string; messageBody: string }
> = {
  PENDIENTE: {
    badge: "bg-[#ffe1a3] text-[#f4b23e]",
    panel: "border-[#ffd88b] bg-[#ffe29a] text-[#7a5a00]",
    messageTitle: "Tu pedido está siendo procesado",
    messageBody: "Te notificaremos cuando haya un cambio de estado."
  },
  CONFIRMADO: {
    badge: "bg-[#d7f5e7] text-success",
    panel: "border-[#bdeed8] bg-[#ecfbf5] text-[#116b4c]",
    messageTitle: "Tu pedido fue confirmado",
    messageBody: "Ya está en preparación y avanzando al siguiente paso."
  },
  TERMINADO: {
    badge: "bg-[#dbeafe] text-primary-soft",
    panel: "border-[#bcd8ff] bg-[#eef5ff] text-[#1d4ed8]",
    messageTitle: "Tu pedido ya está terminado",
    messageBody: "Podés revisarlo y esperar la entrega si corresponde."
  },
  CANCELADO: {
    badge: "bg-[#ffd9d9] text-danger",
    panel: "border-[#ffc0c0] bg-[#fff1f1] text-[#b91c1c]",
    messageTitle: "Tu pedido fue cancelado",
    messageBody: "Si necesitás ayuda, contactate con el local."
  }
};

const user = getAuthorizedUser(Rol.USUARIO);
const logoutButton = document.getElementById(
  "logout-button"
) as HTMLButtonElement;
const cartCount = document.getElementById(
  "cart-count"
) as HTMLSpanElement | null;
const ordersCount = document.getElementById(
  "orders-count"
) as HTMLParagraphElement | null;
const ordersStatus = document.getElementById(
  "orders-status"
) as HTMLParagraphElement | null;
const ordersList = document.getElementById("orders-list") as HTMLElement | null;
const orderDetailDialog = document.getElementById(
  "order-detail-dialog"
) as HTMLDialogElement | null;
const closeOrderDetailButton = document.getElementById(
  "close-order-detail"
) as HTMLButtonElement | null;
const orderDetailStatus = document.getElementById(
  "order-detail-status"
) as HTMLSpanElement | null;
const orderDetailDate = document.getElementById(
  "order-detail-date"
) as HTMLParagraphElement | null;
const orderDetailAddress = document.getElementById(
  "order-detail-address"
) as HTMLSpanElement | null;
const orderDetailPhone = document.getElementById(
  "order-detail-phone"
) as HTMLSpanElement | null;
const orderDetailPayment = document.getElementById(
  "order-detail-payment"
) as HTMLSpanElement | null;
const orderDetailNotesRow = document.getElementById(
  "order-detail-notes-row"
) as HTMLParagraphElement | null;
const orderDetailNotes = document.getElementById(
  "order-detail-notes"
) as HTMLSpanElement | null;
const orderDetailProducts = document.getElementById(
  "order-detail-products"
) as HTMLElement | null;
const orderDetailSubtotal = document.getElementById(
  "order-detail-subtotal"
) as HTMLSpanElement | null;
const orderDetailTotal = document.getElementById(
  "order-detail-total"
) as HTMLSpanElement | null;
const orderDetailMessage = document.getElementById(
  "order-detail-message"
) as HTMLDivElement | null;
const orderDetailMessageTitle = document.getElementById(
  "order-detail-message-title"
) as HTMLParagraphElement | null;
const orderDetailMessageBody = document.getElementById(
  "order-detail-message-body"
) as HTMLParagraphElement | null;

let loadedOrders: Order[] = [];

const formatCurrency = (value: number) => `$${value.toFixed(2)}`;

const formatDate = (value: string) =>
  new Intl.DateTimeFormat("es-AR", {
    day: "numeric",
    month: "long",
    year: "numeric",
    timeZone: "UTC"
  }).format(new Date(`${value}T00:00:00Z`));

const getOrderSubtotal = (order: Order) =>
  order.detalles.reduce((total, detail) => total + detail.subtotal, 0);

const getOrderStatusMeta = (status: OrderStatus) => ORDER_STATUS_META[status];

const renderEmptyState = (message: string, isError = false) => `
  <div class="rounded-lg border ${isError ? "border-danger/30 bg-danger/10 text-danger" : "border-dashed border-border bg-white text-[#7b7b7b]"} px-6 py-10 text-center shadow-[0_4px_14px_rgba(0,0,0,0.05)]">
    <p class="text-[16px] font-semibold ${isError ? "text-danger" : "text-text-strong"}">${message}</p>
  </div>
`;

const renderOrderSummary = (details: OrderDetail[]) => {
  const visibleDetails = details.slice(0, 3);
  const remainingCount = Math.max(details.length - visibleDetails.length, 0);

  const visibleItems = visibleDetails
    .map(
      (detail) => `
        <span class="inline-flex items-center rounded-full bg-[#f6f8fc] px-3 py-1 text-[12px] font-medium text-[#50606b]">
          • ${detail.producto.nombre} (x${detail.cantidad})
        </span>
      `
    )
    .join("");

  const counter =
    remainingCount > 0
      ? `
        <span class="inline-flex items-center rounded-full bg-primary/10 px-3 py-1 text-[12px] font-semibold text-primary">
          +${remainingCount} más
        </span>
      `
      : "";

  return `${visibleItems}${counter}`;
};

const renderOrderCard = (order: Order) => {
  const statusMeta = getOrderStatusMeta(order.estado);

  return `
    <button
      type="button"
      data-order-id="${order.id}"
      class="w-full cursor-pointer rounded-lg bg-white px-4 py-3 text-left shadow-[0_4px_14px_rgba(0,0,0,0.08)] transition hover:-translate-y-0.5 hover:shadow-[0_8px_22px_rgba(0,0,0,0.12)]"
    >
      <div class="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <h2 class="text-[13px] font-semibold text-text-strong">
            Pedido #${order.id}
          </h2>
          <p class="mt-1 text-[10px] text-[#a3a3a3]">
            📅 ${formatDate(order.fecha)}
          </p>
        </div>

        <span class="inline-flex items-center gap-2 rounded-full px-3 py-2 text-[10px] font-bold uppercase tracking-wide ${statusMeta.badge}">
          <span class="h-2 w-2 rounded-full bg-current"></span>
          ${order.estado}
        </span>
      </div>

      <div class="my-3 border-t border-[#edf0f4]"></div>

      <div class="space-y-3 text-[13px] text-[#5f6b76]">
        <div class="flex flex-wrap gap-2">
          ${renderOrderSummary(order.detalles)}
        </div>

        <div class="border-t border-[#edf0f4]"></div>

        <div class="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <p class="text-[13px] text-[#7a8791]">📦 ${order.detalles.length} producto${order.detalles.length === 1 ? "" : "s"}</p>
          <p class="text-right text-[16px] font-bold text-primary-soft">
            ${formatCurrency(order.total)}
          </p>
        </div>
      </div>
    </button>
  `;
};

const renderOrderProducts = (order: Order) =>
  order.detalles
    .map(
      (detail) => `
        <article class="rounded-2xl bg-[#f6f8fc] px-5 py-5 shadow-[0_4px_14px_rgba(0,0,0,0.04)] sm:px-6">
          <div class="flex items-center justify-between gap-4">
            <div>
              <h3 class="text-[17px] font-semibold text-text-strong">
                ${detail.producto.nombre}
              </h3>
              <p class="mt-2 text-[13px] text-[#7a8791]">
                Cantidad: ${detail.cantidad} - ${formatCurrency(detail.subtotal)}
              </p>
            </div>

            <p class="text-[17px] font-bold text-primary-soft">${formatCurrency(detail.subtotal)}</p>
          </div>
        </article>
      `
    )
    .join("");

const openOrderDetail = (order: Order) => {
  if (
    !orderDetailDialog ||
    !orderDetailStatus ||
    !orderDetailDate ||
    !orderDetailAddress ||
    !orderDetailPhone ||
    !orderDetailPayment ||
    !orderDetailNotesRow ||
    !orderDetailNotes ||
    !orderDetailProducts ||
    !orderDetailSubtotal ||
    !orderDetailTotal ||
    !orderDetailMessage ||
    !orderDetailMessageTitle ||
    !orderDetailMessageBody
  ) {
    return;
  }

  const statusMeta = getOrderStatusMeta(order.estado);
  const subtotal = getOrderSubtotal(order);

  orderDetailStatus.className = `mx-auto inline-flex items-center gap-2 rounded-full px-6 py-3 text-[14px] font-bold uppercase tracking-wide ${statusMeta.badge}`;
  orderDetailStatus.innerHTML = `
    <span class="h-2.5 w-2.5 rounded-full bg-current"></span>
    <span>${order.estado}</spa>
  `;
  orderDetailDate.textContent = `📅 ${formatDate(order.fecha)}`;
  orderDetailAddress.textContent = order.direccion;
  orderDetailPhone.textContent = order.telefono;
  orderDetailPayment.textContent = order.formaPago;

  if (order.notaAdicional && order.notaAdicional.trim()) {
    orderDetailNotesRow.classList.remove("hidden");
    orderDetailNotes.textContent = order.notaAdicional;
  } else {
    orderDetailNotesRow.classList.add("hidden");
    orderDetailNotes.textContent = "";
  }

  orderDetailProducts.innerHTML = renderOrderProducts(order);
  orderDetailSubtotal.textContent = formatCurrency(subtotal);
  orderDetailTotal.textContent = formatCurrency(order.total);
  orderDetailMessage.className = `mt-8 rounded-2xl border px-5 py-4 shadow-[0_4px_14px_rgba(0,0,0,0.04)] ${statusMeta.panel}`;
  orderDetailMessageTitle.textContent = statusMeta.messageTitle;
  orderDetailMessageBody.textContent = statusMeta.messageBody;

  orderDetailDialog.showModal();
};

const renderOrders = () => {
  if (!ordersList || !ordersCount || !ordersStatus) {
    return;
  }

  ordersCount.textContent = `${loadedOrders.length} pedido${loadedOrders.length === 1 ? "" : "s"}`;
  ordersStatus.textContent = "";

  if (loadedOrders.length === 0) {
    ordersList.innerHTML = renderEmptyState("Todavía no hiciste pedidos");
    return;
  }

  ordersList.innerHTML = loadedOrders.map(renderOrderCard).join("");
};

const renderOrdersError = (message: string) => {
  if (!ordersList || !ordersCount || !ordersStatus) {
    return;
  }

  ordersCount.textContent = "0 pedidos";
  ordersStatus.textContent = message;
  ordersStatus.className = "mt-4 text-[13px] text-danger";
  ordersList.innerHTML = renderEmptyState(message, true);
};

const setLoadingState = (message: string) => {
  if (!ordersStatus || !ordersList) {
    return;
  }

  ordersStatus.textContent = message;
  ordersStatus.className = "mt-4 text-[13px] text-[#8a8a8a]";
  ordersList.innerHTML = "";
};

const loadOrders = async () => {
  if (!user?.id) {
    renderOrdersError("No se pudo identificar el usuario");
    return;
  }

  setLoadingState("Cargando tus pedidos...");

  try {
    const response = await fetch(`${API_BASE_URL}/pedidos/usuario/${user.id}`);

    if (response.status === 404) {
      const errorResponse = (await response.json()) as Partial<ErrorResponse>;
      renderOrdersError(errorResponse.message ?? "Recurso no encontrado");
      return;
    }

    if (!response.ok) {
      renderOrdersError("No se pudieron cargar tus pedidos");
      return;
    }

    loadedOrders = (await response.json()) as Order[];
    renderOrders();
  } catch {
    renderOrdersError("No se pudieron cargar tus pedidos");
  }
};

if (user) {
  const userName = document.getElementById("user-name") as HTMLSpanElement;
  userName.textContent = `${user.nombre} ${user.apellido}`;

  if (cartCount) {
    cartCount.textContent = String(getCartCount());
  }
}

logoutButton.addEventListener("click", logOut);

ordersList?.addEventListener("click", (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  const button = target.closest<HTMLButtonElement>("button[data-order-id]");

  if (!button) {
    return;
  }

  const orderId = Number(button.dataset.orderId);

  if (Number.isNaN(orderId)) {
    return;
  }

  const order = loadedOrders.find((item) => item.id === orderId);

  if (order) {
    openOrderDetail(order);
  }
});

closeOrderDetailButton?.addEventListener("click", () => {
  orderDetailDialog?.close();
});

orderDetailDialog?.addEventListener("click", (event: MouseEvent) => {
  if (event.target === orderDetailDialog) {
    orderDetailDialog.close();
  }
});

loadOrders();
