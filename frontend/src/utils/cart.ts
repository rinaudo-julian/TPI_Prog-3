import type { Product } from "../types/product";
import type { CartItem } from "../types/cart";

const CART_STORAGE_KEY = "cart";

export const getCart = (): CartItem[] => {
  const rawCart = localStorage.getItem(CART_STORAGE_KEY);

  if (!rawCart) {
    return [];
  }

  try {
    const parsedCart = JSON.parse(rawCart) as CartItem[];

    return Array.isArray(parsedCart) ? parsedCart : [];
  } catch {
    return [];
  }
};

export const saveCart = (cart: CartItem[]) => {
  localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
};

export const addProductToCart = (product: Product, quantity: number) => {
  const cart = getCart();
  const existingProduct = cart.find((item) => item.id === product.id);
  const safeQuantity = Math.max(1, quantity);

  if (existingProduct) {
    const remainingStock = Math.max(0, product.stock - existingProduct.quantity);

    if (remainingStock === 0) {
      return cart;
    }

    existingProduct.quantity += Math.min(safeQuantity, remainingStock);
  } else {
    cart.push({ ...product, quantity: Math.min(safeQuantity, product.stock) });
  }

  saveCart(cart);

  return cart;
};

export const incrementCartItem = (productId: number) => {
  const cart = getCart();
  const existingProduct = cart.find((item) => item.id === productId);

  if (!existingProduct) {
    return cart;
  }

  if (existingProduct.quantity >= existingProduct.stock) {
    return cart;
  }

  existingProduct.quantity += 1;
  saveCart(cart);

  return cart;
};

export const decrementCartItem = (productId: number) => {
  const cart = getCart();
  const existingProductIndex = cart.findIndex((item) => item.id === productId);

  if (existingProductIndex === -1) {
    return cart;
  }

  const existingProduct = cart[existingProductIndex];

  if (existingProduct.quantity <= 1) {
    cart.splice(existingProductIndex, 1);
  } else {
    existingProduct.quantity -= 1;
  }

  saveCart(cart);

  return cart;
};

export const removeCartItem = (productId: number) => {
  const cart = getCart().filter((item) => item.id !== productId);

  saveCart(cart);

  return cart;
};

export const clearCart = () => {
  saveCart([]);
};

export const getCartCount = () =>
  getCart().reduce((total, item) => total + item.quantity, 0);

export const getCartSubtotal = () =>
  getCart().reduce((total, item) => total + item.precio * item.quantity, 0);

export const getCartTotal = () => getCartSubtotal();
