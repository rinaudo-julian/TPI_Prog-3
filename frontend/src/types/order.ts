import type { FormasPago } from "./formasPago";
import type { Product } from "./product";

export type OrderStatus = "PENDIENTE" | "CONFIRMADO" | "TERMINADO" | "CANCELADO";

export interface OrderDetail {
  id: number;
  cantidad: number;
  subtotal: number;
  producto: Product;
}

export interface Order {
  id: number;
  fecha: string;
  estado: OrderStatus;
  total: number;
  telefono: string;
  direccion: string;
  notaAdicional: string | null;
  formaPago: FormasPago;
  idUsuario: number;
  detalles: OrderDetail[];
}
