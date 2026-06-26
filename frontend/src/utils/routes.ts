const ROUTES = {
  home: "/src/pages/store/home/home.html",
  login: "/src/pages/auth/login/login.html",
  register: "/src/pages/auth/register/register.html",
  cart: "/src/pages/store/cart/cart.html",
  productDetail: "/src/pages/store/productDetail/productDetail.html",
  clientOrders: "/src/pages/client/orders/clientOrders.html",
  adminHome: "/src/pages/admin/adminHome/adminHome.html",
  adminCategories: "/src/pages/admin/categorias/categorias.html",
  adminOrders: "/src/pages/admin/pedidos/pedidos.html"
} as const;

type RouteName = keyof typeof ROUTES;

export const navigate = (viewName: RouteName) => {
  window.location.href = ROUTES[viewName];
};

export const replace = (viewName: RouteName) => {
  window.location.replace(ROUTES[viewName]);
};
