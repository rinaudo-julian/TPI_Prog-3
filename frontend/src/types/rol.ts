export const Rol = {
  ADMIN: "ADMIN",
  USUARIO: "USUARIO"
} as const;

export type Rol = (typeof Rol)[keyof typeof Rol];
