export const FormasPago = {
  TARJETA: "TARJETA",
  TRANSFERENCIA: "TRANSFERENCIA",
  EFECTIVO: "EFECTIVO"
} as const;

export type FormasPago = (typeof FormasPago)[keyof typeof FormasPago];
